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

import java.io.IOException;
import java.util.Set;

import org.springframework.shell.core.Converter;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.StringUtils;

import jline.console.ConsoleReader;

/**
 * Represents an {@link ArgumentResolver} that will convert a String value and transform it into an appropriate object
 * of the expected type and will request the value interactively if no value has already been provided.
 * 
 * @author Camilo Gonzalez
 * @since 1.2.1
 */
public class InteractiveArgumentResolver extends StringParserArgumentResolver {

	/**
	 * Constructs a new {@link InteractiveArgumentResolver} to resolve the argument object for the given interactive
	 * {@link CliOption}
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
	public InteractiveArgumentResolver(Class<?> expectedClass, CliOption cliOption, String sourcedFrom, String value,
			Set<Converter<?>> converters) {
		super(expectedClass, cliOption, sourcedFrom, value, converters);
		if (!cliOption.interactive()) {
			throw new IllegalArgumentException(
					"InteractiveArgumentResolver is only intended for use with interactive arguments");
		}
	}

	@Override
	protected String getValue(ConsoleReader consoleReader) {
		final String valueProvided = super.getValue(consoleReader);
		// check if the value has already been provided via the command options
		if (!StringUtils.isEmpty(valueProvided)) {
			return valueProvided;
		}

		// otherwise, try to get it from the command line
		// first, define the prompt to be used:
		final String key;
		if (sourcedFrom == null) {
			key = cliOption.key()[0];
		} else {
			key = sourcedFrom;
		}
		final String prompt = key + "> ";

		// second, request the value
		final String value;
		try {
			boolean isMasked = cliOption.masked();
			if (isMasked) {
				value = consoleReader.readLine(prompt, '*');
			} else {
				value = consoleReader.readLine(prompt);
			}

			boolean isMandatory = cliOption.mandatory();
			if (isMandatory && StringUtils.isEmpty(value)) {
				StringBuilder errorMessage = new StringBuilder("A value for ");
				errorMessage.append(key);
				errorMessage.append(" is mandatory");
				throw new IllegalArgumentException(errorMessage.toString());
			}

			return value;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
