/*
 * Copyright 2025-present the original author or authors.
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

import java.util.List;

import org.springframework.shell.core.Input;

/**
 * Default implementation of {@link CommandParser}. Supports options in the form -o=value
 * and --option=value. Options and arguments can be specified in any order. Arguments are
 * 0-based indexed among other arguments. <pre>
 * CommandSyntax ::= CommandName (Option | Argument)*
 * CommandName   ::= String
 * Option        ::= ShortOption | LongOption
 * ShortOption   ::= '-' Char ('=' String)?
 * LongOption    ::= '--' String ('=' String)?
 * Argument      ::= String
 *
 * Example: mycommand --option1=value1 arg1 -o2=value2 arg2
 * </pre>
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
// TODO add support for subcommands
public class DefaultCommandParser implements CommandParser {

	@Override
	public ParsedInput parse(Input input) {
		List<String> words = input.words();

		// the first word is the command name
		String commandName = words.get(0);
		ParsedInput.Builder parsedInputBuilder = ParsedInput.builder().commandName(commandName);
		if (words.size() == 1) {
			return parsedInputBuilder.build();
		}

		List<String> remainingWords = words.subList(1, words.size());
		// parse options
		List<String> options = remainingWords.stream().filter(this::isOption).toList();
		for (String option : options) {
			CommandOption commandOption = parseOption(option);
			parsedInputBuilder.addOption(commandOption);
		}
		// parse arguments
		List<String> arguments = remainingWords.stream().filter(word -> !isOption(word)).toList();
		for (int i = 0; i < arguments.size(); i++) {
			CommandArgument commandArgument = parseArgument(i, arguments.get(i));
			parsedInputBuilder.addArgument(commandArgument);
		}
		return parsedInputBuilder.build();
	}

	private boolean isOption(String word) {
		return word.startsWith("-") || word.startsWith("--");
	}

	private CommandOption parseOption(String word) {
		char shortName = ' ';
		String longName = "";
		String value = "";
		if (word.startsWith("--")) {
			word = word.substring(2);
			String[] tokens = word.split("=");
			longName = tokens[0];
			if (tokens.length > 1) {
				value = tokens[1];
			}
		}
		else if (word.startsWith("-")) {
			word = word.substring(1);
			String[] tokens = word.split("=");
			shortName = tokens[0].charAt(0);
			if (tokens.length > 1) {
				value = tokens[1];
			}
		}
		return new CommandOption(shortName, longName, value);
	}

	private CommandArgument parseArgument(int index, String word) {
		return new CommandArgument(index, word);
	}

}
