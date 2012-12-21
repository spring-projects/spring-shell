/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.converters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A simple {@link Converter} for those classes which provide public static fields to represent possible
 * textual values.
 *
 * @author Stefan Schmidt
 * @author Ben Alex
 * @since 1.0
 */
public class StaticFieldConverterImpl implements StaticFieldConverter {

	// Fields
	private final Map<Class<?>,Map<String,Field>> fields = new HashMap<Class<?>,Map<String,Field>>();

	public void add(final Class<?> clazz) {
		Assert.notNull(clazz, "A class to provide conversion services is required");
		Assert.isNull(fields.get(clazz), "Class '" + clazz + "' is already registered for completion services");
		Map<String,Field> ffields = new HashMap<String, Field>();
		for (Field field : clazz.getFields()) {
			int modifier = field.getModifiers();
			if (Modifier.isStatic(modifier) && Modifier.isPublic(modifier)) {
				ffields.put(field.getName(), field);
			}
		}
		Assert.notEmpty(ffields, "Zero public static fields accessible in '" + clazz + "'");
		fields.put(clazz, ffields);
	}

	public void remove(final Class<?> clazz) {
		Assert.notNull(clazz, "A class that was providing conversion services is required");
		fields.remove(clazz);
	}

	public Object convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		Map<String,Field> ffields = fields.get(requiredType);
		if (ffields == null) {
			return null;
		}
		Field f = ffields.get(value);
		if (f == null) {
			// Fallback to case insensitive search
			for (Field candidate : ffields.values()) {
				if (candidate.getName().equalsIgnoreCase(value)) {
					f = candidate;
					break;
				}
			}
			if (f == null) {
				// Still not found, despite a case-insensitive search
				return null;
			}
		}
		try {
			return f.get(null);
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to acquire field '" + value + "' from '" + requiredType.getName() + "'", ex);
		}
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		Map<String,Field> ffields = fields.get(requiredType);
		if (ffields == null) {
			return true;
		}
		for (String field : ffields.keySet()) {
			completions.add(new Completion(field));
		}
		return true;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return fields.get(requiredType) != null;
	}
}
