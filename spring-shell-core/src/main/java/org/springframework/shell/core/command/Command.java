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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.command.adapter.ConsumerCommandAdapter;
import org.springframework.shell.core.command.adapter.FunctionCommandAdapter;
import org.springframework.shell.core.command.availability.AvailabilityProvider;
import org.springframework.shell.core.command.exit.ExitStatusExceptionMapper;
import org.springframework.util.Assert;

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
	 * Check if the command is hidden.
	 * @return true if the command is hidden, false otherwise
	 */
	default boolean isHidden() {
		return false;
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
	 * Get the availability provider of the command. Defaults to always available.
	 * @return the availability provider of the command
	 */
	default AvailabilityProvider getAvailabilityProvider() {
		return AvailabilityProvider.alwaysAvailable();
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
		return new Builder();
	}

	/**
	 * Builder for creating command.
	 */
	final class Builder {

		private String name = "";

		private String description = "";

		private String group = "";

		private String help = "";

		private boolean hidden = false;

		private AvailabilityProvider availabilityProvider = AvailabilityProvider.alwaysAvailable();

		@Nullable ExitStatusExceptionMapper exitStatusExceptionMapper;

		private List<String> aliases = new ArrayList<>();

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder help(String help) {
			this.help = help;
			return this;
		}

		public Builder group(String group) {
			this.group = group;
			return this;
		}

		public Builder hidden(boolean hidden) {
			this.hidden = hidden;
			return this;
		}

		public Builder availabilityProvider(AvailabilityProvider availabilityProvider) {
			this.availabilityProvider = availabilityProvider;
			return this;
		}

		public Builder exitStatusExceptionMapper(ExitStatusExceptionMapper exitStatusExceptionMapper) {
			this.exitStatusExceptionMapper = exitStatusExceptionMapper;
			return this;
		}

		public Builder aliases(String... aliases) {
			this.aliases = Arrays.asList(aliases);
			return this;
		}

		public AbstractCommand execute(Consumer<CommandContext> commandExecutor) {
			Assert.hasText(name, "'name' must be specified");

			ConsumerCommandAdapter command = new ConsumerCommandAdapter(name, description, group, help, hidden,
					commandExecutor);
			command.setAliases(aliases);
			command.setAvailabilityProvider(availabilityProvider);
			if (exitStatusExceptionMapper != null) {
				command.setExitStatusExceptionMapper(exitStatusExceptionMapper);
			}

			return command;
		}

		public AbstractCommand execute(Function<CommandContext, String> commandExecutor) {
			Assert.hasText(name, "'name' must be specified");

			FunctionCommandAdapter command = new FunctionCommandAdapter(name, description, group, help, hidden,
					commandExecutor);
			command.setAliases(aliases);
			command.setAvailabilityProvider(availabilityProvider);
			if (exitStatusExceptionMapper != null) {
				command.setExitStatusExceptionMapper(exitStatusExceptionMapper);
			}

			return command;
		}

	}

}
