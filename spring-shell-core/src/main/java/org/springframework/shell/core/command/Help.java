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

import org.springframework.shell.core.utils.Utils;

import java.io.PrintWriter;
import java.util.List;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 * @author David Pilar
 */
public class Help extends AbstractCommand {

	public Help() {
		super("help", "Show help about available commands", "Built-In Commands");
	}

	@Override
	public String getHelp() {
		return "Display help about available commands. If a command is specified, display detailed help about that command.";
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) throws Exception {
		PrintWriter outputWriter = commandContext.outputWriter();
		CommandRegistry commandRegistry = commandContext.commandRegistry();
		String helpMessage = Utils.formatAvailableCommands(commandRegistry);
		List<CommandArgument> arguments = commandContext.parsedInput().arguments();
		String commandName = String.join(" ", arguments.stream().map(CommandArgument::value).toList());
		Command command = commandRegistry.getCommandByName(commandName);
		if (command != null) {
			helpMessage = CommandHelpRenderer.render(command);
		}
		else {
			Command aliasCommand = commandRegistry.getCommandByAlias(commandName);
			if (aliasCommand != null) {
				helpMessage = CommandHelpRenderer.render(aliasCommand);
			}
		}
		outputWriter.println(helpMessage);
		outputWriter.flush();
		return ExitStatus.OK;
	}

}
