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
import java.util.Collection;
import java.util.List;


/**
 * {@link Converter} for specific objects using {@link ObjectResolver}.
 *
 * @author Joern Huxhorn
 * @since 1.1.0
 */
public class ObjectConverter<T> implements Converter<T> {
	
	public static final String CASE_SENSITIVE = "case-sensitive";
	
	private ObjectResolver<T> objectResolver;
	
	protected ObjectConverter(ObjectResolver<T> objectResolver) {
		if(objectResolver == null) {
			throw new IllegalArgumentException("objectResolver must not be null!");
		}
		this.objectResolver=objectResolver;
	}
	
	public T convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		if(value == null) {
			return null;
		}
		if(optionContext != null && optionContext.contains(CASE_SENSITIVE)) {
			return objectResolver.resolveObject(value);
		}
		Collection<String> validValues = objectResolver.getAllValidValues();
		String corrected = matches(value, validValues);
		if(corrected == null) {
			return null;
		}
		return objectResolver.resolveObject(corrected);
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		final boolean caseSensitive = optionContext != null && optionContext.contains(CASE_SENSITIVE);

		Collection<String> validValues = objectResolver.getAllValidValues();
		String upperData = existingData.toUpperCase();
		for(String current : validValues) {
			String upperCurrent = current.toUpperCase();
			if ("".equals(existingData) 
				|| current.startsWith(existingData) || existingData.startsWith(current)
				|| (!caseSensitive && (upperCurrent.startsWith(upperData) || upperData.startsWith(upperCurrent)))) {
				completions.add(new Completion(current));
			}
		}
		return true;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return objectResolver.getTargetClass().isAssignableFrom(requiredType);
	}

	static String matches(String inputValue, Collection<String> validValues) {
		String upperInput = inputValue.toUpperCase();
		for(String current : validValues) {
			if(inputValue.equalsIgnoreCase(current)) {
				return current;
			}
		}
		return null;
	}
}
