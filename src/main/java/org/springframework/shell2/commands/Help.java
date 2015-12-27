/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.shell2.commands;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.shell2.MethodTarget;
import org.springframework.shell2.ParameterDescription;
import org.springframework.shell2.ParameterResolver;
import org.springframework.shell2.Shell;
import org.springframework.shell2.standard.ShellComponent;
import org.springframework.shell2.standard.ShellMethod;
import org.springframework.shell2.standard.ShellOption;
import org.springframework.shell2.Utils;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class Help {

	private final List<ParameterResolver> parameterResolvers;

	private Shell shell;

	@Autowired
	public Help(List<ParameterResolver> parameterResolvers) {
		this.parameterResolvers = parameterResolvers;
	}

	@Autowired // ctor injection impossible b/c of circular dependency
	public void setShell(Shell shell) {
		this.shell = shell;
	}

	@ShellMethod(help = "Display help about available commands.", prefix = "-")
	public CharSequence help(
			@ShellOption(defaultValue = ShellOption.NULL,
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
		MethodTarget methodTarget = shell.listCommands().get(command);
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
				result.append("[");
			}
			if (!description.mandatoryKey()) {
				result.append("[");
			}
			result.append(description.keys().iterator().next(), AttributedStyle.BOLD);
			if (!description.mandatoryKey()) {
				result.append("]");
				if (!description.formal().isEmpty()) {
					result.append(" ");
				}
			}
			appendUnderlinedFormal(result, description);
			if (description.defaultValue().isPresent()) {
				result.append("]");
			}
			result.append("  ");
		}
		result.append("\n\n");

		// OPTIONS
		if (!parameterDescriptions.isEmpty()) {
			result.append("OPTIONS", AttributedStyle.BOLD).append("\n");
		}
		for (ParameterDescription description : parameterDescriptions) {
			result.append("\t").append(description.keys().stream().collect(Collectors.joining(" or ")), AttributedStyle.BOLD);
			if (description.formal().length() > 0) {
				result.append("  ");
				appendUnderlinedFormal(result, description);
				result.append("\n\t");
			}
			else if (description.keys().size() > 1) {
				result.append("\n\t");
			}
			result.append("\t");
			result.append(description.help());
			if (description.defaultValue().isPresent()) {
				result
						.append("  [Optional, default = ", AttributedStyle.BOLD)
						.append(description.defaultValue().get(), AttributedStyle.BOLD.italic())
						.append("]", AttributedStyle.BOLD);
			} else {
				result.append("  [Mandatory]", AttributedStyle.BOLD);
			}
			result.append("\n\n");
		}

		// ALSO KNOWN AS
		Set<String> aliases = shell.listCommands().entrySet().stream()
				.filter(e -> e.getValue().equals(methodTarget))
				.map(e -> e.getKey())
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

	private CharSequence listCommands() {
		Map<String, Set<String>> groupedByMethodTarget = shell.listCommands().entrySet().stream()
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
		return (e1, e2) -> e1.getValue().iterator().next().compareTo(e2.getValue().iterator().next());
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
		Parameter[] parameters = methodTarget.getMethod().getParameters();
		List<ParameterDescription> parameterDescriptions = new ArrayList<>();
		for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
			for (ParameterResolver parameterResolver : parameterResolvers) {
				MethodParameter methodParameter = Utils.createMethodParameter(methodTarget.getMethod(), i);
				if (parameterResolver.supports(methodParameter)) {
					parameterDescriptions.add(parameterResolver.describe(methodParameter));
					break;
				}
			}
		}
		return parameterDescriptions;
	}

}
