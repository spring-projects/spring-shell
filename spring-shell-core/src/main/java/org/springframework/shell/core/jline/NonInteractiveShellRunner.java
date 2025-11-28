/*
 * Copyright 2021-2025 the original author or authors.
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
package org.springframework.shell.core.jline;

import org.jline.terminal.Terminal;

import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.*;
import org.springframework.shell.core.utils.CommandUtils;

/**
 * A {@link ShellRunner} that executes a command without entering interactive shell mode.
 *
 * @author Janne Valkealahti
 * @author Chris Bono
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class NonInteractiveShellRunner implements ShellRunner {

	private final String primaryCommand;

	private final Terminal terminal;

	private final CommandRegistry commandRegistry;

	private CommandParser commandParser = new DefaultCommandParser();

	public NonInteractiveShellRunner(String primaryCommand, Terminal terminal, CommandRegistry commandRegistry) {
		this.primaryCommand = primaryCommand;
		this.terminal = terminal;
		this.commandRegistry = commandRegistry;
	}

	@Override
	public void run(String[] args) throws Exception {
		ParsedInput parsedInput = commandParser.parse(() -> primaryCommand);
		String commandName = parsedInput.commandName();
		if (!parsedInput.subCommands().isEmpty()) {
			commandName += " " + String.join(" ", parsedInput.subCommands());
		}
		Command command = this.commandRegistry.getCommandByName(commandName);
		if (command == null) {
			String availableCommands = CommandUtils.formatAvailableCommands(commandRegistry);
			throw new CommandNotFoundException(
					"No command found for name: " + primaryCommand + ". " + availableCommands);
		}
		CommandContext commandContext = new CommandContext(parsedInput.options(), parsedInput.arguments(),
				this.commandRegistry, this.terminal);
		try {
			command.execute(commandContext);
		}
		catch (Exception exception) {
			this.terminal.writer().append(exception.getMessage());
			this.terminal.writer().flush();
		}
	}

	public void setCommandParser(CommandParser commandParser) {
		this.commandParser = commandParser;
	}

}
