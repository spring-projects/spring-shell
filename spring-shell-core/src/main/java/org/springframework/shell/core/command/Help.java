/*
 * Copyright 2017-present the original author or authors.
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

import java.io.PrintWriter;
import java.util.List;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class Help extends AbstractCommand {

	public Help() {
		super("help", "Show help about available commands", "Built-In Commands");
	}

	@Override
	public String getHelp() {
		return "help [command]\n\n"
				+ "Display help about available commands. If a command is specified, display detailed help about that command.";
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) throws Exception {
		PrintWriter outputWriter = commandContext.outputWriter();
		CommandRegistry commandRegistry = commandContext.commandRegistry();
		String helpMessage = Utils.formatAvailableCommands(commandRegistry);
		List<CommandArgument> arguments = commandContext.parsedInput().arguments();
		String commandName = String.join(" ", arguments.stream().map(CommandArgument::value).toList());
		Command command = commandRegistry.getCommandByName(commandName);
		if (command != null) {
			helpMessage = getHelpMessageForCommand(command);
		}
		else {
			Command aliasCommand = commandRegistry.getCommandByAlias(commandName);
			if (aliasCommand != null) {
				helpMessage = getHelpMessageForCommand(aliasCommand);
			}
		}
		outputWriter.println(helpMessage);
		outputWriter.flush();
		return ExitStatus.OK;
	}

	private String getHelpMessageForCommand(Command command) {
		StringBuilder helpMessageBuilder = new StringBuilder();
		appendName(command, helpMessageBuilder);
		appendSynopsis(command, helpMessageBuilder);
		appendOptions(command, helpMessageBuilder);
		appendArguments(command, helpMessageBuilder);
		appendAliases(command, helpMessageBuilder);
		return helpMessageBuilder.toString();
	}

	private void appendName(Command command, StringBuilder helpMessageBuilder) {
		helpMessageBuilder.append("NAME\n")
			.append("\t")
			.append(command.getName())
			.append(" - ")
			.append(command.getDescription())
			.append("\n\n");
	}

	private void appendSynopsis(Command command, StringBuilder helpMessageBuilder) {
		List<CommandOption> options = command.getOptions();
		List<CommandArgument> arguments = command.getArguments();
		helpMessageBuilder.append("SYNOPSIS\n").append("\t").append(command.getName());
		if (!options.isEmpty()) {
			for (CommandOption option : options) {
				helpMessageBuilder.append(" ");
				if (option.required()) {
					helpMessageBuilder.append("[");
				}
				if (option.longName() != null) {
					helpMessageBuilder.append("--").append(option.longName());
				}
				else {
					helpMessageBuilder.append("-").append(option.shortName());
				}
				helpMessageBuilder.append(" ").append(option.type().getSimpleName());
				if (option.required()) {
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
				helpMessageBuilder.append("(").append(argument.type().getSimpleName()).append(")");
				if (hasDefaultValue) {
					helpMessageBuilder.append("]");
				}
			}
		}
		helpMessageBuilder.append(" ").append("--help\n\n");
	}

	private void appendOptions(Command command, StringBuilder helpMessageBuilder) {
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
				if (option.required()) {
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

	private void appendArguments(Command command, StringBuilder helpMessageBuilder) {
		List<CommandArgument> arguments = command.getArguments();
		if (!arguments.isEmpty()) {
			helpMessageBuilder.append("ARGUMENTS [Positional]\n");
			int index = 0;
			for (CommandArgument argument : arguments) {
				helpMessageBuilder.append("\t");
				helpMessageBuilder.append("[Index ").append(index++).append("]");
				helpMessageBuilder.append(" ").append(argument.type().getSimpleName()).append("\n");
				helpMessageBuilder.append("\t").append(argument.description()).append("\n");
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
