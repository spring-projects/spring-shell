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

package org.springframework.shell.core.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Record representing the result of parsing user input into commands.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public record ParsedInput(String commandName, List<String> subCommands, List<CommandOption> options,
		List<CommandArgument> arguments) {

	public static Builder builder() {
		return Builder.builder();
	}

	public static class Builder {

		private String commandName = "";

		private final List<String> subCommands = new ArrayList<>();

		private final List<CommandOption> options = new ArrayList<>();

		private final List<CommandArgument> arguments = new ArrayList<>();

		static Builder builder() {
			return new Builder();
		}

		public Builder commandName(String commandName) {
			this.commandName = commandName;
			return this;
		}

		public Builder addSubCommand(String subCommand) {
			this.subCommands.add(subCommand);
			return this;
		}

		public Builder addOption(CommandOption option) {
			this.options.add(option);
			return this;
		}

		public Builder addArgument(CommandArgument argument) {
			this.arguments.add(argument);
			return this;
		}

		public ParsedInput build() {
			List<String> subCommands = List.copyOf(this.subCommands);
			List<CommandOption> options = List.copyOf(this.options);
			List<CommandArgument> arguments = List.copyOf(this.arguments);
			return new ParsedInput(commandName, subCommands, options, arguments);
		}

	}

}
