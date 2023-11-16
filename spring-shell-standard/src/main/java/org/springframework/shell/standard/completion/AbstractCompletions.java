/*
 * Copyright 2022-2023 the original author or authors.
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
package org.springframework.shell.standard.completion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.Utils;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Base class for completion script commands providing functionality for
 * resource handling and templating with {@code antrl stringtemplate}.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractCompletions {

	private final ResourceLoader resourceLoader;
	private final CommandCatalog commandCatalog;

	public AbstractCompletions(ResourceLoader resourceLoader, CommandCatalog commandCatalog) {
		this.resourceLoader = resourceLoader;
		this.commandCatalog = commandCatalog;
	}

	protected Builder builder() {
		return new DefaultBuilder();
	}

	/**
	 * Generates a model for a recursive command model starting from root
	 * level going down with all sub commands with options. Essentially providing
	 * all needed to build completions structure.
	 */
	protected CommandModel generateCommandModel() {
		Collection<CommandRegistration> commandsByName = Utils.removeHiddenCommands(commandCatalog.getRegistrations())
				.values();
		HashMap<String, DefaultCommandModelCommand> commands = new HashMap<>();
		HashSet<CommandModelCommand> topCommands = new HashSet<>();
		commandsByName.stream()
			.forEach(registration -> {
				String key = registration.getCommand();
				String[] splitKeys = key.split(" ");
				String commandKey = "";
				for (int i = 0; i < splitKeys.length; i++) {
					DefaultCommandModelCommand parent = null;
					String main = splitKeys[i];
					if (i > 0) {
						parent = commands.get(commandKey);
						commandKey = commandKey + " " + splitKeys[i];
					}
					else {
						commandKey = splitKeys[i];
					}
					String desc = i + 1 < splitKeys.length ? null : registration.getDescription();
					DefaultCommandModelCommand command = commands.computeIfAbsent(commandKey,
							(fullCommand) -> new DefaultCommandModelCommand(fullCommand, main, desc));

					// TODO long vs short
					List<CommandModelOption> options = registration.getOptions().stream()
						.flatMap(co -> Arrays.stream(co.getLongNames()))
						.map(lo -> CommandModelOption.of("--", lo))
						.collect(Collectors.toList());

					if (i == splitKeys.length - 1) {
						command.addOptions(options);
					}
					if (parent != null) {
						parent.addCommand(command);
					}
					if (i == 0) {
						topCommands.add(command);
					}
				}
			});
		return new DefaultCommandModel(new ArrayList<>(topCommands));
	}

	/**
	 * Interface for a command model structure. Is also used as entry model
	 * for ST4 templates which is a reason it has utility methods for easier usage
	 * of a templates.
	 */
	interface CommandModel {

		/**
		 * Gets root level commands where sub-commands can be found.
		 *
		 * @return root level commands
		 */
		List<CommandModelCommand> getCommands();

		/**
		 * Gets all commands as a flattened structure.
		 *
		 * @return all commands
		 */
		List<CommandModelCommand> getAllCommands();

		/**
		 * Gets root commands.
		 *
		 * @return root commands
		 */
		List<String> getRootCommands();
	}

	/**
	 * Interface for a command in a model. Also contains methods which makes it
	 * easier to work with ST4 templates.
	 */
	interface CommandModelCommand  {

		/**
		 * Gets a description of a command.
		 * @return command description
		 */
		String getDescription();

		/**
		 * Gets sub-commands known to this command.
		 * @return known sub-commands
		 */
		List<CommandModelCommand> getCommands();

		/**
		 * Gets options known to this command
		 *
		 * @return known options
		 */
		List<CommandModelOption> getOptions();

		/**
		 * Gets command flags.
		 *
		 * @return command flags
		 */
		List<String> getFlags();

		/**
		 * Gets sub commands.
		 *
		 * @return sub commands
		 */
		List<String> getSubCommands();

		/**
		 * Gets command parts. Essentially full command split into parts.
		 *
		 * @return command parts
		 */
		List<String> getCommandParts();

		/**
		 * Gets a main command
		 *
		 * @return the main command
		 */
		String getMainCommand();

		/**
		 * Gets a last command part.
		 *
		 * @return the last command part
		 */
		String getLastCommandPart();
	}

	interface CommandModelOption {
		String option();

		static CommandModelOption of(String prefix, String name) {
			return new DefaultCommandModelOption(String.format("%s%s", prefix, name));
		}
	}

	class DefaultCommandModel implements CommandModel {

		private final List<CommandModelCommand> commands;

		public DefaultCommandModel(List<CommandModelCommand> commands) {
			this.commands = commands;
		}

		@Override
		public List<CommandModelCommand> getCommands() {
			return commands;
		}

		@Override
		public List<CommandModelCommand> getAllCommands() {
			return getCommands().stream()
					.flatMap(c -> flatten(c))
					.collect(Collectors.toList());
		}

		@Override
		public List<String> getRootCommands() {
			return getCommands().stream()
					.map(c -> c.getLastCommandPart())
					.collect(Collectors.toList());
		}

		private Stream<CommandModelCommand> flatten(CommandModelCommand command) {
			return Stream.concat(Stream.of(command), command.getCommands().stream().flatMap(c -> flatten(c)));
		}
	}

	class DefaultCommandModelCommand implements CommandModelCommand {

		private String fullCommand;
		private String mainCommand;
		private String description;
		private List<CommandModelCommand> commands = new ArrayList<>();
		private List<CommandModelOption> options = new ArrayList<>();

		DefaultCommandModelCommand(String fullCommand, String mainCommand, String description) {
			this.fullCommand = fullCommand;
			this.mainCommand = mainCommand;
			this.description = description;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public List<String> getCommandParts() {
			return Arrays.asList(fullCommand.split(" "));
		}

		@Override
		public String getLastCommandPart() {
			String[] split = fullCommand.split(" ");
			return split[split.length - 1];
		}

		@Override
		public String getMainCommand() {
			return mainCommand;
		}

		@Override
		public List<String> getSubCommands() {
			return this.commands.stream()
					.map(c -> c.getMainCommand())
					.collect(Collectors.toList());
		}

		@Override
		public List<String> getFlags() {
			return this.options.stream()
					.map(o -> o.option())
					.collect(Collectors.toList());
		}

		@Override
		public List<CommandModelCommand> getCommands() {
			return commands;
		}

		@Override
		public List<CommandModelOption> getOptions() {
			return options;
		}

		void addOptions(List<CommandModelOption> options) {
			this.options.addAll(options);
		}

		void addCommand(DefaultCommandModelCommand command) {
			if (commands.contains(command)) {
				return;
			}
			commands.add(command);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + ((fullCommand == null) ? 0 : fullCommand.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			DefaultCommandModelCommand other = (DefaultCommandModelCommand) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance())) {
				return false;
			}
			if (fullCommand == null) {
				if (other.fullCommand != null) {
					return false;
				}
			} else if (!fullCommand.equals(other.fullCommand)) {
				return false;
			}
			return true;
		}

		private AbstractCompletions getEnclosingInstance() {
			return AbstractCompletions.this;
		}
	}

	static class DefaultCommandModelOption implements CommandModelOption {

		private String option;

		public DefaultCommandModelOption(String option) {
			this.option = option;
		}

		@Override
		public String option() {
			return option;
		}
	}

	private static String resourceAsString(Resource resource) {
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return FileCopyUtils.copyToString(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	interface Builder {

		Builder attribute(String name, Object value);
		Builder group(String resource);
		Builder appendGroup(String instance);
		String build();
	}

	class DefaultBuilder implements Builder {

		private final MultiValueMap<String, Object> defaultAttributes = new LinkedMultiValueMap<>();
		private final List<Supplier<String>> operations = new ArrayList<>();
		private String groupResource;

		@Override
		public Builder attribute(String name, Object value) {
			this.defaultAttributes.add(name, value);
			return this;
		}

		@Override
		public Builder group(String resource) {
			groupResource = resource;
			return this;
		}

		@Override
		public Builder appendGroup(String instance) {
			// delay so that we render with build
			Supplier<String> operation = () -> {
				String template = resourceAsString(resourceLoader.getResource(groupResource));
				STGroup group = new STGroupString(template);
				ST st = group.getInstanceOf(instance);
				defaultAttributes.entrySet().stream().forEach(entry -> {
					String key = entry.getKey();
					List<Object> values = entry.getValue();
					values.stream().forEach(v -> {
						st.add(key, v);
					});
				});
				return st.render();
			};
			operations.add(operation);
			return this;
		}

		@Override
		public String build() {
			StringBuilder buf = new StringBuilder();
			operations.stream().forEach(operation -> {
				buf.append(operation.get());
			});
			return buf.toString();
		}
	}
}
