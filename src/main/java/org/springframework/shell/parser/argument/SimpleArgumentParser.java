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
import org.springframework.shell.parser.SimpleParser;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.util.StringUtils;

/**
 * Implementation of an {@link ArgumentParser} to be used by a {@link SimpleParser}
 * 
 * @author Camilo Gonzalez
 * @since 1.2.1
 */
public class SimpleArgumentParser implements ArgumentParser {

	private static final Logger LOGGER = HandlerUtils.getLogger(ArgumentParser.class);

	private final Set<Converter<?>> converters;

	public SimpleArgumentParser(Set<Converter<?>> converters) {
		this.converters = converters;
	}

	@Override
	public Object parseArgument(ArgumentHolder argumentHolder) {
		final Class<?> expectedType = argumentHolder.getExpectedType();
		final CliOption cliOption = argumentHolder.getCliOption();
		final String value = argumentHolder.getInputValue();

		try {
			Object result;
			Converter<?> c = null;
			for (Converter<?> candidate : converters) {
				if (candidate.supports(expectedType, cliOption.optionContext())) {
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
			result = c.convertFromText(value, expectedType, cliOption.optionContext());

			// If the option has been specified to be mandatory then the result should never be null
			if (result == null && cliOption.mandatory()) {
				throw new IllegalStateException("Expected value for option: " + cliOption.key()[0]);
			}
			return result;
		} catch (RuntimeException e) {
			LOGGER.warning(e.getClass().getName() + ": Failed to convert '" + value + "' to type "
					+ expectedType.getSimpleName() + " for option '"
					+ StringUtils.arrayToCommaDelimitedString(cliOption.key()) + "'");
			if (StringUtils.hasText(e.getMessage())) {
				LOGGER.warning(e.getMessage());
			}
			return null;
		}
	}
}
