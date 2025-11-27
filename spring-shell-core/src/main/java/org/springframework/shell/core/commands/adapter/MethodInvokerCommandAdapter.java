package org.springframework.shell.core.commands.adapter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.shell.core.command.CommandArgument;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Arguments;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.commands.AbstractCommand;
import org.springframework.util.MethodInvoker;

import static org.springframework.shell.core.utils.CommandUtils.getOptionByName;

public class MethodInvokerCommandAdapter extends AbstractCommand {

	Method method;

	Object targetObject;

	ConfigurableConversionService conversionService;

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
		commandContext.terminal().flush();
		return ExitStatus.OK;
	}

	private List<Object> prepareArguments(CommandContext commandContext) {
		List<Object> args = new ArrayList<>();
		List<CommandOption> options = commandContext.options();
		Parameter[] parameters = method.getParameters();
		Class<?>[] methodParameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			Option optionAnnotation = parameters[i].getAnnotation(Option.class);
			if (optionAnnotation != null) {
				char shortName = optionAnnotation.shortName();
				String longName = optionAnnotation.longName();
				CommandOption commandOption = getOptionByName(options,
						longName.isEmpty() ? String.valueOf(shortName) : longName);
				if (commandOption == null) {
					// Option not provided, use default value
					String defaultValue = optionAnnotation.defaultValue();
					Class<?> parameterType = methodParameterTypes[i];
					Object value = this.conversionService.convert(defaultValue, parameterType);
					args.add(value);
				}
				else {
					String rawValue = commandOption.value();
					Class<?> parameterType = methodParameterTypes[i];
					Object value = this.conversionService.convert(rawValue, parameterType);
					args.add(value);
				}
			}
			Argument argumentAnnotation = parameters[i].getAnnotation(Argument.class);
			if (argumentAnnotation != null) {
				int index = argumentAnnotation.index();
				String rawValue = commandContext.arguments().get(index).value();
				Class<?> parameterType = methodParameterTypes[i];
				Object value = this.conversionService.convert(rawValue, parameterType);
				args.add(value);
			}
			Arguments argumentsAnnotation = parameters[i].getAnnotation(Arguments.class);
			if (argumentsAnnotation != null) {
				List<String> rawValues = commandContext.arguments().stream().map(CommandArgument::value).toList();
				Class<?> parameterType = methodParameterTypes[i];
				// TODO check for collection types
				Object value = this.conversionService.convert(rawValues, parameterType);
				args.add(value);
			}

		}
		return args;
	}

}
