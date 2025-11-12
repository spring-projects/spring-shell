package org.springframework.shell.core.commands.adapter;

import java.util.function.Consumer;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.commands.AbstractCommand;

public class ConsumerCommandAdapter extends AbstractCommand {

	Consumer<CommandContext> commandExecutor;

	public ConsumerCommandAdapter(String name, String description, String help, String group,
			Consumer<CommandContext> commandExecutor) {
		super(name, description, help, group);
		this.commandExecutor = commandExecutor;
	}

	@Override
	public void execute(CommandContext commandContext) throws Exception {
		this.commandExecutor.accept(commandContext);
	}

}
