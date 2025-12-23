/*
 * Copyright 2022-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core.command;

import java.io.PrintWriter;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.InputReader;

/**
 * Interface containing runtime information about the current command invocation.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public record CommandContext(ParsedInput parsedInput, CommandRegistry commandRegistry, PrintWriter outputWriter,
		InputReader inputReader) {

	/**
	 * Retrieve a command option by its name (long or short).
	 * @param optionName the name of the option to retrieve
	 * @return the matching {@link CommandOption} or null if not found
	 */
	@Nullable public CommandOption getOptionByName(String optionName) {
		return this.parsedInput.options()
			.stream()
			.filter(option -> option.longName().equals(optionName) || option.shortName() == optionName.charAt(0))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Retrieve a command argument by its index.
	 * @param index the index of the argument to retrieve
	 * @return the matching {@link CommandArgument}
	 */
	@Nullable public CommandArgument getArgumentByIndex(int index) {
		return this.parsedInput.arguments()
			.stream()
			.filter(argument -> argument.index() == index)
			.findAny()
			.orElse(null);
	}
}
