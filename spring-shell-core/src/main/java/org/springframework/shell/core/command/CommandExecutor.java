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

import java.io.PrintWriter;
import java.util.List;

/**
 * Executes commands based on parsed input.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class CommandExecutor {

	private final CommandRegistry commandRegistry;

	/**
	 * Create a new {@link CommandExecutor} instance.
	 * @param commandRegistry the command registry
	 */
	public CommandExecutor(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	/**
	 * Execute a command based on the given command context.
	 * @param commandContext the command context
	 * @return the exit status of the command execution
	 * @throws CommandNotFoundException if the command is not found
	 * @throws CommandExecutionException if an error occurs during command execution
	 */
	public ExitStatus execute(CommandContext commandContext)
			throws CommandNotFoundException, CommandExecutionException {
		ParsedInput parsedInput = commandContext.parsedInput();
		String commandName = parsedInput.commandName();
		if (!parsedInput.subCommands().isEmpty()) {
			commandName += " " + String.join(" ", parsedInput.subCommands());
		}
		Command command = this.commandRegistry.getCommandByName(commandName);
		if (command == null) {
			List<Command> candidateSubCommands = this.commandRegistry.getCommandsByPrefix(commandName);
			if (!candidateSubCommands.isEmpty()) {
				PrintWriter outputWriter = commandContext.outputWriter();
				outputWriter.println("Available sub-commands for '" + commandName + "':");
				for (Command candidateSubCommand : candidateSubCommands) {
					outputWriter
						.println("  " + candidateSubCommand.getName() + " - " + candidateSubCommand.getDescription());
				}
				outputWriter.flush();
				return ExitStatus.OK;
			}
			else {
				throw new CommandNotFoundException(commandName);
			}
		}
		try {
			return command.execute(commandContext);
		}
		catch (Exception exception) {
			throw new CommandExecutionException("Unable to execute command " + commandName, exception);
		}
	}

}
