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

import org.springframework.util.StringUtils;

/**
 * Model encapsulating info about {@code command parameter}.
 *
 * @author Janne Valkealahti
 */
class CommandParameterInfoModel {

	private String type;
	private List<String> arguments;
	private boolean required;
	private String description;
	private String defaultValue;

	CommandParameterInfoModel(String type, List<String> arguments, boolean required, String description,
			String defaultValue) {
		this.type = type;
		this.arguments = arguments;
		this.required = required;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	/**
	 * Builds {@link CommandParameterInfoModel}.
	 *
	 * @param type the type
	 * @param arguments the arguments
	 * @param required the required flag
	 * @param description the description
	 * @param defaultValue the default value
	 * @return a command parameter info model
	 */
	static CommandParameterInfoModel of(String type, List<String> arguments, boolean required,
			String description, String defaultValue) {
		return new CommandParameterInfoModel(type, arguments, required, description, defaultValue);
	}

	public String getType() {
		return type;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public boolean getRequired() {
		return required;
	}

	public String getDescription() {
		return description;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public boolean getHasDefaultValue() {
		return StringUtils.hasText(this.defaultValue);
	}
}
