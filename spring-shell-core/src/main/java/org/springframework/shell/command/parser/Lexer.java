/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.command.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.command.parser.CommandModel.CommandInfo;
import org.springframework.shell.command.parser.ParserConfig.Feature;

/**
 * Interface to tokenize arguments into tokens. Generic language parser usually
 * contains lexing and parsing where this {@code Lexer} represents the former
 * lexing side.
 *
 * Lexing takes a first step to analyse basic construct of elements out from
 * given arguments. We get rough idea what each argument represents but don't
 * look deeper if any of it is correct which happens later when tokens go
 * through parsing operation.
 *
 * @author Janne Valkealahti
 */
public interface Lexer {

	/**
	 * Tokenize given command line arguments into a list of tokens.
	 *
	 * @param arguments the command line arguments
	 * @return lexer result having tokens and operation messages
	 */
	LexerResult tokenize(List<String> arguments);

	/**
	 * Representing result from {@link Lexer} tokenisation.
	 *
	 * @param tokens list of tokens in this result
	 * @param messageResults list of error results in this result
	 */
	public record LexerResult(List<Token> tokens, List<MessageResult> messageResults) {
	}

	/**
	 * Default implementation of a {@link Lexer}.
	 */
	public class DefaultLexer implements Lexer {

		private final static Logger log = LoggerFactory.getLogger(DefaultLexer.class);
		private final CommandModel commandModel;
		private final ParserConfig config;

		public DefaultLexer(CommandModel commandModel, ParserConfig config) {
			this.commandModel = commandModel;
			this.config = config;
		}

		private record ArgumentsSplit(List<String> before, List<String> after) {
		}

		/**
		 * Splits arguments from a point first valid command is found, where
		 * {@code before} is everything before commands and {@code after} what's
		 * remaining.
		 */
		private ArgumentsSplit splitArguments(List<String> arguments, Map<String, Token> validTokens) {
			int i = -1;
			boolean foundSplit = false;
			for (String argument : arguments) {
				if (!config.isEnabled(Feature.CASE_SENSITIVE_COMMANDS)) {
					argument = argument.toLowerCase();
				}
				i++;
				if (validTokens.containsKey(argument)) {
					foundSplit = true;
					break;
				}
			}
			if (i < 0) {
				return new ArgumentsSplit(Collections.emptyList(), Collections.emptyList());
			}
			else if (i == 0) {
				if (foundSplit) {
					return new ArgumentsSplit(Collections.emptyList(), arguments);
				}
				return new ArgumentsSplit(arguments, Collections.emptyList());
			}
			return new ArgumentsSplit(arguments.subList(0, i), arguments.subList(i, arguments.size()));
		}

		private List<String> extractDirectives(List<String> arguments) {
			List<String> ret = new ArrayList<>();
			Pattern pattern = Pattern.compile("\\[(.*?)\\]");
			String raw = arguments.stream().collect(Collectors.joining());
			Matcher matcher = pattern.matcher(raw);
			while (matcher.find()) {
				String group = matcher.group(1);
				ret.add(group);
			}
			return ret;
		}

		@Override
		public LexerResult tokenize(List<String> arguments) {
			log.debug("Tokenizing arguments {}", arguments);
			List<MessageResult> errorResults = new ArrayList<>();
			List<Token> tokenList = new ArrayList<Token>();

			preValidate(errorResults, arguments);

			// starting from root level
			Map<String, Token> validTokens = commandModel.getValidRootTokens();

			// we process arguments in two steps, ones before commands and commands
			ArgumentsSplit split = splitArguments(arguments, validTokens);

			// consume everything before command section starts
			// currently there can only be directives and we need
			// to differentiate if we silenty ignore those vs.
			// whether directive support is enabled or not
			List<String> beforeArguments = split.before();

			int i1 = split.before().size() - 1;

			if (config.isEnabled(Feature.ALLOW_DIRECTIVES)) {
				List<String> rawDirectives = extractDirectives(beforeArguments);
				for (String raw : rawDirectives) {
					tokenList.add(Token.of(raw, TokenType.DIRECTIVE, 0));
				}
			}
			else {
				if (!config.isEnabled(Feature.IGNORE_DIRECTIVES) && beforeArguments.size() > 0) {
					errorResults.add(MessageResult.of(ParserMessage.ILLEGAL_CONTENT_BEFORE_COMMANDS, 0, beforeArguments));
				}
			}

			// consume remaining arguments which should contain
			// only ones starting from a first command
			boolean foundDoubleDash = false;
			List<String> afterArguments = split.after();
			CommandInfo currentCommand = null;

			int i2 = i1;
			for (String argument : afterArguments) {
				// if (!configuration.isEnabled(Feature.CASE_SENSITIVE_COMMANDS)) {
				// 	argument = argument.toLowerCase();
				// }
				i2++;

				// We've found bash style "--" meaning further option processing is
				// stopped and remaining arguments are simply command arguments
				if (foundDoubleDash) {
					tokenList.add(Token.of(argument, TokenType.ARGUMENT, i2));
					continue;
				}
				if (!foundDoubleDash && "--".equals(argument)) {
						tokenList.add(Token.of(argument, TokenType.DOUBLEDASH, i2));
						foundDoubleDash = true;
						continue;
				}

				String argumentToCheck = argument;
				if (!config.isEnabled(Feature.CASE_SENSITIVE_COMMANDS)
						|| !config.isEnabled(Feature.CASE_SENSITIVE_OPTIONS)) {
					argumentToCheck = argument.toLowerCase();
				}

				if (validTokens.containsKey(argumentToCheck)) {
					Token token = validTokens.get(argumentToCheck);
					switch (token.getType()) {
						case COMMAND:
							currentCommand = currentCommand == null ? commandModel.getRootCommands().get(argumentToCheck)
									: currentCommand.getChildren(argument);
							tokenList.add(Token.of(argument, TokenType.COMMAND, i2));
							validTokens = currentCommand.getValidTokens();
							break;
						case OPTION:
							tokenList.add(Token.of(argument, TokenType.OPTION, i2));
							break;
						default:
							break;
					}
				}
				else if (isLastTokenOfType(tokenList, TokenType.OPTION)) {
					// posix short style can only have one or more letters
					int decuceArgumentStyle = decuceArgumentStyle(argument);
					if (decuceArgumentStyle > 0) {
						tokenList.add(Token.of(argument, TokenType.OPTION, i2));
					}
					else if (decuceArgumentStyle < 0) {
						tokenList.add(Token.of(argument, TokenType.ARGUMENT, i2));
					}
					else {
						tokenList.add(Token.of(argument, TokenType.OPTION, i2));
					}
				}
				else if (isLastTokenOfType(tokenList, TokenType.COMMAND)) {
					if (argument.startsWith("-")) {
						tokenList.add(Token.of(argument, TokenType.OPTION, i2));
					}
					else {
						tokenList.add(Token.of(argument, TokenType.ARGUMENT, i2));
					}
				}
				else if (isLastTokenOfType(tokenList, TokenType.ARGUMENT)) {
					tokenList.add(Token.of(argument, TokenType.ARGUMENT, i2));
				}

			}

			log.debug("Generated token list {}", tokenList);
			return new LexerResult(tokenList, errorResults);
		}

		private void preValidate(List<MessageResult> errorResults, List<String> arguments) {
			if (arguments.size() > 0) {
				String arg = arguments.get(0);
				if ("--".equals(arg)) {
					errorResults.add(MessageResult.of(ParserMessage.ILLEGAL_CONTENT_BEFORE_COMMANDS, 0, arg));
				}
			}
		}

		private static boolean isLastTokenOfType(List<Token> tokenList, TokenType type) {
			if (tokenList.size() > 0) {
				if (tokenList.get(tokenList.size() - 1).getType() == type) {
					return true;
				}
			}
			return false;
		}

		private static int decuceArgumentStyle(String str) {
			// positive - looks like posix short
			// 0 - looks like long option
			// negative - looks like argument, not option
			if (str.length() < 2) {
				return -1;
			}
			if (str.charAt(0) != '-') {
				return -1;
			}
			if (str.length() > 1 && str.charAt(0) == '-' && str.charAt(1) == '-') {
				return 0;
			}
			int ret = 1;
			for (int i = 1; i < str.length(); i++) {
				if (!Character.isLetter(str.charAt(i))) {
					ret = -1;
					break;
				}
			}
			return ret;
		}
	}
}
