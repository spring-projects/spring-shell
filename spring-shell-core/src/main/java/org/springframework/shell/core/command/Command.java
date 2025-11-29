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

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.shell.core.commands.AbstractCommand;

/**
 * @author Eric Bottard
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
@FunctionalInterface
public interface Command {

	/**
	 * Get the name of the command.
	 * @return the name of the command
	 */
	default String getName() {
		return this.getClass().getSimpleName().toLowerCase();
	}

	/**
	 * Get a short description of the command.
	 * @return the description of the command
	 */
	default String getDescription() {
		return "";
	}

	/**
	 * Get the help text of the command.
	 * @return the help text of the command
	 */
	default String getHelp() {
		return getName() + "(" + String.join(",", getAliases()) + "): " + getDescription();
	}

	/**
	 * Get the group of the command.
	 * @return the group of the command
	 */
	default String getGroup() {
		return "";
	}

	/**
	 * Get the aliases of the command.
	 * @return the aliases of the command
	 */
	default List<String> getAliases() {
		return Collections.emptyList();
	}

	/**
	 * Execute the command within the given context.
	 * @param commandContext the context of the command
	 * @return the exit status of the command
	 */
	ExitStatus execute(CommandContext commandContext) throws Exception;

	/**
	 * Creates and returns a new instance of a {@code Builder} for defining and
	 * constructing commands.
	 * <p>
	 * The builder allows customization of command properties such as name, description,
	 * group, help text, aliases, and execution logic.
	 * @return a new {@code Builder} instance for configuring and creating commands
	 */
	static Builder builder() {
		return new DefaultCommandBuilder();
	}

	/**
	 * Builder for creating command.
	 */
	interface Builder {

		/**
		 * Set the name of the command.
		 * @return this builder
		 */
		Builder name(String name);

		/**
		 * Set the description of the command.
		 * @return this builder
		 */
		Builder description(String description);

		/**
		 * Set the help of the command.
		 * @return this builder
		 */
		Builder help(String help);

		/**
		 * Set the group of the command.
		 * @return this builder
		 */
		Builder group(String group);

		/**
		 * Set the aliases of the command.
		 * @return this builder
		 */
		Builder aliases(String... aliases);

		/**
		 * Set the aliases of the command.
		 * @return this builder
		 */
		Builder aliases(List<String> aliases);

		/**
		 * Set command execution logic.
		 * @return this builder
		 */
		Builder execute(Consumer<CommandContext> commandExecutor);

		/**
		 * Build the {@link AbstractCommand}.
		 */
		AbstractCommand build();

	}

}
