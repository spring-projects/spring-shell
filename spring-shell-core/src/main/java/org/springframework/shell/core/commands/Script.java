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
package org.springframework.shell.core.commands;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.jline.reader.Parser;

import org.springframework.shell.core.Input;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandNotFoundException;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.jline.FileInputProvider;

/**
 * A command that can read and execute other commands from a file.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
public class Script extends AbstractCommand {

	private final Parser parser;

	public Script(String name, String description, Parser parser) {
		super(name, description);
		this.parser = parser;
	}

	@Override
	public void execute(CommandContext commandContext) throws Exception {
		List<CommandOption> options = getOptions();
		File file = null;// TODO get file name from options
		Reader reader = new FileReader(file);
		FileInputProvider inputProvider = new FileInputProvider(reader, parser);
		Input input;
		while ((input = inputProvider.readInput()) != null) {
			executeCommand(commandContext, input);
		}
	}

	private void executeCommand(CommandContext commandContext, Input input) throws Exception {
		String commandName = input.words().get(0);
		Command command = commandContext.commandRegistry().getCommandByName(commandName);
		if (command == null) {
			String availableCommands = getAvailableCommands(commandContext);
			throw new CommandNotFoundException(
					"No command found for name: " + commandName + ". Available commands: " + availableCommands);
		}
		CommandContext singleCommandContext = new CommandContext(input.words(), commandContext.commandRegistry(),
				commandContext.terminal());
		command.execute(singleCommandContext);
	}

	private String getAvailableCommands(CommandContext commandContext) {
		return commandContext.commandRegistry()
			.getCommands()
			.stream()
			.map(Command::getName)
			.sorted()
			.collect(Collectors.joining(", "));
	}

}
