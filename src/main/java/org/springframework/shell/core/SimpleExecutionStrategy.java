package org.springframework.shell.core;

import org.springframework.shell.event.ParseResult;
import org.springframework.shell.support.util.Assert;
import org.springframework.shell.support.util.ReflectionUtils;

/**
 * Simple execution strategy for invoking a target method.
 * Supports pre/post processing to allow {@link CommandMarker}s for aop-like behavior (
 * typically used for controlling stateful objects). 
 * 
 * @author Mark Pollack
 * @author Costin Leau
 */
public class SimpleExecutionStrategy implements ExecutionStrategy {

	private final Class<?> mutex = SimpleExecutionStrategy.class;

	public Object execute(ParseResult parseResult) throws RuntimeException {
		Assert.notNull(parseResult, "Parse result required");
		synchronized (mutex) {
			Assert.isTrue(isReadyForCommands(), "ProcessManagerHostedExecutionStrategy not yet ready for commands");
			Object target = parseResult.getInstance();
			if (target instanceof ExecutionProcessor) {
				ExecutionProcessor processor = ((ExecutionProcessor) target);
				parseResult = processor.beforeInvocation(parseResult);
				try {
					Object result = invoke(parseResult);
					processor.afterReturningInvocation(parseResult, result);
					return result;
				} catch (Throwable th) {
					processor.afterThrowingInvocation(parseResult, th);
					if (th instanceof Error) {
						throw ((Error) th);
					}
					if (th instanceof RuntimeException) {
						throw ((RuntimeException) th);
					}
					throw new RuntimeException(th);
				}
			}
			else {
				return invoke(parseResult);
			}
		}
	}

	private Object invoke(ParseResult parseResult) {
		return ReflectionUtils.invokeMethod(parseResult.getMethod(), parseResult.getInstance(), parseResult.getArguments());
	}

	public boolean isReadyForCommands() {
		return true;
	}

	public void terminate() {
		// do nothing
	}

}
