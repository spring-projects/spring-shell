package org.springframework.shell.core.utils;

import java.util.List;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandArgument;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.CommandRegistry;

/**
 * Utility class for command-related operations.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class CommandUtils {

	/**
	 * Retrieve a command option by its name (long or short).
	 * @param options the list of command options
	 * @param optionName the name of the option to retrieve
	 * @return the matching {@link CommandOption} or null if not found
	 */
	@Nullable public static CommandOption getOptionByName(List<CommandOption> options, String optionName) {
		return options.stream()
			.filter(option -> option.longName().equalsIgnoreCase(optionName)
					|| option.shortName() == optionName.charAt(0))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Retrieve a command argument by its index.
	 * @param arguments the list of command arguments
	 * @param index the index of the argument to retrieve
	 * @return the matching {@link CommandArgument}
	 */
	@Nullable public static CommandArgument getArgumentByIndex(List<CommandArgument> arguments, int index) {
		return arguments.stream().filter(argument -> argument.index() == index).findAny().orElse(null);
	}

	/**
	 * Get a comma-separated string of available command names from the command registry.
	 * @param commandRegistry the command registry
	 * @return a string of available command names
	 */
	public static String getAvailableCommands(CommandRegistry commandRegistry) {
		StringBuilder stringBuilder = new StringBuilder("Available commands: ");
		stringBuilder.append(System.lineSeparator());
		for (Command command : commandRegistry.getCommands()) {
			stringBuilder.append("\t")
				.append(command.getName())
				.append(": ")
				.append(command.getDescription())
				.append(System.lineSeparator());
		}
		return stringBuilder.toString();
	}

}
