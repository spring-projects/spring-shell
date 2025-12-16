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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.shell.core.command.*;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Arguments;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.util.MethodInvoker;

import static org.springframework.shell.core.utils.CommandUtils.getOptionByName;

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

	private final ConfigurableConversionService conversionService;

	/**
	 * Create a new {@link MethodInvokerCommandAdapter}.
	 * @param name the name of the command
	 * @param description the description of the command
	 * @param group the group of the command
	 * @param help the help text of the command
	 * @param method the method to invoke
	 * @param targetObject the target object on which to invoke the method
	 * @param conversionService the conversion service to use for parameter conversion
	 */
	public MethodInvokerCommandAdapter(String name, String description, String group, String help, Method method,
			Object targetObject, ConfigurableConversionService conversionService) {
		super(name, description, group, help);
		this.method = method;
		this.targetObject = targetObject;
		this.conversionService = conversionService;
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
		Class<?>[] parameterTypes = this.method.getParameterTypes();

		// TODO should be able to mix CommandContext and other parameters
		if (parameterTypes.length == 1
				&& parameterTypes[0].equals(org.springframework.shell.core.command.CommandContext.class)) {
			methodInvoker.setArguments(commandContext);
		}
		else {
			List<Object> arguments = prepareArguments(commandContext);
			methodInvoker.setArguments(arguments.toArray());
		}
		methodInvoker.prepare();

		// invoke method
		methodInvoker.invoke();
		commandContext.outputWriter().flush();

		return ExitStatus.OK;
	}

	private List<Object> prepareArguments(CommandContext commandContext) {
		log.debug("Preparing method arguments for method: " + this.method.getName() + " of class: "
				+ this.method.getDeclaringClass().getName() + " with context: " + commandContext);
		List<Object> args = new ArrayList<>();
		ParsedInput parsedInput = commandContext.parsedInput();
		List<CommandOption> options = parsedInput.options();
		List<CommandArgument> arguments = parsedInput.arguments();
		Parameter[] parameters = this.method.getParameters();
		Class<?>[] parameterTypes = this.method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			Option optionAnnotation = parameters[i].getAnnotation(Option.class);
			if (optionAnnotation != null) {
				log.debug("Processing option for parameter: " + parameters[i].getName());
				char shortName = optionAnnotation.shortName();
				String longName = optionAnnotation.longName();
				CommandOption commandOption = getOptionByName(options,
						longName.isEmpty() ? String.valueOf(shortName) : longName);
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
			}
			Argument argumentAnnotation = parameters[i].getAnnotation(Argument.class);
			if (argumentAnnotation != null) {
				log.debug("Processing argument for parameter: " + parameters[i].getName());
				int index = argumentAnnotation.index();
				String rawValue;
				try {
					rawValue = arguments.get(index).value();
				}
				catch (Exception e) {
					rawValue = argumentAnnotation.defaultValue();
				}
				Class<?> parameterType = parameterTypes[i];
				Object value = this.conversionService.convert(rawValue, parameterType);
				args.add(value);
			}
			Arguments argumentsAnnotation = parameters[i].getAnnotation(Arguments.class);
			if (argumentsAnnotation != null) {
				log.debug("Processing arguments for parameter: " + parameters[i].getName());
				List<String> rawValues = arguments.stream().map(CommandArgument::value).toList();
				Class<?> parameterType = parameterTypes[i];
				// TODO check for collection types
				Object value = this.conversionService.convert(rawValues, parameterType);
				args.add(value);
			}

		}
		return args;
	}

}
