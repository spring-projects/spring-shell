/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.standard.commands;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.standard.CommandValueProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.Utils;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class Help {

	private final List<ParameterResolver> parameterResolvers;

	private CommandRegistry commandRegistry;

	@Autowired
	public Help(List<ParameterResolver> parameterResolvers) {
		this.parameterResolvers = parameterResolvers;
	}

	@Autowired // ctor injection impossible b/c of circular dependency
	public void setCommandRegistry(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@ShellMethod(help = "Display help about available commands.", prefix = "-")
	public CharSequence help(
			@ShellOption(defaultValue = ShellOption.NULL,
					valueProvider = CommandValueProvider.class,
					value = {"-C", "--command"},
					help = "The command to obtain help for.") String command) throws IOException {
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

		// NAME
		AttributedStringBuilder result = new AttributedStringBuilder().append("\n\n");
		result.append("NAME", AttributedStyle.BOLD).append("\n\t");
		result.append(command).append(" - ").append(methodTarget.getHelp()).append("\n\n");

		// SYNOPSYS
		result.append("SYNOPSYS", AttributedStyle.BOLD).append("\n\t");
		result.append(command, AttributedStyle.BOLD);
		result.append(" ");

		List<ParameterDescription> parameterDescriptions = getParameterDescriptions(methodTarget);

		for (ParameterDescription description : parameterDescriptions) {

			if (description.defaultValue().isPresent()) {
				result.append("["); // Whole parameter is optional, as there is a default value (1)
			}
			List<String> keys = description.keys();
			if(!keys.isEmpty()) {
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
			if (description.defaultValue().isPresent()) {
				result.append("]"); // (close 1)
			}
			result.append("  "); // two spaces between each param for better legibility
		}
		result.append("\n\n");

		// OPTIONS
		if (!parameterDescriptions.isEmpty()) {
			result.append("OPTIONS", AttributedStyle.BOLD).append("\n");
		}
		for (ParameterDescription description : parameterDescriptions) {
			result.append("\t").append(description.keys().stream().collect(Collectors.joining(" or ")), AttributedStyle.BOLD);
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
			result.append(description.help());
			// Optional parameter
			if (description.defaultValue().isPresent()) {
				result
						.append("  [Optional, default = ", AttributedStyle.BOLD)
						.append(description.defaultValue().get(), AttributedStyle.BOLD.italic());
				description.defaultValueWhenFlag().ifPresent(
					s -> result.append(", or ", AttributedStyle.BOLD)
						.append(s, AttributedStyle.BOLD.italic())
					.append(" if used as a flag", AttributedStyle.BOLD)
				);

				result.append("]", AttributedStyle.BOLD);
			} // Mandatory parameter, but with a default when used as a flag
			else if (description.defaultValueWhenFlag().isPresent()) {
				result
					.append("  [Mandatory, default = ", AttributedStyle.BOLD)
					.append(description.defaultValueWhenFlag().get(), AttributedStyle.BOLD.italic())
					.append(" when used as a flag]", AttributedStyle.BOLD)
				;
			} // true mandatory parameter
			else {
				result.append("  [Mandatory]", AttributedStyle.BOLD);
			}
			result.append("\n\n");
		}

		// ALSO KNOWN AS
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

		result.append("\n");
		return result;
	}

	private String first(List<String> keys) {
		return keys.iterator().next();
	}

	private CharSequence listCommands() {
		Map<String, Set<String>> groupedByMethodTarget = commandRegistry.listCommands().entrySet().stream()
				.collect(Collectors.groupingBy(e -> e.getValue().getHelp(), // Use help() as the grouping key
						mapping(Map.Entry::getKey, toCollection(TreeSet::new)))); // accumulate the command 'names' into a sorted set

		// Then display commands, sorted alphabetically by their first alias
		AttributedStringBuilder result = new AttributedStringBuilder();
		result.append("AVAILABLE COMMANDS\n\n", AttributedStyle.BOLD);

		groupedByMethodTarget.entrySet().stream()
				.sorted(sortByFirstElement())
				.forEach(e -> result.append("\t")
								.append(e.getValue().stream().collect(Collectors.joining(", ")), AttributedStyle.BOLD)
								.append(": ")
								.append(e.getKey())
								.append('\n')
				);
		return result.append("\n");
	}

	private Comparator<Map.Entry<String, Set<String>>> sortByFirstElement() {
		return Comparator.comparing(e -> e.getValue().iterator().next());
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
			.flatMap(mp -> parameterResolvers.stream().filter(pr -> pr.supports(mp)).limit(1L).flatMap(pr -> pr.describe(mp)))
			.collect(Collectors.toList());

	}

}
