/*
 * Copyright 2017-2023 the original author or authors.
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

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public class Help implements Command {

	@Override
	public void execute(CommandContext commandContext) throws Exception {
		Set<Command> availableCommands = commandContext.commandRegistry().getCommands();
		// FIXME render message from template
		// TODO do we really need a template engine? would Java String templates be
		// enough?
		String helpMessage = "Available commands: "
				+ availableCommands.stream().map(Command::getName).sorted().collect(Collectors.joining(", "));
		commandContext.terminal().writer().println(helpMessage);
		commandContext.terminal().flush();
	}

}
