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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link CommandParser}. Supports options in the long form of
 * --key=value or --key value as well in the short form of -k=value or -k value. Options
 * and arguments can be specified in any order. Arguments are 0-based indexed among other
 * arguments. Option values and arguments can be quoted with double or single quotes to
 * include whitespace, and quote characters can be escaped with a backslash inside quoted
 * values (e.g. greet "she said \" and left"). Unbalanced quotes are rejected with an
 * {@link IllegalArgumentException}. <pre>
 * CommandSyntax  ::= CommandName [SubCommandName]* [Option | Argument]*
 * CommandName    ::= String
 * SubCommandName ::= String
 * Option         ::= ShortOption | LongOption
 * ShortOption    ::= '-' Char ['='|' ']? String
 * LongOption     ::= '--' String ['='|' ']? String
 * Argument       ::= String
 *
 * Example: mycommand mysubcommand --optionA=value1 arg1 -b=value2 arg2 --optionC value3 -d value4
 * </pre>
 *
 * @author Mahmoud Ben Hassine
 * @author David Pilar
 * @since 4.0.0
 */
public class DefaultCommandParser implements CommandParser {

	private static final Log log = LogFactory.getLog(DefaultCommandParser.class);

	private final CommandRegistry commandRegistry;

	public DefaultCommandParser(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Override
	public ParsedInput parse(String input) {
		log.debug("Parsing input: " + input);
		List<String> words = tokenize(input);
		if (words.isEmpty()) {
			words = List.of("");
		}

		// the first word is the (root) command name
		String commandName = words.get(0);
		ParsedInput.Builder parsedInputBuilder = ParsedInput.builder().commandName(commandName);
		if (words.size() == 1) {
			ParsedInput parsedInput = parsedInputBuilder.build();
			log.debug("Parsed input: " + parsedInput);
			return parsedInput;
		}

		if (words.size() == 2 && (words.get(1).equals("--help") || words.get(1).equals("-h"))) {
			parsedInputBuilder.addOption(CommandOption.with().shortName('h').longName("help").value("true").build());
			ParsedInput parsedInput = parsedInputBuilder.build();
			log.debug("Parsed input: " + parsedInput);
			return parsedInput;
		}

		List<String> remainingWords = words.subList(1, words.size());

		int firstNonCommandWordIndex = 1;
		String prefix = commandName;
		for (String remainingWord : remainingWords) {
			String potentialCommandName = prefix + " " + remainingWord;
			List<Command> commands = commandRegistry.getCommandsByPrefix(potentialCommandName);
			if (commands.isEmpty()) {
				break;
			}
			prefix = potentialCommandName;
			firstNonCommandWordIndex++;
		}
		List<String> subCommands = firstNonCommandWordIndex == 1 ? Collections.emptyList()
				: words.subList(1, firstNonCommandWordIndex);
		String fullCommandName = firstNonCommandWordIndex == 1 ? commandName : prefix;

		// add command and sub commands
		for (String subCommand : subCommands) {
			parsedInputBuilder.addSubCommand(subCommand);
		}

		// parse options and arguments
		List<String> optionsAndArguments = words.subList(firstNonCommandWordIndex, words.size());
		if (!optionsAndArguments.isEmpty() && isArgumentSeparator(optionsAndArguments.get(0))) {
			optionsAndArguments = optionsAndArguments.subList(1, optionsAndArguments.size());
			int argumentIndex = 0;
			for (String remainingWord : optionsAndArguments) {
				CommandArgument commandArgument = parseArgument(argumentIndex++, remainingWord);
				parsedInputBuilder.addArgument(commandArgument);
			}
		}
		else { // parse remaining words as options and arguments
			int argumentIndex = 0;
			for (int i = 0; i < optionsAndArguments.size(); i++) {
				String currentWord = optionsAndArguments.get(i);
				String nextWord = i + 1 < optionsAndArguments.size() ? optionsAndArguments.get(i + 1) : null;
				if (isOption(currentWord)) {
					if (currentWord.contains("=")) {
						CommandOption commandOption = parseOption(currentWord);
						parsedInputBuilder.addOption(commandOption);
					}
					else { // use next word as option value
						if (nextWord == null || isOption(nextWord) || isArgumentSeparator(nextWord)) {
							if (!isBooleanOption(fullCommandName, currentWord)) {
								throw new IllegalArgumentException("Option '" + currentWord + "' requires a value");
							}
							nextWord = "true";
						}
						else if (isBooleanOption(fullCommandName, currentWord) && !isBooleanValue(nextWord)) {
							nextWord = "true";
						}
						else {
							i++; // skip next word as it was used as option value
						}
						CommandOption commandOption = parseOption(currentWord + "=" + nextWord);
						parsedInputBuilder.addOption(commandOption);
					}
				}
				else {
					CommandArgument commandArgument = parseArgument(argumentIndex++, currentWord);
					parsedInputBuilder.addArgument(commandArgument);
				}
			}
		}
		ParsedInput parsedInput = parsedInputBuilder.build();
		log.debug("Parsed input: " + parsedInput);
		return parsedInput;
	}

	/**
	 * Split the input into words on whitespace, keeping quoted sections together. This is
	 * a character-by-character state machine tracking the currently open quote (none,
	 * double or single). A backslash makes the following character part of the current
	 * word without any special meaning, so an escaped quote neither opens nor closes a
	 * quoted section. Words are kept verbatim (quotes and escape sequences included),
	 * quote removal and escape resolution happen later in
	 * {@link #unquoteAndUnescapeQuoted(String)}.
	 * @param input the raw input line
	 * @return the list of words
	 * @throws IllegalArgumentException if the input contains an unbalanced quote
	 * @since 4.0.4
	 */
	protected List<String> tokenize(String input) {
		List<String> words = new ArrayList<>();
		StringBuilder currentWord = new StringBuilder();
		char openingQuote = 0;
		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);
			if (currentChar == '\\' && i + 1 < input.length()) {
				currentWord.append(currentChar).append(input.charAt(++i));
			}
			else if (openingQuote != 0) {
				currentWord.append(currentChar);
				if (currentChar == openingQuote) {
					openingQuote = 0;
				}
			}
			else if (currentChar == '"' || currentChar == '\'') {
				openingQuote = currentChar;
				currentWord.append(currentChar);
			}
			else if (Character.isWhitespace(currentChar)) {
				if (!currentWord.isEmpty()) {
					words.add(currentWord.toString());
					currentWord.setLength(0);
				}
			}
			else {
				currentWord.append(currentChar);
			}
		}
		if (openingQuote != 0) {
			throw new IllegalArgumentException("Unbalanced quote (" + openingQuote + ") in input: " + input);
		}
		if (!currentWord.isEmpty()) {
			words.add(currentWord.toString());
		}
		return words;
	}

	// Check if the word is the argument separator, ie empty "--" (POSIX style)
	private boolean isArgumentSeparator(String word) {
		return word.equals("--");
	}

	private boolean isOption(String word) {
		return (word.startsWith("-") || word.startsWith("--")) && !isArgumentSeparator(word);
	}

	private CommandOption parseOption(String word) {
		char shortName = ' ';
		String longName = "";
		String value = "";
		if (word.startsWith("--")) {
			word = word.substring(2);
			String[] tokens = word.split("=", 2);
			longName = tokens[0];
			if (tokens.length > 1) {
				value = tokens[1];
			}
		}
		else if (word.startsWith("-")) {
			word = word.substring(1);
			String[] tokens = word.split("=", 2);
			shortName = tokens[0].charAt(0);
			if (tokens.length > 1) {
				value = tokens[1];
			}
		}
		return CommandOption.with()
			.shortName(shortName)
			.longName(longName)
			.value(unquoteAndUnescapeQuoted(value))
			.build();
	}

	private CommandArgument parseArgument(int index, String word) {
		return CommandArgument.with().index(index).value(unquoteAndUnescapeQuoted(word)).build();
	}

	private String unquoteAndUnescapeQuoted(String s) {
		// only process quoted strings
		if (s.length() >= 2) {
			char quote = s.charAt(0);
			if ((quote == '"' || quote == '\'') && s.charAt(s.length() - 1) == quote) {
				s = s.substring(1, s.length() - 1);

				// unescape only inside quoted strings
				s = s.replace("\\" + quote, String.valueOf(quote)).replace("\\\\", "\\");
			}
		}
		return s;
	}

	private boolean isBooleanOption(String commandName, String currentWord) {
		List<CommandOption> declaredOptions = Optional.ofNullable(commandRegistry.getCommandByName(commandName))
			.map(Command::getOptions)
			.orElse(List.of())
			.stream()
			.filter(o -> o.isOptionEqual(currentWord))
			.toList();
		if (declaredOptions.isEmpty()) {
			return isHelpOption(currentWord);
		}
		return declaredOptions.stream().anyMatch(o -> o.type() == boolean.class || o.type() == Boolean.class);
	}

	private boolean isHelpOption(String word) {
		return word.equals("--help") || word.equals("-h");
	}

	private boolean isBooleanValue(String rawValue) {
		return "true".equalsIgnoreCase(rawValue) || "false".equalsIgnoreCase(rawValue);
	}

}
