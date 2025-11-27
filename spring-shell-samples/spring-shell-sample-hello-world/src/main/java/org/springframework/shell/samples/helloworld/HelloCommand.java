package org.springframework.shell.samples.helloworld;

import java.util.List;

import org.jline.terminal.Terminal;

import org.springframework.shell.core.command.CommandArgument;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.commands.AbstractCommand;

import static org.springframework.shell.core.utils.CommandUtils.getArgumentByIndex;
import static org.springframework.shell.core.utils.CommandUtils.getOptionByName;

public class HelloCommand extends AbstractCommand {

	public HelloCommand() {
		super("hello", "Say hello to a given name", "greetings",
				"A command that greets the user with a specified name and suffix. Example usage: hello -s=! John");
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		Terminal terminal = commandContext.terminal();
		// check for name argument
		List<CommandArgument> arguments = commandContext.arguments();
		if (arguments.isEmpty()) {
			terminal.writer().println("Error: Name argument is required.");
			terminal.writer().println("Usage: " + getHelp());
			terminal.flush();
			return ExitStatus.USAGE_ERROR;
		}

		// get name argument
		CommandArgument nameArgument = getArgumentByIndex(arguments, 0);
		String name = nameArgument.value();

		// get suffix option
		CommandOption suffixOption = getOptionByName(commandContext.options(), "s");
		String suffix = suffixOption == null ? "!" : suffixOption.value();

		// print greeting
		terminal.writer().println("Hello " + name + suffix);
		terminal.flush();
		return ExitStatus.OK;
	}

}
