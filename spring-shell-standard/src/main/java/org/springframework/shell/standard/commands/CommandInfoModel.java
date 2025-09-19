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
package org.springframework.shell.standard.commands;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.shell.Availability;
import org.springframework.shell.command.CommandOption;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Model encapsulating info about {@code command}.
 *
 * @author Janne Valkealahti
 */
class CommandInfoModel {

	private String name;
	private List<String> aliases;
	private String description;
	private List<CommandParameterInfoModel> parameters;
	private CommandAvailabilityInfoModel availability;

	CommandInfoModel(String name, List<String> aliases, String description, List<CommandParameterInfoModel> parameters,
			CommandAvailabilityInfoModel availability) {
		this.name = name;
		this.aliases = aliases;
		this.description = description;
		this.parameters = parameters;
		this.availability = availability;
	}

	/**
	 * Builds {@link CommandInfoModel} from {@link CommandRegistration}.
	 *
	 * @param name the command name
	 * @param registration the command registration
	 * @return the command info model
	 */
	static CommandInfoModel of(String name, CommandRegistration registration) {
		List<CommandOption> options = registration.getOptions();
		List<CommandParameterInfoModel> parameters = options.stream()
			.map(o -> {
				String type = commandOptionType(o);
				List<String> arguments = Stream.concat(
						Stream.of(o.getLongNames()).map(a -> "--" + a),
						Stream.of(o.getShortNames()).map(s -> "-" + s))
					.collect(Collectors.toList());
				boolean required = o.isRequired();
				String description = o.getDescription();
				String defaultValue = o.getDefaultValue();
				return CommandParameterInfoModel.of(type, arguments, required, description, defaultValue);
			})
			.collect(Collectors.toList());

		List<String> aliases = registration.getAliases().stream().map(ca -> ca.getCommand())
				.collect(Collectors.toList());

		String description = registration.getDescription();
		boolean available = true;
		String availReason = "";
		if (registration.getAvailability() != null) {
			Availability a = registration.getAvailability();
			available = a.isAvailable();
			availReason = a.getReason();
		}
		CommandAvailabilityInfoModel availModel = CommandAvailabilityInfoModel.of(available, availReason);
		return new CommandInfoModel(name, aliases, description, parameters, availModel);
	}

	private static String commandOptionType(CommandOption o) {
		if (StringUtils.hasText(o.getLabel())) {
			return o.getLabel();
		}
		else {
			if (o.getType() != null) {
				if (ClassUtils.isAssignable(o.getType().getRawClass(), Void.class)) {
					return "";
				}
				else {
					return ClassUtils.getShortName(o.getType().getRawClass());
				}
			}
			else {
				return "String";
			}
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

	public String getDescription() {
		return description;
	}

	public List<CommandParameterInfoModel> getParameters() {
		return parameters;
	}

	public CommandAvailabilityInfoModel getAvailability() {
		return availability;
	}
}
