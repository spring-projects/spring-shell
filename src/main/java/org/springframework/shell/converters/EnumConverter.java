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

import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

/**
 * {@link Converter} for {@link Enum}.
 * 
 * @author Ben Alex
 * @author Alan Stewart
 * @since 1.0
 */
@SuppressWarnings("all")
@Component
public class EnumConverter implements Converter<Enum<?>> {

	@Override
	@SuppressWarnings("unchecked")
	public Enum<?> convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		if (!Enum.class.isAssignableFrom(requiredType)) {
			return null;
		}
		Class<Enum> enumClass = (Class<Enum>) requiredType;
		return Enum.valueOf(enumClass, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType,
			final String existingData, final String optionContext, final MethodTarget target) {
		if (!Enum.class.isAssignableFrom(requiredType)) {
			return false;
		}
		Class<Enum> enumClass = (Class<Enum>) requiredType;
		for (Enum<?> enumValue : enumClass.getEnumConstants()) {
			String candidate = enumValue.name();
			if ("".equals(existingData) || candidate.startsWith(existingData) || existingData.startsWith(candidate)
					|| candidate.toUpperCase().startsWith(existingData.toUpperCase())
					|| existingData.toUpperCase().startsWith(candidate.toUpperCase())) {
				completions.add(new Completion(candidate));
			}
		}
		return true;
	}

	@Override
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Enum.class.isAssignableFrom(requiredType);
	}
}