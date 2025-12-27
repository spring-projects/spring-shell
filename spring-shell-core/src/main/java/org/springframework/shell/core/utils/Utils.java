/*
 * Copyright 2015-present the original author or authors.
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

package org.springframework.shell.core.utils;

import java.util.List;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;

/**
 * Some text utilities.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine
 */
public class Utils {

	/**
	 * Get a formatted string of available non-hidden commands from the command registry.
	 * @param commandRegistry the command registry
	 * @return a string of available commands with their descriptions
	 * @since 4.0.0
	 */
	public static String formatAvailableCommands(CommandRegistry commandRegistry) {
		StringBuilder stringBuilder = new StringBuilder("Available commands: ");
		stringBuilder.append(System.lineSeparator());
		List<String> groups = commandRegistry.getCommands()
			.stream()
			.filter(command -> !command.isHidden())
			.map(Command::getGroup)
			.distinct()
			.sorted()
			.toList();
		for (String group : groups) {
			stringBuilder.append(group).append(System.lineSeparator());
			for (Command command : commandRegistry.getCommands()
				.stream()
				.filter(c -> !c.isHidden())
				.filter(c -> c.getGroup().equals(group))
				.toList()) {
				stringBuilder.append("\t")
					.append(command.getName())
					.append(": ")
					.append(command.getDescription())
					.append(System.lineSeparator());
			}
		}
		return stringBuilder.toString();
	}

	private final static ValidatorFactory DEFAULT_VALIDATOR_FACTORY;

	private final static Validator DEFAULT_VALIDATOR;

	static {
		DEFAULT_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
		DEFAULT_VALIDATOR = DEFAULT_VALIDATOR_FACTORY.getValidator();
	}

	/**
	 * Gets a default shared validation factory.
	 * @return default validation factory
	 */
	public static ValidatorFactory defaultValidatorFactory() {
		return DEFAULT_VALIDATOR_FACTORY;
	}

	/**
	 * Gets a default shared validator.
	 * @return default validator
	 */
	public static Validator defaultValidator() {
		return DEFAULT_VALIDATOR;
	}

}
