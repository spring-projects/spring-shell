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

import org.jline.terminal.Terminal;

import org.springframework.shell.core.Input;
import org.springframework.shell.core.InputProvider;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.*;

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

	private final InputProvider inputProvider;

	private CommandParser commandParser = new DefaultCommandParser();

	private final CommandExecutor commandExecutor;

	public InteractiveShellRunner(InputProvider inputProvider, Terminal terminal, CommandRegistry commandRegistry) {
		this.inputProvider = inputProvider;
		this.commandExecutor = new CommandExecutor(commandRegistry, terminal);
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
			ParsedInput parsedInput = commandParser.parse(input);
			this.commandExecutor.execute(parsedInput);
		}
	}

	public void setCommandParser(CommandParser commandParser) {
		this.commandParser = commandParser;
	}

}
