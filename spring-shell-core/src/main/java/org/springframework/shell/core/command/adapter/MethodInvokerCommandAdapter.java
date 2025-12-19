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
package org.springframework.shell.core.command.adapter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.shell.core.ParameterValidationException;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.CommandArgument;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Arguments;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.util.MethodInvoker;

/**
 * An adapter to adapt a method as a command.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class MethodInvokerCommandAdapter extends AbstractCommand {

	private static final Log log = LogFactory.getLog(MethodInvokerCommandAdapter.class);

	private final Method method;

	private final Object targetObject;

	private final Validator validator;

	private final ConfigurableConversionService conversionService;

	/**
	 * Create a new {@link MethodInvokerCommandAdapter}.
	 * @param name the name of the command
	 * @param description the description of the command
	 * @param group the group of the command
	 * @param help the help text of the command
	 * @param hidden whether the command is hidden or not
	 * @param method the method to invoke
	 * @param targetObject the target object on which to invoke the method
	 * @param conversionService the conversion service to use for parameter conversion
	 */
	public MethodInvokerCommandAdapter(String name, String description, String group, String help, boolean hidden,
			Method method, Object targetObject, ConfigurableConversionService conversionService, Validator validator) {
		super(name, description, group, help, hidden);
		this.method = method;
		this.targetObject = targetObject;
		this.conversionService = conversionService;
		this.validator = validator;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) throws Exception {
		// prepare method invoker
		MethodInvoker methodInvoker = new MethodInvoker();
		Class<?> declaringClass = this.method.getDeclaringClass();
		methodInvoker.setTargetClass(declaringClass);
		methodInvoker.setTargetObject(this.targetObject);
		methodInvoker.setTargetMethod(method.getName());

		// prepare method parameters
		List<Object> arguments = prepareArguments(commandContext);
		methodInvoker.setArguments(arguments.toArray());
		methodInvoker.prepare();

		// validate parameters
		Set<ConstraintViolation<Object>> constraintViolations = validator.forExecutables()
			.validateParameters(targetObject, method, arguments.toArray());
		if (!constraintViolations.isEmpty()) {
			throw new ParameterValidationException(constraintViolations);
		}

		// invoke method
		methodInvoker.invoke();
		commandContext.outputWriter().flush();

		return ExitStatus.OK;
	}

	private List<Object> prepareArguments(CommandContext commandContext) {
		log.debug("Preparing method arguments for method: " + this.method.getName() + " of class: "
				+ this.method.getDeclaringClass().getName() + " with context: " + commandContext);
		List<Object> args = new ArrayList<>();
		Parameter[] parameters = this.method.getParameters();
		Class<?>[] parameterTypes = this.method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			// Handle CommandContext injection
			if (parameterTypes[i].equals(CommandContext.class)) {
				log.debug("Injecting CommandContext for parameter: " + parameters[i].getName());
				args.add(commandContext);
				continue;
			}

			// Ensure parameter is not annotated with both Option and Argument
			if (parameters[i].getAnnotation(Option.class) != null
					&& parameters[i].getAnnotation(Argument.class) != null) {
				throw new IllegalArgumentException("Parameter " + parameters[i].getName()
						+ " cannot be annotated with @Option and @Argument at the same time.");
			}

			// Handle Option injection
			Option optionAnnotation = parameters[i].getAnnotation(Option.class);
			if (optionAnnotation != null) {
				log.debug("Processing option for parameter: " + parameters[i].getName());
				char shortName = optionAnnotation.shortName();
				String longName = optionAnnotation.longName();
				CommandOption commandOption = commandContext
					.getOptionByName(longName.isEmpty() ? String.valueOf(shortName) : longName);
				if (commandOption == null) {
					// Option not provided, use default value
					String defaultValue = optionAnnotation.defaultValue();
					Class<?> parameterType = parameterTypes[i];
					Object value = this.conversionService.convert(defaultValue, parameterType);
					args.add(value);
				}
				else {
					String rawValue = commandOption.value();
					Class<?> parameterType = parameterTypes[i];
					Object value = this.conversionService.convert(rawValue, parameterType);
					args.add(value);
				}
				continue;
			}
			// Handle Argument injection
			Argument argumentAnnotation = parameters[i].getAnnotation(Argument.class);
			if (argumentAnnotation != null) {
				log.debug("Processing argument for parameter: " + parameters[i].getName());
				int index = argumentAnnotation.index();
				String rawValue;
				try {
					rawValue = commandContext.getArgumentByIndex(index).value();
				}
				catch (Exception e) {
					rawValue = argumentAnnotation.defaultValue();
				}
				Class<?> parameterType = parameterTypes[i];
				Object value = this.conversionService.convert(rawValue, parameterType);
				args.add(value);
				continue;
			}
			// Handle Arguments list injection
			Arguments argumentsAnnotation = parameters[i].getAnnotation(Arguments.class);
			if (argumentsAnnotation != null) {
				log.debug("Processing arguments for parameter: " + parameters[i].getName());
				List<String> rawValues = commandContext.parsedInput()
					.arguments()
					.stream()
					.map(CommandArgument::value)
					.toList();
				Class<?> parameterType = parameterTypes[i];
				// TODO check for collection types
				Object value = this.conversionService.convert(rawValues, parameterType);
				args.add(value);
			}

		}
		return args;
	}

}
