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

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.utils.CommandUtils;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class Help implements Command {

	@Override
	public String getDescription() {
		return "Display help about available commands";
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
		String helpMessage = CommandUtils.getAvailableCommands(commandContext.commandRegistry());

		commandContext.terminal().writer().println(helpMessage);
		commandContext.terminal().flush();
		return ExitStatus.OK;
	}

}
