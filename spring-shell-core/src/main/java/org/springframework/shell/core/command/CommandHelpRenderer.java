/*
 * Copyright 2026-present the original author or authors.
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

import org.springframework.shell.core.utils.Utils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Renders the detailed help message of a single command.
 * <p>
 * The same message is used by the {@code help <command>} command and by the implicit
 * {@code --help} option of every command, so that both report the command in the same
 * way.
 *
 * @author David Pilar
 * @since 4.0.4
 */
public final class CommandHelpRenderer {

	private CommandHelpRenderer() {
	}

	/**
	 * Render the help message of the given command. The message is made of a NAME
	 * section, a DESCRIPTION section holding the help text of the command when it defines
	 * one, a SYNOPSIS section and a section per options, arguments and aliases the
	 * command declares.
	 * @param command the command to render
	 * @return the help message of the command
	 */
	public static String renderHelp(Command command) {
		StringBuilder helpMessageBuilder = new StringBuilder();
		appendName(command, helpMessageBuilder);
		appendDescription(command, helpMessageBuilder);
		appendSynopsis(command, helpMessageBuilder);
		appendOptions(command, helpMessageBuilder);
		appendArguments(command, helpMessageBuilder);
		appendAliases(command, helpMessageBuilder);
		return helpMessageBuilder.toString();
	}

	private static void appendName(Command command, StringBuilder helpMessageBuilder) {
		helpMessageBuilder.append("NAME\n")
			.append("\t")
			.append(command.getName())
			.append(" - ")
			.append(command.getDescription())
			.append("\n\n");
	}

	private static void appendDescription(Command command, StringBuilder helpMessageBuilder) {
		String help = command.getHelp();
		if (!StringUtils.hasText(help)) {
			return;
		}
		helpMessageBuilder.append("DESCRIPTION\n");
		for (String line : help.lines().toList()) {
			helpMessageBuilder.append("\t").append(line).append("\n");
		}
		helpMessageBuilder.append("\n");
	}

	private static void appendSynopsis(Command command, StringBuilder helpMessageBuilder) {
		List<CommandOption> options = command.getOptions();
		List<CommandArgument> arguments = command.getArguments();
		helpMessageBuilder.append("SYNOPSIS\n").append("\t").append(command.getName());
		if (!options.isEmpty()) {
			for (CommandOption option : options) {
				helpMessageBuilder.append(" ");
				if (isRequired(option)) {
					helpMessageBuilder.append("[");
				}
				if (option.longName() != null) {
					helpMessageBuilder.append("--").append(option.longName());
				}
				else {
					helpMessageBuilder.append("-").append(option.shortName());
				}
				helpMessageBuilder.append(" ").append(option.type().getSimpleName());
				if (isRequired(option)) {
					helpMessageBuilder.append("]");
				}
			}
		}
		if (!arguments.isEmpty()) {
			for (CommandArgument argument : arguments) {
				helpMessageBuilder.append(" ");
				boolean hasDefaultValue = StringUtils.hasText(argument.defaultValue());
				if (hasDefaultValue) {
					helpMessageBuilder.append("[");
				}
				helpMessageBuilder.append("(").append(argument.type().getSimpleName());
				if (argument.variadic()) {
					helpMessageBuilder.append("...");
				}
				helpMessageBuilder.append(")");
				if (hasDefaultValue) {
					helpMessageBuilder.append("]");
				}
			}
		}
		helpMessageBuilder.append(" ").append("--help\n\n");
	}

	private static void appendOptions(Command command, StringBuilder helpMessageBuilder) {
		List<CommandOption> options = command.getOptions();
		helpMessageBuilder.append("OPTIONS\n");
		if (!options.isEmpty()) {
			for (CommandOption option : options) {
				helpMessageBuilder.append("\t");
				if (option.longName() != null) {
					helpMessageBuilder.append("--").append(option.longName());
				}
				if (option.shortName() != ' ') {
					helpMessageBuilder.append(" or -").append(option.shortName());
				}
				helpMessageBuilder.append(" ").append(option.type().getSimpleName()).append("\n");
				helpMessageBuilder.append("\t").append(option.description()).append("\n");
				if (isRequired(option)) {
					helpMessageBuilder.append("\t").append("[Mandatory]").append("\n\n");
				}
				else {
					helpMessageBuilder.append("\t").append("[Optional, default = ");
					String defaultValue = option.defaultValue();
					Class<?> optionType = option.type();
					if (defaultValue == null && optionType.isPrimitive()) {
						defaultValue = Utils.getDefaultValueForPrimitiveType(optionType).toString();
					}
					helpMessageBuilder.append(defaultValue).append("]\n\n");
				}
			}
		}
		helpMessageBuilder.append("\t--help or -h").append("\n");
		helpMessageBuilder.append("\thelp for ").append(command.getName()).append("\n");
		helpMessageBuilder.append("\t").append("[Optional]").append("\n").append("\n");
	}

	private static boolean isRequired(CommandOption option) {
		return Boolean.TRUE.equals(option.required());
	}

	private static void appendArguments(Command command, StringBuilder helpMessageBuilder) {
		List<CommandArgument> arguments = command.getArguments();
		if (!arguments.isEmpty()) {
			helpMessageBuilder.append("ARGUMENTS [Positional]\n");
			int index = 0;
			for (CommandArgument argument : arguments) {
				helpMessageBuilder.append("\t");
				helpMessageBuilder.append("[Index ").append(index++);
				if (argument.variadic()) {
					helpMessageBuilder.append("...");
				}
				helpMessageBuilder.append("]");
				helpMessageBuilder.append(" ").append(argument.type().getSimpleName()).append("\n");
				helpMessageBuilder.append("\t").append(argument.description()).append("\n");
				if (argument.variadic()) {
					// a variadic argument collects the remaining values, a default value
					// does not apply to it
					helpMessageBuilder.append("\n");
					continue;
				}
				String defaultValue = argument.defaultValue();
				helpMessageBuilder.append("\t").append("[default = ");
				Class<?> optionType = argument.type();
				if (defaultValue == null && optionType.isPrimitive()) {
					defaultValue = Utils.getDefaultValueForPrimitiveType(optionType).toString();
				}
				helpMessageBuilder.append(defaultValue).append("]\n\n");
			}
		}
	}

	private static void appendAliases(Command command, StringBuilder helpMessageBuilder) {
		List<String> aliases = command.getAliases();
		if (!aliases.isEmpty()) {
			helpMessageBuilder.append("ALIASES\n");
			helpMessageBuilder.append("\t").append(String.join(", ", aliases)).append("\n");
		}
	}

}
