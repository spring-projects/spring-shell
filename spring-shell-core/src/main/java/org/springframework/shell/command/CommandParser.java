/*
 * Copyright 2022-2023 the original author or authors.
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
package org.springframework.shell.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.shell.command.parser.Ast;
import org.springframework.shell.command.parser.Ast.DefaultAst;
import org.springframework.shell.command.parser.CommandModel;
import org.springframework.shell.command.parser.Lexer.DefaultLexer;
import org.springframework.shell.command.parser.Parser.DefaultParser;
import org.springframework.shell.command.parser.Parser.ParseResult;
import org.springframework.shell.command.parser.ParserConfig;

/**
 * Interface parsing arguments for a {@link CommandRegistration}. A command is
 * always identified by a set of words like
 * {@code command subcommand1 subcommand2} and remaining part of it are options
 * which this interface intercepts and translates into format we can understand.
 *
 * @author Janne Valkealahti
 */
public interface CommandParser {

	/**
	 * Result of a parsing {@link CommandOption} with an argument.
	 */
	interface CommandParserResult {

		/**
		 * Gets the {@link CommandOption}.
		 *
		 * @return the command option
		 */
		CommandOption option();

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		Object value();

		/**
		 * Gets an instance of a default {@link CommandParserResult}.
		 *
		 * @param option the command option
		 * @param value the value
		 * @return a result
		 */
		static CommandParserResult of(CommandOption option, Object value) {
			return new DefaultCommandParserResult(option, value);
		}
	}

	/**
	 * Results of a {@link CommandParser}. Basically contains a list of {@link CommandParserResult}s.
	 */
	interface CommandParserResults {

		/**
		 * Gets the registration.
		 *
		 * @return the registration
		 */
		CommandRegistration registration();

		/**
		 * Gets the results.
		 *
		 * @return the results
		 */
		List<CommandParserResult> results();

		/**
		 * Gets the unmapped positional arguments.
		 *
		 * @return the unmapped positional arguments
		 */
		List<String> positional();

		/**
		 * Gets parsing errors.
		 *
		 * @return the parsing errors
		 */
		List<CommandParserException> errors();

		/**
		 * Gets an instance of a default {@link CommandParserResults}.
		 *
		 * @param registration the registration
		 * @param results the results
		 * @param positional the list of positional arguments
		 * @param errors the parsing errors
		 * @return a new instance of results
		 */
		static CommandParserResults of(CommandRegistration registration, List<CommandParserResult> results,
				List<String> positional, List<CommandParserException> errors) {
			return new DefaultCommandParserResults(registration, results, positional, errors);
		}
	}

	/**
	 * Parse options with a given arguments.
	 *
	 * May throw various runtime exceptions depending how parser is configure.
	 * For example if required option is missing an exception is thrown.
	 *
	 * @param args the arguments
	 * @return parsed results
	 */
	CommandParserResults parse(String[] args);

	/**
	 * Gets an instance of a default command parser.
	 *
	 * @param conversionService the conversion service
	 * @param registrations the command registrations
	 * @param config the parser config
	 * @return instance of a default command parser
	 */
	static CommandParser of(ConversionService conversionService, Map<String, CommandRegistration> registrations,
			ParserConfig config) {
		return new AstCommandParser(registrations, config, conversionService);
	}

	/**
	 * Default implementation of a {@link CommandParserResults}.
	 */
	static class DefaultCommandParserResults implements CommandParserResults {

		private CommandRegistration registration;
		private List<CommandParserResult> results;
		private List<String> positional;
		private List<CommandParserException> errors;

		DefaultCommandParserResults(CommandRegistration registration, List<CommandParserResult> results,
				List<String> positional, List<CommandParserException> errors) {
			this.registration = registration;
			this.results = results;
			this.positional = positional;
			this.errors = errors;
		}

		@Override
		public CommandRegistration registration() {
			return registration;
		}

		@Override
		public List<CommandParserResult> results() {
			return results;
		}

		@Override
		public List<String> positional() {
			return positional;
		}

		@Override
		public List<CommandParserException> errors() {
			return errors;
		}
	}

	/**
	 * Default implementation of a {@link CommandParserResult}.
	 */
	static class DefaultCommandParserResult implements CommandParserResult {

		private CommandOption option;
		private Object value;

		DefaultCommandParserResult(CommandOption option, Object value) {
			this.option = option;
			this.value = value;
		}

		@Override
		public CommandOption option() {
			return option;
		}

		@Override
		public Object value() {
			return value;
		}
	}

	/**
	 * Default implementation of a {@link CommandParser}.
	 */
	static class AstCommandParser implements CommandParser {

		private final Map<String, CommandRegistration> registrations;
		private final ParserConfig configuration;
		private final ConversionService conversionService;

		public AstCommandParser(Map<String, CommandRegistration> registrations, ParserConfig configuration,
				ConversionService conversionService) {
			this.registrations = registrations;
			this.configuration = configuration;
			this.conversionService = conversionService;
		}

		@Override
		public CommandParserResults parse(String[] args) {
			CommandModel commandModel = new CommandModel(registrations, configuration);
			org.springframework.shell.command.parser.Lexer lexer = new DefaultLexer(commandModel, configuration);
			Ast ast = new DefaultAst();
			org.springframework.shell.command.parser.Parser parser = new DefaultParser(commandModel, lexer, ast,
					configuration, conversionService);
			ParseResult result = parser.parse(Arrays.asList(args));

			List<CommandParserResult> results = new ArrayList<>();
			List<String> positional = new ArrayList<>();
			List<CommandParserException> errors = new ArrayList<>();

			result.optionResults().forEach(or -> {
				results.add(CommandParserResult.of(or.option(), or.value()));
			});

			result.messageResults().forEach(mr -> {
				errors.add(new CommandParserException(mr.getMessage()));
			});

			result.argumentResults().forEach(ar -> {
				positional.add(ar.value());
			});

			return new DefaultCommandParserResults(result.commandRegistration(), results, positional, errors);
		}
	}

	public static class CommandParserException extends RuntimeException {

		public CommandParserException(String message) {
			super(message);
		}

		public CommandParserException(String message, Throwable cause) {
			super(message, cause);
		}

		public static CommandParserException of(String message) {
			return new CommandParserException(message);
		}
	}
}
