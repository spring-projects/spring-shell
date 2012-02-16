package org.springframework.shell;

import org.springframework.roo.shell.ExecutionStrategy;
import org.springframework.roo.shell.ParseResult;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.ReflectionUtils;

public class SimpleExecutionStrategy implements ExecutionStrategy {

	private final Class<?> mutex = SimpleExecutionStrategy.class;
	
	public Object execute(ParseResult parseResult) throws RuntimeException {
		Assert.notNull(parseResult, "Parse result required");
		synchronized (mutex) {
			Assert.isTrue(isReadyForCommands(), "ProcessManagerHostedExecutionStrategy not yet ready for commands");
			return ReflectionUtils.invokeMethod(parseResult.getMethod(), parseResult.getInstance(), parseResult.getArguments());
			/*
			Assert.isTrue(isReadyForCommands(), "ProcessManagerHostedExecutionStrategy not yet ready for commands");
			return processManager.execute(new CommandCallback<Object>() {
				public Object callback() {
					return ReflectionUtils.invokeMethod(parseResult.getMethod(), parseResult.getInstance(), parseResult.getArguments());
				}
			});*/
		}
	}

	public boolean isReadyForCommands() {
		// TODO Auto-generated method stub
		return true;
	}

	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}
