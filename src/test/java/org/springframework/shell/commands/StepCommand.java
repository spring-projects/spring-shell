/**
 * 
 */
package org.springframework.shell.commands;

import org.springframework.shell.core.AbstractStepExecutionProcessor;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliStepIndicator;
import org.springframework.shell.event.ParseResult;
import org.springframework.stereotype.Component;

/**
 * Multi-step commands to be tested
 *
 * @author robin
 */
@Component
public class StepCommand extends AbstractStepExecutionProcessor {
	
	private int steps = 0;
	
	@CliCommand(value={ "step-test"})
	@CliStepIndicator
	public Integer stepTest() {
		return steps;
	};
	
	@CliCommand(value={ "step-check"})
	public Integer stepCheck() {
		return steps;
	}

	/* (non-Javadoc)
	 * @see org.springframework.shell.core.AbstractStepExecutionProcessor#handleStepExecutionResult(org.springframework.shell.event.ParseResult, java.lang.Object)
	 */
	@Override
	protected void handleStepExecutionResult(ParseResult invocationContext, Object stepResult) {
		shell.getLogger().info("Step result: " + stepResult.toString());
	}

	/* (non-Javadoc)
	 * @see org.springframework.shell.core.AbstractStepExecutionProcessor#hasMoreSteps(java.lang.Object)
	 */
	@Override
	protected boolean hasMoreSteps(Object stepResult) {
		return steps < 3;
	}

	/* (non-Javadoc)
	 * @see org.springframework.shell.core.AbstractStepExecutionProcessor#configureStep(java.lang.Object)
	 */
	@Override
	protected Object configureStep(Object stepResult) {
		steps++;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.shell.core.AbstractStepExecutionProcessor#executeStep(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Object executeStep(Object stepResult, Object stepConfig) {
		shell.getLogger().info("Executing step " + steps);
		return steps;
	}

}
