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
package org.springframework.shell.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandExecutor;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.ParsedInput;
import org.springframework.util.ObjectUtils;

/**
 * A {@link ShellRunner} that executes commands without entering interactive shell mode.
 * It can process a single command, or a script of commands defined in a file. The script
 * file is specified by prefixing the file name with the special character {@literal @}.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class NonInteractiveShellRunner implements ShellRunner {

	private static final Log log = LogFactory.getLog(NonInteractiveShellRunner.class);

	private final CommandParser commandParser;

	private final CommandExecutor commandExecutor;

	private final CommandRegistry commandRegistry;

	// Use a no-op PrintWriter since output is not needed in non-interactive mode
	private final PrintWriter outputWriter = new PrintWriter(PrintWriter.nullWriter());

	// Use a no-op InputReader since input is not needed in non-interactive mode
	private final InputReader inputReader = new InputReader() {
	};

	/**
	 * Create a new {@link NonInteractiveShellRunner} instance.
	 * @param commandParser the command parser
	 * @param commandRegistry the command registry
	 */
	public NonInteractiveShellRunner(CommandParser commandParser, CommandRegistry commandRegistry) {
		this.commandParser = commandParser;
		this.commandRegistry = commandRegistry;
		this.commandExecutor = new CommandExecutor(commandRegistry);
	}

	@Override
	public void run(String[] args) throws Exception {
		if (ObjectUtils.isEmpty(args)) {
			log.error(
					"In non interactive mode, it expected to have at least one argument: the command to execute or the script file");
			// TODO return or System.exit(1) or throw an exception?
		}
		else if (args[0].startsWith("@")) {
			File script = new File(args[0].substring(1));
			executeScript(script);
		}
		else {
			executeCommand(String.join(" ", args));
		}
	}

	private void executeScript(File script) {
		try (FileInputProvider inputProvider = new FileInputProvider(script)) {
			executeScript(inputProvider);
		}
		catch (IOException e) {
			log.error("Unable to locate script file", e);
		}
	}

	private void executeScript(InputProvider inputProvider) {
		while (true) {
			String input;
			try {
				input = inputProvider.readInput();
			}
			catch (Exception e) {
				log.error("Unable to read command", e);
				break;
			}
			if (input == null) {
				// break on end of file
				break;
			}
			if (input.isEmpty()) {
				// ignore empty lines
				continue;
			}
			ParsedInput parsedInput;
			try {
				parsedInput = this.commandParser.parse(input);
			}
			catch (Exception exception) {
				log.error("Command " + input + " parsed with error: " + exception.getMessage()
						+ ". Skipping next commands in the script");
				break;
			}
			CommandContext commandContext = new CommandContext(parsedInput, this.commandRegistry, this.outputWriter,
					this.inputReader);
			ExitStatus exitStatus = this.commandExecutor.execute(commandContext);
			if (ExitStatus.OK.code() != exitStatus.code()) { // business error
				log.error("Command " + parsedInput.commandName() + " returned an error: " + exitStatus.description()
						+ ". Skipping next commands in the script");
				break;
			}
		}
	}

	private void executeCommand(String primaryCommand) {
		ParsedInput parsedInput;
		try {
			parsedInput = this.commandParser.parse(primaryCommand);
		}
		catch (Exception exception) {
			log.error("Command " + primaryCommand + " parsed with error: " + exception.getMessage());
			return;
		}
		CommandContext commandContext = new CommandContext(parsedInput, this.commandRegistry, this.outputWriter,
				this.inputReader);
		ExitStatus exitStatus = this.commandExecutor.execute(commandContext);
		if (ExitStatus.OK.code() != exitStatus.code()) {
			log.error("Command " + parsedInput.commandName() + " returned an error: " + exitStatus.description());
		}
	}

}
