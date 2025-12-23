/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.shell.core.InputReader;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandExecutor;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ParsedInput;

/**
 * Client for shell session which can be used as a programmatic way to interact with a
 * shell application. In a typical test, it is required to send a command to the shell as
 * if a user typed it and then verify the shell output.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
public class ShellTestClient {

	private final CommandParser commandParser;

	private final CommandRegistry commandRegistry;

	private final CommandExecutor commandExecutor;

	/**
	 * Create a {@link ShellTestClient} using the given {@link CommandParser} and
	 * {@link CommandRegistry}.
	 * @param commandParser the command parser
	 * @param commandRegistry the command registry
	 */
	public ShellTestClient(CommandParser commandParser, CommandRegistry commandRegistry) {
		this.commandParser = commandParser;
		this.commandRegistry = commandRegistry;
		this.commandExecutor = new CommandExecutor(commandRegistry);
	}

	/**
	 * Execute a command and write its result to the shell screen.
	 * @param input the raw input command
	 * @return the shell screen after command execution
	 * @throws Exception if an error occurred during command execution
	 */
	public ShellScreen sendCommand(String input) throws Exception {
		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = this.commandParser.parse(input);
		PrintWriter outputWriter = new PrintWriter(stringWriter);
		InputReader inputReader = new InputReader() {
		};
		CommandContext commandContext = new CommandContext(parsedInput, this.commandRegistry, outputWriter,
				inputReader);
		this.commandExecutor.execute(commandContext);
		return ShellScreen.of(stringWriter.toString().lines().toList());
	}

}
