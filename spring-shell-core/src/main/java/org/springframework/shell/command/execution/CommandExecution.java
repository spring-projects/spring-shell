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
package org.springframework.shell.command.execution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Validator;
import org.jline.terminal.Terminal;

import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.shell.command.*;
import org.springframework.shell.command.CommandParser.CommandParserException;


/**
 * Interface to evaluate a result from a command with arguments.
 *
 * @author Janne Valkealahti
 */
public interface CommandExecution {

	/**
	 * Evaluate a command with given arguments.
	 *
	 * @param args         the command args
	 * @return evaluated execution
	 */
	Object evaluate(String[] args);

	/**
	 * Gets an instance of a default {@link CommandExecution}.
	 *
	 * @param resolvers the handler method argument resolvers
	 * @return default command execution
	 */
	static CommandExecution of(List<? extends HandlerMethodArgumentResolver> resolvers) {
		return new DefaultCommandExecution(resolvers, null, null, null, null);
	}

	/**
	 * Gets an instance of a default {@link CommandExecution}.
	 *
	 * @param resolvers the handler method argument resolvers
	 * @param validator the validator
	 * @param terminal the terminal
	 * @param conversionService the conversion services
	 * @return default command execution
	 */
	static CommandExecution of(List<? extends HandlerMethodArgumentResolver> resolvers, Validator validator,
			Terminal terminal, ConversionService conversionService) {
		return new DefaultCommandExecution(resolvers, validator, terminal, conversionService, null);
	}

	/**
	 * Gets an instance of a default {@link CommandExecution}.
	 *
	 * @param resolvers the handler method argument resolvers
	 * @param validator the validator
	 * @param terminal the terminal
	 * @param conversionService the conversion services
	 * @return default command execution
	 */
	static CommandExecution of(List<? extends HandlerMethodArgumentResolver> resolvers, Validator validator,
			Terminal terminal, ConversionService conversionService, CommandCatalog commandCatalog) {
		return new DefaultCommandExecution(resolvers, validator, terminal, conversionService, commandCatalog);
	}

	class CommandExecutionException extends RuntimeException {

		public CommandExecutionException(Throwable cause) {
			super(cause);
		}
	}

	class CommandParserExceptionsException extends RuntimeException {

		private final List<CommandParserException> parserExceptions;

		public CommandParserExceptionsException(String message, List<CommandParserException> parserExceptions) {
			super(message);
			this.parserExceptions = parserExceptions;
		}

		public static CommandParserExceptionsException of(String message, List<CommandParserException> parserExceptions) {
			return new CommandParserExceptionsException(message, parserExceptions);
		}

		public List<CommandParserException> getParserExceptions() {
			return parserExceptions;
		}
	}

	class CommandExecutionHandlerMethodArgumentResolvers {

		private final List<? extends HandlerMethodArgumentResolver> resolvers;

		public CommandExecutionHandlerMethodArgumentResolvers(List<? extends HandlerMethodArgumentResolver> resolvers) {
			this.resolvers = resolvers;
		}

		public List<? extends HandlerMethodArgumentResolver> getResolvers() {
			return resolvers;
		}
	}
}
