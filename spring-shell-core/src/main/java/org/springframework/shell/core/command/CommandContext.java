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

import org.jspecify.annotations.Nullable;
import org.springframework.shell.core.InputReader;

import java.io.PrintWriter;
import java.util.function.Predicate;

/**
 * Interface containing runtime information about the current command invocation.
 *
 * @author Mahmoud Ben Hassine
 * @author David Pilar
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
		CommandOption option = getOptionByLongName(optionName);
		if (option == null && optionName.length() == 1) {
			option = getOptionByShortName(optionName.charAt(0));
		}
		return option;
	}

	/**
	 * Retrieve a command option by its long name.
	 * @param longName the long name of the option to retrieve
	 * @return the matching {@link CommandOption} or null if not found
	 */
	@Nullable public CommandOption getOptionByLongName(String longName) {
		return getOptionByPredicate(option -> longName.equals(option.longName()));
	}

	/**
	 * Retrieve a command option by its short name.
	 * @param shortName the short name of the option to retrieve
	 * @return the matching {@link CommandOption} or null if not found
	 */
	@Nullable public CommandOption getOptionByShortName(char shortName) {
		return getOptionByPredicate(option -> option.shortName() == shortName);
	}

	/**
	 * Retrieve a command option by a custom predicate.
	 * @param predicate the predicate to filter options
	 * @return the matching {@link CommandOption} or {@code null} if not found
	 */
	@Nullable private CommandOption getOptionByPredicate(Predicate<CommandOption> predicate) {
		return this.parsedInput.options().stream().filter(predicate).findFirst().orElse(null);
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
