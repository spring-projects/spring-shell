/**
 * 
 */
package org.springframework.shell.commands;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.annotation.CliStepIndicator;

/**
 * Tests for commands annotated with @{@link CliStepIndicator}
 *
 * @author robin
 */
public class StepCommandsTest extends AbstractShellIntegrationTest {

	@Test
	public void command_withTwoSteps_StepCheckReportsCorrectCount() throws Exception {
		CommandResult cr = getShell().executeCommand("step-test");   
		String result = cr.getResult().toString();
		MatcherAssert.assertThat(result, Matchers.equalTo("0"));
		
		cr = getShell().executeCommand("step-check");   
		result = cr.getResult().toString();
		MatcherAssert.assertThat(result, Matchers.equalTo("3"));
	}
	
}
