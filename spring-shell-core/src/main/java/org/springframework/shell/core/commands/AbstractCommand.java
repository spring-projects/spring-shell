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

import java.util.ArrayList;
import java.util.List;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandAlias;
import org.springframework.shell.core.command.CommandOption;

/**
 * Base class helping to build shell components.
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

	private List<CommandOption> options = new ArrayList<>();

	private List<CommandAlias> aliases = new ArrayList<>();

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
	public List<CommandOption> getOptions() {
		return this.options;
	}

	public void setOptions(List<CommandOption> options) {
		this.options = options;
	}

	@Override
	public List<CommandAlias> getAliases() {
		return this.aliases;
	}

	public void setAliases(List<CommandAlias> aliases) {
		this.aliases = aliases;
	}

}
