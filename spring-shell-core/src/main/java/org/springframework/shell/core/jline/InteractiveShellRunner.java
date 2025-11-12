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

package org.springframework.shell.core.jline;

import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jline.terminal.Terminal;

import org.springframework.shell.core.Input;
import org.springframework.shell.core.InputProvider;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistry;

/**
 * A {@link ShellRunner} that bootstraps the shell in interactive mode. It requires an
 * {@link InputProvider} to read user inputs, a {@link Terminal} to write output to the
 * user and a {@link CommandRegistry} to look up commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Chris Bono
 * @author Mahmoud Ben Hassine
 */
public class InteractiveShellRunner implements ShellRunner {

	private static final Log log = LogFactory.getLog(InteractiveShellRunner.class);

	private final InputProvider inputProvider;

	private final Terminal terminal;

	private final CommandRegistry commandRegistry;

	public InteractiveShellRunner(InputProvider inputProvider, Terminal terminal, CommandRegistry commandRegistry) {
		this.inputProvider = inputProvider;
		this.terminal = terminal;
		this.commandRegistry = commandRegistry;
	}

	@Override
	public void run(String[] args) throws Exception {
		while (true) {
			Input input = this.inputProvider.readInput();
			if (input == Input.INTERRUPTED || input == Input.EMPTY) {
				break;
			}
			if (input.rawText().equalsIgnoreCase("quit") || input.rawText().equalsIgnoreCase("exit")) {
				break;
			}
			if (input == null || input.rawText().isEmpty() || input.words().isEmpty()) {
				// Ignore empty lines
				continue;
			}
			String commandName = input.words().get(0);
			Command command = this.commandRegistry.getCommandByName(commandName);
			if (command == null) {
				String availableCommands = getAvailableCommands();
				this.terminal.writer()
					.println(
							"No command found for name: " + commandName + ". Available commands: " + availableCommands);
				this.terminal.writer().flush();
				continue;
			}
			log.debug(String.format("Evaluate input with line=[%s], command=[%s]", input.rawText(), command));
			CommandContext commandContext = new CommandContext(input.words(), this.commandRegistry, this.terminal);
			try {
				command.execute(commandContext);
			}
			catch (Exception exception) {
				this.terminal.writer().append(exception.getMessage());
				this.terminal.writer().flush();
			}
		}
	}

	private String getAvailableCommands() {
		return this.commandRegistry.getCommands()
			.stream()
			.map(Command::getName)
			.sorted()
			.collect(Collectors.joining(", "));
	}

}
