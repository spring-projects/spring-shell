/*
 * Copyright 2022-present the original author or authors.
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.Command;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Model encapsulating info about {@code command}.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
class CommandInfoModel {

	private String name;

	private List<String> aliases;

	private @Nullable String description;

	private List<CommandParameterInfoModel> parameters;

	private CommandAvailabilityInfoModel availability;

	CommandInfoModel(String name, List<String> aliases, @Nullable String description,
			List<CommandParameterInfoModel> parameters, CommandAvailabilityInfoModel availability) {
		this.name = name;
		this.aliases = aliases;
		this.description = description;
		this.parameters = parameters;
		this.availability = availability;
	}

	/**
	 * Builds {@link CommandInfoModel} from {@link Command}.
	 * @param name the command name
	 * @param command the command
	 * @return the command info model
	 */
	static CommandInfoModel of(String name, Command command) {
		List<CommandOption> options = command.getOptions();
		List<CommandParameterInfoModel> parameters = options.stream().map(o -> {
			String type = commandOptionType(o);
			List<String> arguments = Stream
				.concat(Stream.of(o.longName()).map(a -> "--" + a), Stream.of(o.shortName()).map(s -> "-" + s))
				.collect(Collectors.toList());
			boolean required = o.required();
			String description = o.description();
			String defaultValue = o.defaultValue();
			return CommandParameterInfoModel.of(type, arguments, required, description, defaultValue);
		}).collect(Collectors.toList());

		List<String> aliases = command.getAliases().stream().map(ca -> ca.getCommand()).collect(Collectors.toList());

		String description = command.getDescription();
		boolean available = true;
		String availReason = "";
		// if (command.getAvailability() != null) {
		// Availability a = registration.getAvailability();
		// available = a.isAvailable();
		// availReason = a.getReason();
		// }
		CommandAvailabilityInfoModel availModel = CommandAvailabilityInfoModel.of(available, availReason);
		return new CommandInfoModel(name, aliases, description, parameters, availModel);
	}

	private static String commandOptionType(CommandOption o) {
		if (o.type() != null) {
			Class<?> rawClass = o.type().getRawClass();
			Assert.notNull(rawClass, "'rawClass' must not be null");
			if (ClassUtils.isAssignable(rawClass, Void.class)) {
				return "";
			}
			else {
				return ClassUtils.getShortName(rawClass);
			}
		}
		else {
			return "String";
		}
	}

	public String getName() {
		return name;
	}

	public List<String> getNames() {
		return Arrays.asList(name.split(" "));
	}

	public List<String> getAliases() {
		return this.aliases;
	}

	public @Nullable String getDescription() {
		return description;
	}

	public List<CommandParameterInfoModel> getParameters() {
		return parameters;
	}

	public CommandAvailabilityInfoModel getAvailability() {
		return availability;
	}

}
