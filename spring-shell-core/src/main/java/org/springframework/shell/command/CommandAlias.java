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
package org.springframework.shell.command;

/**
 * Interface representing an alias in a command.
 *
 * @author Janne Valkealahti
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
	String getGroup();

	/**
	 * Gets an instance of a default {@link CommandAlias}.
	 *
	 * @param command the command
	 * @param group the group
	 * @return default command alias
	 */
	public static CommandAlias of(String command, String group) {
		return new DefaultCommandAlias(command, group);
	}

	/**
	 * Default implementation of {@link CommandAlias}.
	 */
	public static class DefaultCommandAlias implements CommandAlias {

		private final String command;
		private final String group;

		public DefaultCommandAlias(String command, String group) {
			this.command = command;
			this.group = group;
		}

		@Override
		public String getCommand() {
			return command;
		}

		@Override
		public String getGroup() {
			return group;
		}
	}
}
