/**
 * 
 */
package org.springframework.shell.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.core.annotation.CliStepIndicator;
import org.springframework.shell.event.ParseResult;


/**
 * @author robin
 *
 */
public abstract class AbstractStepExecutionProcessor implements ExecutionProcessor {
	
	@Autowired
	@Qualifier("shell")
	protected JLineShellComponent shell;
	
	private Object stepResult;
	private Object stepConfig;
	
	@Override
	public ParseResult beforeInvocation(ParseResult invocationContext) {
		return invocationContext;
	}
	
	@Override
	public void afterThrowingInvocation(ParseResult invocationContext, Throwable thrown) { }
	
	@Override
	public void afterReturningInvocation(ParseResult invocationContext, Object result) {
		// if the command being invoked supports steps...
		if (isStepCommand(invocationContext)) {
			stepResult = result;
			
			// display the initial step result
			outputStepResult();
			
			// while there are more steps
			while (hasMoreSteps()) {
				// configure the next step
				stepConfig = configureStep();
				
				// process the next step in the workflow
				stepResult = processStep(stepConfig);
				
				// display the step result
				outputStepResult();
			}
		}
	}
	
	/**
	 * If the {@link CliStepIndicator} annotation is present, process the Step workflow
	 * 
	 * @param invocationContext
	 * @return
	 */
	protected boolean isStepCommand(ParseResult invocationContext) {
		return invocationContext.getMethod().isAnnotationPresent(CliStepIndicator.class);		
	}

	/**
	 * Print the output of the current step execution
	 */
	protected abstract void outputStepResult();
	
	/**
	 * Are there more steps to process
	 * @return
	 */
	protected abstract boolean hasMoreSteps();
	
	/**
	 * Use this method to control if/when and how the step will be processed
	 * @return
	 */
	protected abstract Object configureStep();
	
	/**
	 * Process the step using the stepConfig
	 * @param stepConfig
	 * @return
	 */
	protected abstract Object processStep(Object stepConfig); 

	/**
	 * @return the stepResult
	 */
	protected Object getStepResult() {
		return stepResult;
	}

	/**
	 * @return the stepConfig
	 */
	protected Object getStepConfig() {
		return stepConfig;
	}

}
