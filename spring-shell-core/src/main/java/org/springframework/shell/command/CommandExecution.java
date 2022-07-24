/*
 * Copyright 2022 the original author or authors.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Validator;

import org.jline.terminal.Terminal;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.Availability;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.command.CommandParser.CommandParserException;
import org.springframework.shell.command.CommandParser.CommandParserResults;
import org.springframework.shell.command.CommandRegistration.TargetInfo;
import org.springframework.shell.command.CommandRegistration.TargetInfo.TargetType;
import org.springframework.shell.command.invocation.InvocableShellMethod;
import org.springframework.shell.command.invocation.ShellMethodArgumentResolverComposite;

/**
 * Interface to evaluate a result from a command with an arguments.
 *
 * @author Janne Valkealahti
 */
public interface CommandExecution {

	/**
	 * Evaluate a command with a given arguments.
	 *
	 * @param registration the command registration
	 * @param args         the command args
	 * @return evaluated execution
	 */
	Object evaluate(CommandRegistration registration, String[] args);

	/**
	 * Gets an instance of a default {@link CommandExecution}.
	 *
	 * @param resolvers the handler method argument resolvers
	 * @return default command execution
	 */
	public static CommandExecution of(List<? extends HandlerMethodArgumentResolver> resolvers) {
		return new DefaultCommandExecution(resolvers, null, null, null);
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
	public static CommandExecution of(List<? extends HandlerMethodArgumentResolver> resolvers, Validator validator,
			Terminal terminal, ConversionService conversionService) {
		return new DefaultCommandExecution(resolvers, validator, terminal, conversionService);
	}

	/**
	 * Default implementation of a {@link CommandExecution}.
	 */
	static class DefaultCommandExecution implements CommandExecution {

		private List<? extends HandlerMethodArgumentResolver> resolvers;
		private Validator validator;
		private Terminal terminal;
		private ConversionService conversionService;

		public DefaultCommandExecution(List<? extends HandlerMethodArgumentResolver> resolvers, Validator validator,
				Terminal terminal, ConversionService conversionService) {
			this.resolvers = resolvers;
			this.validator = validator;
			this.terminal = terminal;
			this.conversionService = conversionService;
		}

		public Object evaluate(CommandRegistration registration, String[] args) {
			// fast fail with availability before doing anything else
			Availability availability = registration.getAvailability();
			if (availability != null && !availability.isAvailable()) {
				return new CommandNotCurrentlyAvailable(registration.getCommand(), availability);
			}

			List<CommandOption> options = registration.getOptions();
			CommandParser parser = CommandParser.of(conversionService);
			CommandParserResults results = parser.parse(options, args);

			if (!results.errors().isEmpty()) {
				throw new CommandParserExceptionsException("Command parser resulted errors", results.errors());
			}

			CommandContext ctx = CommandContext.of(args, results, terminal, registration);

			Object res = null;

			TargetInfo targetInfo = registration.getTarget();

			// pick the target to execute
			if (targetInfo.getTargetType() == TargetType.FUNCTION) {
				res = targetInfo.getFunction().apply(ctx);
			}
			else if (targetInfo.getTargetType() == TargetType.CONSUMER) {
				targetInfo.getConsumer().accept(ctx);
			}
			else if (targetInfo.getTargetType() == TargetType.METHOD) {
				try {
					MessageBuilder<String[]> messageBuilder = MessageBuilder.withPayload(args);
					Map<String, Object> paramValues = new HashMap<>();
					results.results().stream().forEach(r -> {
						if (r.option().getLongNames() != null) {
							for (String n : r.option().getLongNames()) {
								messageBuilder.setHeader(ArgumentHeaderMethodArgumentResolver.ARGUMENT_PREFIX + n, r.value());
								paramValues.put(n, r.value());
							}
						}
						if (r.option().getShortNames() != null) {
							for (Character n : r.option().getShortNames()) {
								messageBuilder.setHeader(ArgumentHeaderMethodArgumentResolver.ARGUMENT_PREFIX + n.toString(), r.value());
							}
						}
					});
					messageBuilder.setHeader(CommandContextMethodArgumentResolver.HEADER_COMMAND_CONTEXT, ctx);

					InvocableShellMethod invocableShellMethod = new InvocableShellMethod(targetInfo.getBean(), targetInfo.getMethod());
					invocableShellMethod.setConversionService(conversionService);
					invocableShellMethod.setValidator(validator);
					ShellMethodArgumentResolverComposite argumentResolvers = new ShellMethodArgumentResolverComposite();
					if (resolvers != null) {
						argumentResolvers.addResolvers(resolvers);
					}
					if (!paramValues.isEmpty()) {
						argumentResolvers.addResolver(new ParamNameHandlerMethodArgumentResolver(paramValues, conversionService));
					}
					invocableShellMethod.setMessageMethodArgumentResolvers(argumentResolvers);

					res = invocableShellMethod.invoke(messageBuilder.build(), (Object[])null);

				} catch (Exception e) {
					throw new CommandExecutionException(e);
				}
			}

			return res;
		}
	}

	@Order(100)
	static class ParamNameHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

		private final Map<String, Object> paramValues = new HashMap<>();
		private final ConversionService conversionService;

		ParamNameHandlerMethodArgumentResolver(Map<String, Object> paramValues, ConversionService conversionService) {
			this.paramValues.putAll(paramValues);
			this.conversionService = conversionService;
		}

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			String parameterName = parameter.getParameterName();
			if (parameterName == null) {
				return false;
			}
			return paramValues.containsKey(parameterName) && conversionService
					.canConvert(paramValues.get(parameterName).getClass(), parameter.getParameterType());
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
			return conversionService.convert(paramValues.get(parameter.getParameterName()), parameter.getParameterType());
		}

	}

	static class CommandExecutionException extends RuntimeException {

		public CommandExecutionException(Throwable cause) {
			super(cause);
		}
	}

	static class CommandParserExceptionsException extends RuntimeException {

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

	static class CommandExecutionHandlerMethodArgumentResolvers {

		private final List<? extends HandlerMethodArgumentResolver> resolvers;

		public CommandExecutionHandlerMethodArgumentResolvers(List<? extends HandlerMethodArgumentResolver> resolvers) {
			this.resolvers = resolvers;
		}

		public List<? extends HandlerMethodArgumentResolver> getResolvers() {
			return resolvers;
		}
	}
}
