/*
 * Copyright 2011-2017 the original author or authors.
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
package org.springframework.shell.parser.argument;

import java.util.Set;
import java.util.logging.Logger;

import org.springframework.shell.core.Converter;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.util.StringUtils;

import jline.console.ConsoleReader;

/**
 * Represents an {@link ArgumentResolver} that will convert a String value and transform it into an appropriate object
 * of the expected type.
 * 
 * @author Camilo Gonzalez
 * @since 1.2.1
 */
public class StringParserArgumentResolver implements ArgumentResolver {

	private static final Logger LOGGER = HandlerUtils.getLogger(StringParserArgumentResolver.class);

	private final Class<?> expectedClass;

	private final String defaultValue;

	private final Set<Converter<?>> converters;

	protected final CliOption cliOption;

	protected final String sourcedFrom;

	/**
	 * Constructs a new {@link StringParserArgumentResolver} to resolve the argument object for the given String value
	 * 
	 * @param expectedClass
	 *            the expected class this object needs to return
	 * @param cliOption
	 *            the configuration of the argument
	 * @param sourcedFrom
	 *            the option key used to retrieve this value
	 * @param value
	 *            the String value to parse
	 * @param converters
	 *            the available converters to transform the String into the required type
	 */
	public StringParserArgumentResolver(Class<?> expectedClass, CliOption cliOption, String sourcedFrom, String value,
			Set<Converter<?>> converters) {
		this.expectedClass = expectedClass;
		this.cliOption = cliOption;
		this.sourcedFrom = sourcedFrom;
		this.defaultValue = value;
		this.converters = converters;
	}

	@Override
	public Object getArgumentValue(ConsoleReader consoleReader) {
		final String value = getValue(consoleReader);

		try {
			Object result;
			Converter<?> c = null;
			for (Converter<?> candidate : converters) {
				if (candidate.supports(expectedClass, cliOption.optionContext())) {
					// Found a usable converter
					c = candidate;
					break;
				}
			}
			if (c == null) {
				throw new IllegalStateException("TODO: Add basic type conversion");
				// TODO Fall back to a normal SimpleTypeConverter and attempt conversion
				// SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();
				// result = simpleTypeConverter.convertIfNecessary(value, requiredType, mp);
			}

			// Use the converter
			result = c.convertFromText(value, expectedClass, cliOption.optionContext());

			// If the option has been specified to be mandatory then the result should never be null
			if (result == null && cliOption.mandatory()) {
				throw new IllegalStateException("Expected value for option: " + cliOption.key()[0]);
			}
			return result;
		} catch (RuntimeException e) {
			LOGGER.warning(e.getClass().getName() + ": Failed to convert '" + value + "' to type "
					+ expectedClass.getSimpleName() + " for option '"
					+ StringUtils.arrayToCommaDelimitedString(cliOption.key()) + "'");
			if (StringUtils.hasText(e.getMessage())) {
				LOGGER.warning(e.getMessage());
			}
			return null;
		}
	}

	protected String getValue(ConsoleReader consoleReader) {
		return defaultValue;
	}
}
