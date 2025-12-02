package org.springframework.shell.samples.helloworld.boot;

import java.io.PrintWriter;
import java.util.List;

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
		PrintWriter outputWriter = commandContext.outputWriter();
		// check for name argument
		List<CommandArgument> arguments = commandContext.parsedInput().arguments();
		if (arguments.isEmpty()) {
			outputWriter.println("Error: Name argument is required.");
			outputWriter.println("Usage: " + getHelp());
			outputWriter.flush();
			return ExitStatus.USAGE_ERROR;
		}

		// get name argument
		CommandArgument nameArgument = getArgumentByIndex(arguments, 0);
		String name = nameArgument.value();

		// get suffix option
		CommandOption suffixOption = getOptionByName(commandContext.parsedInput().options(), "s");
		String suffix = suffixOption == null ? "!" : suffixOption.value();

		// print greeting
		outputWriter.println("Hello " + name + suffix);
		outputWriter.flush();
		return ExitStatus.OK;
	}

}
