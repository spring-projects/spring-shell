package org.springframework.shell.core.utils;

import java.util.List;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;

/**
 * Utility class for command-related operations.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class CommandUtils {

	/**
	 * Get a formatted string of available non-hidden commands from the command registry.
	 * @param commandRegistry the command registry
	 * @return a string of available commands with their descriptions
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
			for (Command command : commandRegistry.getCommands()) {
				if (command.getGroup().equals(group)) {
					stringBuilder.append("\t")
						.append(command.getName())
						.append(": ")
						.append(command.getDescription())
						.append(System.lineSeparator());
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Get a list of available commands from the command registry.
	 * @param commandRegistry the command registry
	 * @return a list of available commands
	 */
	public static List<String> getAvailableCommands(CommandRegistry commandRegistry) {
		return commandRegistry.getCommands().stream().map(Command::getName).toList();
	}

	/**
	 * Get a list of available sub-commands for a given command name from the command
	 * registry.
	 * @param commandName the name of the command
	 * @param commandRegistry the command registry
	 * @return a list of available sub-commands
	 */
	public static List<String> getAvailableSubCommands(String commandName, CommandRegistry commandRegistry) {
		return commandRegistry.getCommands()
			.stream()
			.filter(command -> command.getName().startsWith(commandName + " "))
			.map(command -> command.getName().substring(commandName.length()).trim())
			.toList();
	}

}
