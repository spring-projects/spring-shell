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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.validation.Validator;
import org.jline.terminal.Terminal;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.core.Availability;
import org.springframework.shell.core.CommandNotCurrentlyAvailable;
import org.springframework.shell.core.command.CommandParser.CommandParserException;
import org.springframework.shell.core.command.CommandParser.CommandParserResults;
import org.springframework.shell.core.command.CommandRegistration.HelpOptionInfo;
import org.springframework.shell.core.command.CommandRegistration.TargetInfo;
import org.springframework.shell.core.command.CommandRegistration.TargetInfo.TargetType;
import org.springframework.shell.core.command.invocation.InvocableShellMethod;
import org.springframework.shell.core.command.invocation.ShellMethodArgumentResolverComposite;
import org.springframework.shell.core.command.parser.ParserConfig;
import org.springframework.shell.core.context.ShellContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Interface to evaluate a result from a command with an arguments.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public interface CommandExecution {

	/**
	 * Evaluate a command with a given arguments.
	 *
	 * @param args         the command args
	 * @return evaluated execution
	 */
	@Nullable Object evaluate(String[] args);

	/**
	 * Gets an instance of a default {@link CommandExecution}.
	 *
	 * @param resolvers the handler method argument resolvers
	 * @return default command execution
	 */
	public static CommandExecution of(List<? extends HandlerMethodArgumentResolver> resolvers) {
		return new DefaultCommandExecution(resolvers, null, null, null, null, null);
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
			Terminal terminal, ShellContext shellContext, ConversionService conversionService) {
		return new DefaultCommandExecution(resolvers, validator, terminal, shellContext, conversionService, null);
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
	public static CommandExecution of(@Nullable List<? extends HandlerMethodArgumentResolver> resolvers, Validator validator,
			Terminal terminal, ShellContext shellContext, ConversionService conversionService, CommandRegistry commandRegistry) {
		return new DefaultCommandExecution(resolvers, validator, terminal, shellContext, conversionService, commandRegistry);
	}

	/**
	 * Default implementation of a {@link CommandExecution}.
	 */
	static class DefaultCommandExecution implements CommandExecution {

		private @Nullable List<? extends HandlerMethodArgumentResolver> resolvers;
		private @Nullable Validator validator;
		private @Nullable Terminal terminal;
		private @Nullable ShellContext shellContext;
		private @Nullable ConversionService conversionService;
		private @Nullable CommandRegistry commandRegistry;

		public DefaultCommandExecution(@Nullable List<? extends HandlerMethodArgumentResolver> resolvers, @Nullable Validator validator,
				@Nullable Terminal terminal, @Nullable ShellContext shellContext, @Nullable ConversionService conversionService, @Nullable CommandRegistry commandRegistry) {
			this.resolvers = resolvers;
			this.validator = validator;
			this.terminal = terminal;
			this.shellContext = shellContext;
			this.conversionService = conversionService;
			this.commandRegistry = commandRegistry;
		}

		public @Nullable Object evaluate(String[] args) {
			Map<String, CommandRegistration> registrations = commandRegistry == null ? Map.of() : commandRegistry.getRegistrations();
			CommandParser parser = CommandParser.of(conversionService, registrations, new ParserConfig());
			CommandParserResults results = parser.parse(args);
			CommandRegistration registration = results.registration();
			Assert.state(registration != null, "'registration' must not be null");

			// fast fail with availability before doing anything else
			Availability availability = registration.getAvailability();
			if (availability != null && !availability.isAvailable()) {
				return new CommandNotCurrentlyAvailable(registration.getCommand(), availability);
			}

			// check help options to short circuit
			boolean handleHelpOption = false;
			HelpOptionInfo helpOption = registration.getHelpOption();
			if (helpOption.isEnabled() && helpOption.getCommand() != null && (!ObjectUtils.isEmpty(helpOption.getLongNames()) || !ObjectUtils.isEmpty(helpOption.getShortNames()))) {
				handleHelpOption = results.results().stream()
					.filter(cpr -> {
						boolean present = false;
						if (helpOption.getLongNames() != null) {
							present = Arrays.asList(cpr.option().getLongNames()).stream()
								.filter(ln -> ObjectUtils.containsElement(helpOption.getLongNames(), ln))
								.findFirst()
								.isPresent();
						}
						if (present) {
							return true;
						}
						if (helpOption.getShortNames() != null) {
							present = Arrays.asList(cpr.option().getShortNames()).stream()
								.filter(sn -> ObjectUtils.containsElement(helpOption.getShortNames(), sn))
								.findFirst()
								.isPresent();
						}
						return present;
					})
					.findFirst()
					.isPresent();
			}

			// if needed switch registration to help command if we're short circuiting
			CommandRegistration usedRegistration;
			if (handleHelpOption) {
				String command = registration.getCommand();
				CommandParser helpParser = CommandParser.of(conversionService, registrations,
						new ParserConfig());
				CommandRegistration helpCommandRegistration = registrations
						.get(registration.getHelpOption().getCommand());
				CommandParserResults helpResults = helpParser.parse(new String[] { "help", "--command", command });
				results = helpResults;
				usedRegistration = helpCommandRegistration;
			}
			else {
				usedRegistration = registration;
			}

			if (!results.errors().isEmpty()) {
				throw new CommandParserExceptionsException("Command parser resulted errors", results.errors());
			}
			Assert.notNull(usedRegistration, "'usedRegistration' must not be null");
			CommandContext ctx = CommandContext.of(args, results, terminal, usedRegistration, shellContext);

			Object res = null;

			TargetInfo targetInfo = usedRegistration.getTarget();

			// pick the target to execute
			if (targetInfo.getTargetType() == TargetType.FUNCTION) {
				Function<CommandContext, ?> function = targetInfo.getFunction();
				if (function != null) {
					res = function.apply(ctx);
				}
			}
			else if (targetInfo.getTargetType() == TargetType.CONSUMER) {
				Consumer<CommandContext> consumer = targetInfo.getConsumer();
				if (consumer != null) {
					consumer.accept(ctx);
				}
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
							// need to provide backmapping for orinal names which were modified
							for (String n : r.option().getLongNamesModified()) {
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
					if (!paramValues.isEmpty() && conversionService != null) {
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
			Class<?> sourceType = paramValues.get(parameterName) != null ? paramValues.get(parameterName).getClass()
					: null;
			return paramValues.containsKey(parameterName) && conversionService
					.canConvert(sourceType, parameter.getParameterType());
		}

		@Override
		public @Nullable Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
			Object source = paramValues.get(parameter.getParameterName());
			if (source == null) {
				return null;
			}
			TypeDescriptor sourceType = new TypeDescriptor(ResolvableType.forClass(source.getClass()), null, null);
			TypeDescriptor targetType = new TypeDescriptor(parameter);
			return conversionService.convert(source, sourceType, targetType);
		}

	}

	static class CommandExecutionException extends RuntimeException {

		public CommandExecutionException(Throwable cause) {
			super(cause);
		}
	}

	public static class CommandParserExceptionsException extends RuntimeException {

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
