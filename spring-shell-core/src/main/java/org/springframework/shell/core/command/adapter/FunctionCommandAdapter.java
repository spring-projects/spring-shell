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
package org.springframework.shell.core.command.adapter;

import java.io.PrintWriter;
import java.util.function.Function;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.AbstractCommand;

/**
 * An adapter to adapt a {@link Function} as a command. The String output of the function
 * is printed to the command output.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class FunctionCommandAdapter extends AbstractCommand {

	Function<CommandContext, String> commandFunction;

	/**
	 * Create a new {@link FunctionCommandAdapter}.
	 * @param name the name of the command
	 * @param description the description of the command
	 * @param group the group of the command
	 * @param help the help text of the command
	 * @param hidden whether the command is hidden or not
	 * @param commandFunction the function to adapt as a command
	 */
	public FunctionCommandAdapter(String name, String description, String group, String help, boolean hidden,
			Function<CommandContext, String> commandFunction) {
		super(name, description, group, help, hidden);
		this.commandFunction = commandFunction;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		String commandOutput = this.commandFunction.apply(commandContext);
		PrintWriter outputWriter = commandContext.outputWriter();
		outputWriter.println(commandOutput);
		outputWriter.flush();
		return ExitStatus.OK;
	}

}
