package org.springframework.shell.core.commands.adapter;

import java.util.function.Function;

import org.jline.terminal.Terminal;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.commands.AbstractCommand;

public class FunctionCommandAdapter extends AbstractCommand {

	Function<CommandContext, String> commandFunction;

	public FunctionCommandAdapter(String name, String description, String help, String group,
			Function<CommandContext, String> commandFunction) {
		super(name, description, help, group);
		this.commandFunction = commandFunction;
	}

	@Override
	public void execute(CommandContext commandContext) throws Exception {
		String commandOutput = this.commandFunction.apply(commandContext);
		Terminal terminal = commandContext.terminal();
		terminal.writer().println(commandOutput);
		terminal.flush();
	}

}
