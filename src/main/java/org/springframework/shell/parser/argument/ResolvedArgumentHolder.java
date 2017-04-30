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

import org.springframework.shell.core.annotation.CliOption;

/**
 * Represents an {@link ArgumentHolder} that has already been resolved and doesn't need further processing.
 * 
 * @author Camilo Gonzalez
 * @since 1.2.1
 */
public class ResolvedArgumentHolder extends UnresolvedArgumentHolder {

	private final Object resolvedValue;

	/**
	 * Constructs a new {@link ResolvedArgumentHolder} with an argument value that has already been resolved
	 * 
	 * @param expectedType
	 *            the expected type this object needs to return
	 * @param cliOption
	 *            the configuration of the argument
	 * @param sourcedFrom
	 *            the option key used to retrieve this value (can be null, e.g. for system provided values)
	 * @param inputValue
	 *            the String value to parse (can be null, e.g. for system provided values)
	 * @param resolvedValue
	 *            the value to be used as the argument value (as in, this is the resolved object value)
	 */
	public ResolvedArgumentHolder(Class<?> expectedType, CliOption cliOption, String sourcedFrom, String inputValue,
			Object resolvedValue) {
		super(expectedType, cliOption, sourcedFrom, inputValue);

		if (resolvedValue != null && !expectedType.isInstance(resolvedValue)) {
			throw new IllegalArgumentException(
					"The provided value isn't assignable to class: " + expectedType.getName());
		}

		this.resolvedValue = resolvedValue;
	}

	public ResolvedArgumentHolder(ArgumentHolder baseArgument, Object resolvedValue) {
		this(baseArgument.getExpectedType(), baseArgument.getCliOption(), baseArgument.getSourcedFrom(),
				baseArgument.getInputValue(), resolvedValue);
	}

	@Override
	public ArgumentValue getArgumentValue() {
		return new ArgumentValue(resolvedValue);
	}

}