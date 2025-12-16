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
package org.springframework.shell.core.command;

import java.io.File;
import java.util.List;

import org.springframework.shell.core.FileInputProvider;
import org.springframework.shell.core.utils.CommandUtils;

/**
 * A command that can read and execute other commands from a file.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
public class Script extends AbstractCommand {

	public Script(String name, String description) {
		super(name, description);
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) throws Exception {
		List<CommandArgument> arguments = commandContext.parsedInput().arguments();
		File file = new File(arguments.get(0).value());
		try (FileInputProvider inputProvider = new FileInputProvider(file)) {
			String input;
			while ((input = inputProvider.readInput()) != null) {
				executeCommand(commandContext, input);
			}
		}
		return ExitStatus.OK;
	}

	private void executeCommand(CommandContext commandContext, String input) throws Exception {
		Command command = commandContext.commandRegistry().getCommandByName(input);
		if (command == null) {
			String availableCommands = CommandUtils.formatAvailableCommands(commandContext.commandRegistry());
			throw new CommandNotFoundException("No command found for name: " + input + ". " + availableCommands);
		}
		CommandContext singleCommandContext = new CommandContext(commandContext.parsedInput(),
				commandContext.commandRegistry(), commandContext.outputWriter());
		command.execute(singleCommandContext);
	}

}
