/*
 * Copyright 2018-2025 the original author or authors.
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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jline.reader.Parser;
import org.jline.terminal.Terminal;

import org.springframework.shell.core.Input;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandNotFoundException;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.util.ObjectUtils;

/**
 * A {@link ShellRunner} that looks for process arguments that start with {@literal @},
 * which are then interpreted as references to script files to run and exit.
 *
 * @author Eric Bottard
 */
// tag::documentation[]
public class ScriptShellRunner implements ShellRunner {

	// end::documentation[]

	/**
	 * The precedence at which this runner is ordered by the DefaultApplicationRunner -
	 * which also controls the order it is consulted on the ability to handle the current
	 * shell.
	 */
	private final Parser parser;

	private final Terminal terminal;

	private final CommandRegistry commandRegistry;

	public ScriptShellRunner(Parser parser, Terminal terminal, CommandRegistry commandRegistry) {
		this.parser = parser;
		this.terminal = terminal;
		this.commandRegistry = commandRegistry;
	}

	@Override
	public void run(String[] args) throws Exception {
		String[] sourceArgs = args;
		if (ObjectUtils.isEmpty(sourceArgs)) {
			return;
		}
		if (!(sourceArgs[0].startsWith("@") && sourceArgs[0].length() > 1)) {
			return;
		}

		List<File> scriptsToRun = Arrays.asList(args)
			.stream()
			.filter(s -> s.startsWith("@"))
			.map(s -> new File(s.substring(1)))
			.collect(Collectors.toList());

		for (File file : scriptsToRun) {
			try (Reader reader = new FileReader(file);
					FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {
				execute(inputProvider);
			}
		}
	}

	private void execute(FileInputProvider inputProvider) throws Exception {
		while (true) {
			Input input = inputProvider.readInput();
			if (input == Input.INTERRUPTED || input == Input.EMPTY) {
				break;
			}
			if (input == null || input.words().isEmpty()) {
				// Ignore empty lines
				continue;
			}
			String commandName = input.words().get(0);
			Command command = this.commandRegistry.getCommandByName(commandName);
			if (command == null) {
				String availableCommands = getAvailableCommands();
				throw new CommandNotFoundException(
						"No command found for name: " + commandName + ". Available commands: " + availableCommands);
			}
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
