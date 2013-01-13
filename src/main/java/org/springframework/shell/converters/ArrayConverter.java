/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.converters;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.shell.core.ObjectResolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.lang.reflect.Method;

/**
 * {@link Converter} for arrays.
 * Requires Java 6.
 *
 * @author Joern Huxhorn
 * @since 1.1.0
 */
public class ArrayConverter<T> implements Converter<T[]> {
	
	public static final String CASE_SENSITIVE = "case-sensitive";
	public static final String UNIQUE_VALUES = "unique-values";
	
	private static final Method NEW_INSTANCE_METHOD;
	
	static {
		try {
			Class<?> clazz = Class.forName("java.lang.reflect.Array");
			NEW_INSTANCE_METHOD = clazz.getMethod("newInstance", Class.class, int.class);
		}
		catch(Throwable t) {
			throw new Error("ArrayConverter requires Java 6 or higher.", t);
		}
	}
	
	@SuppressWarnings("unchecked") // <= can't prevent this
	private static <C> C[] newArrayInstance(Class<C> clazz, int size) {
		try {
			return (C[]) NEW_INSTANCE_METHOD.invoke(null, clazz, size);
		}
		catch(Throwable t) {
			throw new Error("Failed to create array!", t);
		}
	}
	
	private ObjectResolver<T> objectResolver;
	private Class<?> arrayClass;
	
	protected ArrayConverter(ObjectResolver<T> objectResolver) {
		if(objectResolver == null) {
			throw new IllegalArgumentException("objectResolver must not be null!");
		}
		this.objectResolver=objectResolver;
		// arrayClass is required for supports method
		this.arrayClass = newArrayInstance(objectResolver.getTargetClass(),0).getClass();
	}
	
	public T[] convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		final boolean caseSensitive = optionContext != null && optionContext.contains(CASE_SENSITIVE);
		final boolean uniqueValues = optionContext != null && optionContext.contains(UNIQUE_VALUES);

		if(value == null) {
			return null;
		}

		Collection<String> validValues = objectResolver.getAllValidValues();

		List<T> result = new ArrayList<T>();
		List<String> input=parseInput(value);
		Set<String> usedIds = new HashSet<String>();
		for(String current : input) {
			String id = current;
			if(!caseSensitive) {
				id = ObjectConverter.matches(current, validValues);
			}
			if(id == null) {
				return null;
			}
			T currentFoo = objectResolver.resolveObject(id);
			if(currentFoo == null) {
				return null;
			}
			if(uniqueValues && usedIds.contains(id)) {
				// we have a duplicate id, ignore it
				continue;
			}
			result.add(currentFoo);
			usedIds.add(id);
		}
		int count = result.size();
		if(count == 0) {
			return null;
		}
		T[] resultArray = newArrayInstance(objectResolver.getTargetClass(),count);
		for(int i=0;i<result.size();i++) {
			resultArray[i] = result.get(i);
		}
		return resultArray;
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		final boolean caseSensitive = optionContext != null && optionContext.contains(CASE_SENSITIVE);
		final boolean uniqueValues = optionContext != null && optionContext.contains(UNIQUE_VALUES);

		Collection<String> validValues = objectResolver.getAllValidValues();
		
		List<String> original=parseInput(existingData);
		List<String> input=parseInput(original, validValues, caseSensitive, uniqueValues);
		if(input == null) {
			// there was some invalid values so we can't complete
			return false;
		}
		int inputSize = input.size();
		Set<String> usedIds = new HashSet<String>();
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<inputSize-1;i++) {
			String current = input.get(i);
			usedIds.add(current);
			builder.append(current);
			builder.append(',');
		}
		String prefix = builder.toString();
		String lastOne = "";
		if(inputSize>0) {
			lastOne = input.get(inputSize-1);
		}
		String upperLastOne = lastOne.toUpperCase();
		for(String current : validValues) {
			String upperCurrent = current.toUpperCase();
			if ("".equals(lastOne) 
				|| current.startsWith(lastOne) || lastOne.startsWith(current)
				|| (!caseSensitive && (upperCurrent.startsWith(upperLastOne) || upperLastOne.startsWith(upperCurrent)))) {
				if(!uniqueValues || !usedIds.contains(current)) {
					completions.add(new Completion(prefix+current));
				}
			}
		}
		return false;
	}

	
	private static List<String> parseInput(String input) {
		List<String> result = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(input,",",true);
		boolean endsWithComma=false;
		while(tok.hasMoreTokens()) {
			String current = tok.nextToken();
			current = current.trim();
			if(",".equals(current)) {
				endsWithComma = true;
				continue;
			}
			endsWithComma = false;
			if("".equals(current)) {
				continue;
			}
			result.add(current);
		};
		if(endsWithComma) {
			result.add("");
		}
		return result;		
	}

	private static List<String> parseInput(List<String> original, Collection<String> validValues, boolean caseSensitive, boolean uniqueValues) {
		Set<String> usedIds = new HashSet<String>();
		
		int originalSize = original.size();
		List<String> result = new ArrayList<String>(originalSize);
		
		for(int i = 0; i < originalSize ; i++) {
			String current = original.get(i);
			if("".equals(current)) {
				result.add(current);
				continue;
			}
			
			if(i != originalSize-1) {
				if(!caseSensitive) {
					current = ObjectConverter.matches(current, validValues);
				} else {
					if(!validValues.contains(current)) {
						current = null;
					}
				}
			} else {
				// last element special handling
				// accept invalid, i.e. incomplete, values
				if(!caseSensitive) {
					String corrected = ObjectConverter.matches(current, validValues);
					if(corrected != null) {
						// ... but correct it anyway if it is complete
						current = corrected;
					}
				}
			}
			if(current == null) {
				return null; // there was some invalid value
			}
			if(uniqueValues && usedIds.contains(current)) {
				continue;
			}
			result.add(current);
			usedIds.add(current);
		}
		return result;		
	}
		
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return arrayClass.isAssignableFrom(requiredType);
	}
}
