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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

/**
 * {@link Converter} for {@link Date}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class DateConverter implements Converter<Date> {

	// Fields
	private final DateFormat dateFormat;

	public DateConverter() {
		this.dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
	}

	public DateConverter(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Date convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		try {
			return dateFormat.parse(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse date: " + e.getMessage());
		}
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Date.class.isAssignableFrom(requiredType);
	}
}