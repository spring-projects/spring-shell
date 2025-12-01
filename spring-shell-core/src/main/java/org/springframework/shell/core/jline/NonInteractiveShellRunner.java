/*
 * Copyright 2021-present the original author or authors.
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

import org.jline.reader.Parser;
import org.jline.terminal.Terminal;

import org.springframework.shell.core.Input;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.CommandExecutor;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;
import org.springframework.shell.core.command.ParsedInput;
import org.springframework.util.ObjectUtils;

/**
 * A {@link ShellRunner} that executes commands without entering interactive shell mode.
 * It can process a single command, or a script of commands defined in a file. The script
 * file is specified by prefixing the file name with the special character {@literal @}.
 *
 * @author Janne Valkealahti
 * @author Chris Bono
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class NonInteractiveShellRunner implements ShellRunner {

	private final Parser parser;

	private final Terminal terminal;

	private CommandParser commandParser = new DefaultCommandParser();

	private final CommandExecutor commandExecutor;

	public NonInteractiveShellRunner(Parser parser, Terminal terminal, CommandRegistry commandRegistry) {
		this.parser = parser;
		this.terminal = terminal;
		this.commandExecutor = new CommandExecutor(commandRegistry, terminal);
	}

	@Override
	public void run(String[] args) throws Exception {
		if (ObjectUtils.isEmpty(args)) {
			terminal.writer().println("No command or script specified for non-interactive mode.");
			terminal.writer().flush();
			return;
		}
		if (isScript(args[0])) {
			executeScripts(args);
		}
		else {
			executeCommand(args);
		}
	}

	private static boolean isScript(String arg) {
		return arg.startsWith("@");
	}

	// FIXME should this throw Exceptions?
	private void executeScripts(String[] args) throws Exception {
		List<File> scriptsToRun = Arrays.stream(args)
			.filter(s -> s.startsWith("@"))
			.map(s -> new File(s.substring(1)))// TODO taken from v3, should we delete the
												// file after execution?
			.toList();

		for (File script : scriptsToRun) {
			executeScript(script);
		}
	}

	// FIXME should this throw Exceptions?
	private void executeScript(File script) throws Exception {
		try (Reader reader = new FileReader(script);
				FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {
			while (true) {
				Input input = inputProvider.readInput();
				if (input == Input.INTERRUPTED || input == Input.EMPTY) {
					break;
				}
				if (input == null || input.words().isEmpty()) {
					// Ignore empty lines
					continue;
				}
				ParsedInput parsedInput = commandParser.parse(input);
				this.commandExecutor.execute(parsedInput);
			}
		}
	}

	private void executeCommand(String[] args) {
		String primaryCommand = String.join(" ", args);
		ParsedInput parsedInput = commandParser.parse(() -> primaryCommand);
		this.commandExecutor.execute(parsedInput);
	}

	public void setCommandParser(CommandParser commandParser) {
		this.commandParser = commandParser;
	}

}
