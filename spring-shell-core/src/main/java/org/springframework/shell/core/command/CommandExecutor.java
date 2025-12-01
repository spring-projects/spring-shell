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
package org.springframework.shell.core.command;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jline.terminal.Terminal;

import org.springframework.shell.core.utils.CommandUtils;

/**
 * Executes commands based on parsed input.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class CommandExecutor {

	private static final Log log = LogFactory.getLog(CommandExecutor.class);

	private final Terminal terminal;

	private final CommandRegistry commandRegistry;

	public CommandExecutor(CommandRegistry commandRegistry, Terminal terminal) {
		this.commandRegistry = commandRegistry;
		this.terminal = terminal;
	}

	public void execute(ParsedInput parsedInput) {
		String commandName = parsedInput.commandName();
		if (!parsedInput.subCommands().isEmpty()) {
			commandName += " " + String.join(" ", parsedInput.subCommands());
		}
		Command command = this.commandRegistry.getCommandByName(commandName);
		// the user typed a non recognized command or a root command with subcommands
		if (command == null) {
			// check if there are subcommands for the given root command
			List<String> candidateSubCommands = CommandUtils.getAvailableCommands(this.commandRegistry)
				.stream()
				.filter(candidateSubCommand -> candidateSubCommand.startsWith(parsedInput.commandName()))
				.toList();
			if (!candidateSubCommands.isEmpty() && parsedInput.subCommands().isEmpty()) {
				List<String> availableSubCommands = CommandUtils.getAvailableSubCommands(commandName,
						this.commandRegistry);
				this.terminal.writer().println("Available sub commands for " + commandName + ": ");
				for (String availableSubCommand : availableSubCommands) {
					this.terminal.writer()
						.println("  " + availableSubCommand + ": "
								+ this.commandRegistry.getCommandByName(commandName + " " + availableSubCommand)
									.getDescription());
				}
			}
			else {
				// the user typed an incorrect command, print general available
				// commands
				String availableCommands = CommandUtils.formatAvailableCommands(this.commandRegistry);
				this.terminal.writer().println("No command found for name: " + commandName + ". " + availableCommands);
			}
			this.terminal.writer().flush();
			return;
		}
		log.debug(String.format("Evaluate input for command=[%s]", command));
		CommandContext commandContext = new CommandContext(parsedInput.options(), parsedInput.arguments(),
				this.commandRegistry, this.terminal);
		try {
			command.execute(commandContext);
		}
		catch (Exception exception) {
			this.terminal.writer().println(exception.getMessage());
			this.terminal.writer().flush();
		}
	}

}
