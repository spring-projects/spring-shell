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

}
