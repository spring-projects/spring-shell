package org.springframework.shell.core.commands.adapter;

import java.util.function.Consumer;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.commands.AbstractCommand;

public class ConsumerCommandAdapter extends AbstractCommand {

	Consumer<CommandContext> commandExecutor;

	public ConsumerCommandAdapter(String name, String description, String group, String help,
			Consumer<CommandContext> commandExecutor) {
		super(name, description, group, help);
		this.commandExecutor = commandExecutor;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		this.commandExecutor.accept(commandContext);
		return ExitStatus.OK;
	}

}
