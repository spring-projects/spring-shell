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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.shell.command.CommandOption;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.util.ClassUtils;

/**
 * Model encapsulating info about {@code command}.
 *
 * @author Janne Valkealahti
 */
class CommandInfoModel {

	private String name;
	private String description;
	private List<CommandParameterInfoModel> parameters;

	CommandInfoModel(String name, String description, List<CommandParameterInfoModel> parameters) {
		this.name = name;
		this.description = description;
		this.parameters = parameters;
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
				String type = o.getType() == null ? "String" : ClassUtils.getShortName(o.getType().getRawClass());
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

		String description = registration.getDescription();
		return new CommandInfoModel(name, description, parameters);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<CommandParameterInfoModel> getParameters() {
		return parameters;
	}
}
