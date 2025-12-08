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
package org.springframework.shell.core.commands;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.ExitStatus;

/**
 * Base class helping to build shell commands.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public abstract class AbstractCommand implements Command {

	private final String name;

	private final String description;

	private final String help;

	private final String group;

	private List<String> aliases = new ArrayList<>();

	public AbstractCommand(String name, String description) {
		this(name, description, "", "");
	}

	public AbstractCommand(String name, String description, String group) {
		this(name, description, group, "");
	}

	public AbstractCommand(String name, String description, String group, String help) {
		this.name = name;
		this.description = description;
		this.group = group;
		this.help = help;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getHelp() {
		return this.help;
	}

	@Override
	public List<String> getAliases() {
		return this.aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
		List<CommandOption> options = commandContext.parsedInput().options();
		if (options.size() == 1 && isHelp(options.get(0))) {
			println(getHelp(), commandContext);
			return ExitStatus.OK;
		}
		return doExecute(commandContext);
	}

	protected void println(String message, CommandContext commandContext) {
		PrintWriter outputWriter = commandContext.outputWriter();
		outputWriter.println(message);
		outputWriter.flush();

	}

	protected boolean isHelp(CommandOption option) {
		return option.longName().equalsIgnoreCase("help") || option.shortName() == 'h';
	}

	public abstract ExitStatus doExecute(CommandContext commandContext) throws Exception;

}
