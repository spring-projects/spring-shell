package org.springframework.shell.core.commands.adapter;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.commands.AbstractCommand;
import org.springframework.util.MethodInvoker;

public class MethodInvokerCommandAdapter extends AbstractCommand {

	MethodInvoker methodInvoker;

	public MethodInvokerCommandAdapter(String name, String description, String help, String group,
			MethodInvoker methodInvoker) {
		super(name, description, help, group);
		this.methodInvoker = methodInvoker;
	}

	@Override
	public void execute(CommandContext commandContext) throws Exception {
		this.methodInvoker.setArguments(commandContext);
		this.methodInvoker.prepare();
		this.methodInvoker.invoke();
		commandContext.terminal().flush();
	}

}
