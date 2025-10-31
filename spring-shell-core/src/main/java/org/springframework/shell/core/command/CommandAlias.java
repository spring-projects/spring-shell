/*
 * Copyright 2022 the original author or authors.
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

import org.jspecify.annotations.Nullable;

/**
 * Interface representing an alias in a command.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public interface CommandAlias {

	/**
	 * Gets a command an alias.
	 *
	 * @return command
	 */
	String getCommand();

	/**
	 * Get group for an alias.
	 *
	 * @return the group
	 */
	@Nullable String getGroup();

	/**
	 * Gets an instance of a default {@link CommandAlias}.
	 *
	 * @param command the command
	 * @param group the group
	 * @return default command alias
	 */
	public static CommandAlias of(String command, @Nullable String group) {
		return new DefaultCommandAlias(command, group);
	}

	/**
	 * Default implementation of {@link CommandAlias}.
	 */
	public static class DefaultCommandAlias implements CommandAlias {

		private final String command;
		private final @Nullable String group;

		public DefaultCommandAlias(String command, @Nullable String group) {
			this.command = command;
			this.group = group;
		}

		@Override
		public String getCommand() {
			return command;
		}

		@Override
		public @Nullable String getGroup() {
			return group;
		}
	}
}
