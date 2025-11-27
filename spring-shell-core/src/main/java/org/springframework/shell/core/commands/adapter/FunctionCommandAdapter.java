package org.springframework.shell.core.commands.adapter;

import java.util.function.Function;

import org.jline.terminal.Terminal;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.commands.AbstractCommand;

public class FunctionCommandAdapter extends AbstractCommand {

	Function<CommandContext, String> commandFunction;

	public FunctionCommandAdapter(String name, String description, String group, String help,
			Function<CommandContext, String> commandFunction) {
		super(name, description, group, help);
		this.commandFunction = commandFunction;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		String commandOutput = this.commandFunction.apply(commandContext);
		Terminal terminal = commandContext.terminal();
		terminal.writer().println(commandOutput);
		terminal.flush();
		return ExitStatus.OK;
	}

}
