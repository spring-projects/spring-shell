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

/**
 * {@link Converter} for {@link Boolean}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class BooleanConverter implements Converter<Boolean> {

	public Boolean convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		if ("true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value) || "0".equals(value) || "no".equalsIgnoreCase(value)) {
			return false;
		} else {
			throw new IllegalArgumentException("Cannot convert " + value + " to type Boolean.");
		}
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		completions.add(new Completion("true"));
		completions.add(new Completion("false"));
		completions.add(new Completion("yes"));
		completions.add(new Completion("no"));
		completions.add(new Completion("1"));
		completions.add(new Completion("0"));
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Boolean.class.isAssignableFrom(requiredType) || boolean.class.isAssignableFrom(requiredType);
	}
}