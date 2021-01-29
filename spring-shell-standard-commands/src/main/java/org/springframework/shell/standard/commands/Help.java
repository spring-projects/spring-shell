/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.standard.commands;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.*;
import org.springframework.shell.standard.CommandValueProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class Help {

	/**
	 * Marker interface for beans providing {@literal help} functionality to the shell.
	 *
	 * <p>
	 * To override the help command, simply register your own bean implementing that interface
	 * and the standard implementation will back off.
	 * </p>
	 *
	 * <p>
	 * To disable the {@literal help} command entirely, set the
	 * {@literal spring.shell.command.help.enabled=false} property in the environment.
	 * </p>
	 *
	 * @author Eric Bottard
	 */
	public interface Command {
	}

	private final List<ParameterResolver> parameterResolvers;

	private CommandRegistry commandRegistry;

	private MessageInterpolator messageInterpolator = Validation.buildDefaultValidatorFactory()
			.getMessageInterpolator();

	@Autowired
	public Help(List<ParameterResolver> parameterResolvers) {
		this.parameterResolvers = parameterResolvers;
	}

	@Autowired // ctor injection impossible b/c of circular dependency
	public void setCommandRegistry(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}


	@Autowired(required = false)
	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.messageInterpolator = validatorFactory.getMessageInterpolator();
	}


	@ShellMethod(value = "Display help about available commands.", prefix = "-")
	public CharSequence help(
			@ShellOption(defaultValue = ShellOption.NULL, valueProvider = CommandValueProvider.class, value = { "-C",
					"--command" }, help = "The command to obtain help for.") String command)
			throws IOException {
		if (command == null) {
			return listCommands();
		}
		else {
			return documentCommand(command);
		}

	}

	/**
	 * Return a description of a specific command. Uses a layout inspired by *nix man pages.
	 */
	private CharSequence documentCommand(String command) {
		MethodTarget methodTarget = commandRegistry.listCommands().get(command);
		if (methodTarget == null) {
			throw new IllegalArgumentException("Unknown command '" + command + "'");
		}

		AttributedStringBuilder result = new AttributedStringBuilder().append("\n\n");
		List<ParameterDescription> parameterDescriptions = getParameterDescriptions(methodTarget);

		// NAME
		documentCommandName(result, command, methodTarget.getHelp());

		// SYNOPSIS
		documentSynopsis(result, command, parameterDescriptions);

		// OPTIONS
		documentOptions(result, parameterDescriptions);

		// ALSO KNOWN AS
		documentAliases(result, command, methodTarget);

		// AVAILABILITY
		documentAvailability(result, methodTarget);

		result.append("\n");
		return result;
	}

	private void documentCommandName(AttributedStringBuilder result, String command, String help) {
		result.append("NAME", AttributedStyle.BOLD).append("\n\t");
		result.append(command).append(" - ").append(help).append("\n\n");
	}

	private void documentSynopsis(AttributedStringBuilder result, String command,
			List<ParameterDescription> parameterDescriptions) {
		result.append("SYNOPSIS", AttributedStyle.BOLD).append("\n\t");
		result.append(command, AttributedStyle.BOLD);
		result.append(" ");

		for (ParameterDescription description : parameterDescriptions) {

			if (description.defaultValue().isPresent() && description.formal().length() > 0) {
				result.append("["); // Whole parameter is optional, as there is a default value (1)
			}
			List<String> keys = description.keys();
			if (!keys.isEmpty()) {
				if (!description.mandatoryKey()) {
					result.append("["); // Specifying a key is optional (ie positional params). (2)
				}
				result.append(first(keys), AttributedStyle.BOLD);
				if (!description.mandatoryKey()) {
					result.append("]"); // (close 2)
				}
				if (!description.formal().isEmpty()) {
					result.append(" ");
				}
			}
			if (description.defaultValueWhenFlag().isPresent()) {
				result.append("["); // Parameter can be used as a toggle flag (3)
			}
			appendUnderlinedFormal(result, description);
			if (description.defaultValueWhenFlag().isPresent()) {
				result.append("]"); // (close 3)
			}
			if (description.defaultValue().isPresent() && description.formal().length() > 0) {
				result.append("]"); // (close 1)
			}
			result.append("  "); // two spaces between each param for better legibility
		}
		result.append("\n\n");
	}

	private void documentOptions(AttributedStringBuilder result, List<ParameterDescription> parameterDescriptions) {
		if (!parameterDescriptions.isEmpty()) {
			result.append("OPTIONS", AttributedStyle.BOLD).append("\n");
		}
		for (ParameterDescription description : parameterDescriptions) {
			result.append("\t").append(description.keys().stream().collect(Collectors.joining(" or ")),
					AttributedStyle.BOLD);
			if (description.formal().length() > 0) {
				if (!description.keys().isEmpty()) {
					result.append("  ");
				}
				description.defaultValueWhenFlag().ifPresent(f -> result.append('['));
				appendUnderlinedFormal(result, description);
				description.defaultValueWhenFlag().ifPresent(f -> result.append(']'));
				result.append("\n\t");
			}
			else if (description.keys().size() > 1) {
				result.append("\n\t");
			}
			result.append("\t");
			result.append(description.help()).append('\n');
			// Optional parameter
			if (description.defaultValue().isPresent()) {
				result
						.append("\t\t[Optional, default = ", AttributedStyle.BOLD)
						.append(description.defaultValue().get(), AttributedStyle.BOLD.italic());
				description.defaultValueWhenFlag().ifPresent(
						s -> result.append(", or ", AttributedStyle.BOLD)
								.append(s, AttributedStyle.BOLD.italic())
								.append(" if used as a flag", AttributedStyle.BOLD));

				result.append("]", AttributedStyle.BOLD);
			} // Mandatory parameter, but with a default when used as a flag
			else if (description.defaultValueWhenFlag().isPresent()) {
				result
						.append("\t\t[Mandatory, default = ", AttributedStyle.BOLD)
						.append(description.defaultValueWhenFlag().get(), AttributedStyle.BOLD.italic())
						.append(" when used as a flag]", AttributedStyle.BOLD);
			} // true mandatory parameter
			else {
				result.append("\t\t[Mandatory]", AttributedStyle.BOLD);
			}
			result.append('\n');
			if (description.elementDescriptor() != null) {
				for (ConstraintDescriptor<?> constraintDescriptor : description.elementDescriptor()
						.getConstraintDescriptors()) {
					String friendlyConstraint = messageInterpolator.interpolate(
							constraintDescriptor.getMessageTemplate(), new DummyContext(constraintDescriptor));
					result.append("\t\t[" + friendlyConstraint + "]\n", AttributedStyle.BOLD);
				}
			}
			result.append('\n');
		}
	}

	private void documentAliases(AttributedStringBuilder result, String command, MethodTarget methodTarget) {
		Set<String> aliases = commandRegistry.listCommands().entrySet().stream()
				.filter(e -> e.getValue().equals(methodTarget))
				.map(Map.Entry::getKey)
				.filter(c -> !command.equals(c))
				.collect(toCollection(TreeSet::new));

		if (!aliases.isEmpty()) {
			result.append("ALSO KNOWN AS", AttributedStyle.BOLD).append("\n");
			for (String alias : aliases) {
				result.append('\t').append(alias).append('\n');
			}
		}
	}

	private void documentAvailability(AttributedStringBuilder result, MethodTarget methodTarget) {
		Availability availability = methodTarget.getAvailability();
		if (!availability.isAvailable()) {
			result.append("CURRENTLY UNAVAILABLE", AttributedStyle.BOLD).append("\n");
			result.append('\t').append("This command is currently not available because ")
					.append(availability.getReason())
					.append(".\n");
		}
	}

	private String first(List<String> keys) {
		return keys.iterator().next();
	}

	private CharSequence listCommands() {
		Map<String, MethodTarget> commandsByName = commandRegistry.listCommands();

		SortedMap<String, Map<String, MethodTarget>> commandsByGroupAndName = commandsByName.entrySet().stream()
				.collect(groupingBy(e -> e.getValue().getGroup(), TreeMap::new, // group by and sort by command group
						toMap(Entry::getKey, Entry::getValue)));

		AttributedStringBuilder result = new AttributedStringBuilder();
		result.append("AVAILABLE COMMANDS\n\n", AttributedStyle.BOLD);

		// display groups, sorted alphabetically, "Default" first
		commandsByGroupAndName.forEach((group, commandsInGroup) -> {
			result.append("".equals(group) ? "Default" : group, AttributedStyle.BOLD).append('\n');

			Map<MethodTarget, SortedSet<String>> commandNamesByMethod = commandsInGroup.entrySet().stream()
					.collect(groupingBy(Entry::getValue, // group by command method
							mapping(Entry::getKey, toCollection(TreeSet::new)))); // sort command names

			// display commands, sorted alphabetically by their first alias
			commandNamesByMethod.entrySet().stream().sorted(sortByFirstCommandName()).forEach(e -> {
				result
						.append(isAvailable(e.getKey()) ? "        " : "      * ")
						.append(String.join(", ", e.getValue()), AttributedStyle.BOLD)
						.append(": ")
						.append(e.getKey().getHelp())
						.append('\n');
			});

			result.append('\n');
		});

		if (commandsByName.values().stream().distinct().anyMatch(m -> !isAvailable(m))) {
			result.append("Commands marked with (*) are currently unavailable.\nType `help <command>` to learn more.\n\n");
		}

		return result;
	}

	private Comparator<Entry<MethodTarget, SortedSet<String>>> sortByFirstCommandName() {
		return Comparator.comparing(e -> e.getValue().first());
	}

	private boolean isAvailable(MethodTarget methodTarget) {
		return methodTarget.getAvailability().isAvailable();
	}

	private void appendUnderlinedFormal(AttributedStringBuilder result, ParameterDescription description) {
		for (char c : description.formal().toCharArray()) {
			if (c != ' ') {
				result.append("" + c, AttributedStyle.DEFAULT.underline());
			}
			else {
				result.append(c);
			}
		}
	}

	private List<ParameterDescription> getParameterDescriptions(MethodTarget methodTarget) {
		return Utils.createMethodParameters(methodTarget.getMethod())
				.flatMap(mp -> parameterResolvers.stream().filter(pr -> pr.supports(mp)).limit(1L)
						.flatMap(pr -> pr.describe(mp)))
				.collect(Collectors.toList());

	}

	private static class DummyContext implements MessageInterpolator.Context {

		private final ConstraintDescriptor<?> descriptor;

		private DummyContext(ConstraintDescriptor<?> descriptor) {
			this.descriptor = descriptor;
		}

		@Override
		public ConstraintDescriptor<?> getConstraintDescriptor() {
			return descriptor;
		}

		@Override
		public Object getValidatedValue() {
			return null;
		}

		@Override
		public <T> T unwrap(Class<T> type) {
			return null;
		}
	}

}
