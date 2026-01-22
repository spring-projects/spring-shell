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

import java.io.PrintWriter;
import java.util.List;

import org.springframework.shell.core.utils.Utils;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class Help implements Command {

	@Override
	public String getDescription() {
		return "Display help about available commands";
	}

	@Override
	public String getGroup() {
		return "Built-In Commands";
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
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
		helpMessageBuilder.append("SYNOPSIS\n").append("\t").append(command.getName()).append(" ");
		if (!options.isEmpty()) {
			for (CommandOption option : options) {
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
					helpMessageBuilder.append("] ");
				}
			}
			helpMessageBuilder.append(" --help\n\n");
		}
	}

	private void appendOptions(Command command, StringBuilder helpMessageBuilder) {
		List<CommandOption> options = command.getOptions();
		if (!options.isEmpty()) {
			helpMessageBuilder.append("OPTIONS\n");
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
					helpMessageBuilder.append("\t").append("[Optional");
					if (option.defaultValue() != null) {
						helpMessageBuilder.append(", default = ").append(option.defaultValue()).append("]\n\n");
					}
					else {
						helpMessageBuilder.append("]\n\n");
					}
				}
			}
			helpMessageBuilder.append("\t--help or -h").append("\n");
			helpMessageBuilder.append("\thelp for ").append(command.getName()).append("\n");
			helpMessageBuilder.append("\t").append("[Optional]").append("\n").append("\n");
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
