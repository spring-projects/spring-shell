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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.util.Assert;

/**
 * Some text utilities.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine
 */
public class Utils {

	/**
	 * Turn CamelCaseText into gnu-style-lowercase.
	 */
	public static String unCamelify(CharSequence original) {
		StringBuilder result = new StringBuilder(original.length());
		boolean wasLowercase = false;
		for (int i = 0; i < original.length(); i++) {
			char ch = original.charAt(i);
			if (Character.isUpperCase(ch) && wasLowercase) {
				result.append('-');
			}
			wasLowercase = Character.isLowerCase(ch);
			result.append(Character.toLowerCase(ch));
		}
		return result.toString();
	}

	/**
	 * Get a formatted string of available non-hidden commands from the command registry.
	 * @param commandRegistry the command registry
	 * @return a string of available commands with their descriptions
	 * @since 4.0.0
	 */
	public static String formatAvailableCommands(CommandRegistry commandRegistry) {
		StringBuilder stringBuilder = new StringBuilder("AVAILABLE COMMANDS");
		stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
		Set<Command> commands = getCommands(commandRegistry);
		List<String> groups = commands.stream()
			.filter(command -> !command.isHidden())
			.map(Command::getGroup)
			.distinct()
			.sorted()
			.toList();
		for (String group : groups) {
			stringBuilder.append(group).append(System.lineSeparator());
			for (Command command : commands.stream()
				.filter(c -> !c.isHidden())
				.filter(c -> c.getGroup().equals(group))
				.toList()) {
				stringBuilder.append("\t")
					.append(command.getName())
					.append(command.getAliases().isEmpty() ? "" : ", " + String.join(", ", command.getAliases()))
					.append(": ")
					.append(command.getDescription())
					.append(System.lineSeparator());
			}
		}
		return stringBuilder.toString();
	}

	public static Object getDefaultValueForPrimitiveType(Class<?> type) {
		Assert.isTrue(type.isPrimitive(), "Type must be a primitive type");
		if (type == boolean.class) {
			return false;
		}
		else if (type == char.class) {
			return '\u0000';
		}
		else if (type == byte.class || type == short.class || type == int.class || type == long.class) {
			return 0;
		} // otherwise it's float or double
		return 0.0;
	}

	private static Set<Command> getCommands(CommandRegistry commandRegistry) {
		Set<Command> commands = new HashSet<>(commandRegistry.getCommands());
		commands.add(QUIT_COMMAND);
		return commands;
	}

	// Dummy exit command to show in available commands
	public static final Command QUIT_COMMAND = new AbstractCommand("quit", "Exit the shell", "Built-In Commands") {
		@Override
		public List<String> getAliases() {
			return List.of("exit");
		}

		@Override
		public ExitStatus doExecute(CommandContext commandContext) {
			return ExitStatus.OK;
		}
	};

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
