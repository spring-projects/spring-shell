/**
 * 
 */
package org.springframework.shell.core;

import static java.util.logging.Level.SEVERE;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.core.annotation.CliStepIndicator;
import org.springframework.shell.event.ParseResult;
import org.springframework.shell.support.logging.HandlerUtils;


/**
 * An abstract custom {@link ExecutionProcessor} whose implementations can examine if the 
 * command being invoked has been annotated with @{@link CliStepIndicator} and, if so, 
 * handles, configures, executes, and displays each of the steps configured for the command.
 *  
 * @author robin
 */
public abstract class AbstractStepExecutionProcessor implements ExecutionProcessor {
	
	private static final Logger logger = HandlerUtils.getLogger(AbstractStepExecutionProcessor.class);
	
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
	public void afterThrowingInvocation(ParseResult invocationContext, Throwable thrown) {
		logger.log(SEVERE, "Exception " + thrown.getMessage() + " thrown for " + invocationContext);
	}

	@Override
	public void afterReturningInvocation(ParseResult invocationContext, Object result) {
		// if the command being invoked supports steps...
		if (isStepCommand(invocationContext)) {			
			stepResult = result;
			
			// display the initial step result
			handleStepExecutionResult(invocationContext, stepResult);
			
			shell.setAlreadyHandledExecutionResult(true);
			
			// while there are more steps
			while (hasMoreSteps(stepResult)) {
				// configure the next step
				stepConfig = configureStep(stepResult);
				
				// execute the next step in the workflow
				stepResult = executeStep(stepResult, stepConfig);
				
				// display the step result
				handleStepExecutionResult(invocationContext, stepResult);
			}
		}
	}
	
	/**
	 * If the {@link CliStepIndicator} annotation is present, process the Step workflow
	 * 
	 * @param invocationContext
	 * @return
	 */
	protected final boolean isStepCommand(ParseResult invocationContext) {
		return invocationContext.getMethod().isAnnotationPresent(CliStepIndicator.class);		
	}

	/**
	 * Print the output of the current step execution
	 * @param invocationContext 
	 * @param stepResult 
	 */
	protected abstract void handleStepExecutionResult(ParseResult invocationContext, Object stepResult);
	
	/**
	 * Are there more steps to process
	 * @param stepResult 
	 * @return
	 */
	protected abstract boolean hasMoreSteps(Object stepResult);
	
	/**
	 * Use this method to control if/when and how the step will be processed
	 * @param stepResult 
	 * @return
	 */
	protected abstract Object configureStep(Object stepResult);
	
	/**
	 * Process the step using the stepConfig
	 * @param stepConfig
	 * @return
	 */
	protected abstract Object executeStep(Object stepResult, Object stepConfig); 

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
