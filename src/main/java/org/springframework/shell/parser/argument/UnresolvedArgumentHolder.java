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
 * Represents an {@link ArgumentHolder} that has not been fully resolved (e.g. it might require parsing or interactive
 * input from the user).
 * 
 * @author Camilo Gonzalez
 * @since 1.2.1
 */
public class UnresolvedArgumentHolder implements ArgumentHolder {

	private final Class<?> expectedType;

	private final String inputValue;

	private final CliOption cliOption;

	private final String sourcedFrom;

	/**
	 * Constructs a new {@link UnresolvedArgumentHolder} to resolve the argument object for the given String value
	 * 
	 * @param expectedType
	 *            the expected type this object needs to return
	 * @param cliOption
	 *            the configuration of the argument
	 * @param sourcedFrom
	 *            the option key used to retrieve this value
	 * @param inputValue
	 *            the String value to parse
	 */
	public UnresolvedArgumentHolder(Class<?> expectedType, CliOption cliOption, String sourcedFrom, String inputValue) {
		this.expectedType = expectedType;
		this.cliOption = cliOption;
		this.sourcedFrom = sourcedFrom;
		this.inputValue = inputValue;
	}

	public UnresolvedArgumentHolder(ArgumentHolder baseArgument, String inputValue) {
		this(baseArgument.getExpectedType(), baseArgument.getCliOption(), baseArgument.getSourcedFrom(), inputValue);
	}
	
	@Override
	public Class<?> getExpectedType() {
		return expectedType;
	}

	@Override
	public String getInputValue() {
		return inputValue;
	}

	@Override
	public CliOption getCliOption() {
		return cliOption;
	}

	@Override
	public String getSourcedFrom() {
		return sourcedFrom;
	}

	@Override
	public ArgumentValue getArgumentValue() {
		return new ArgumentValue();
	}

}
