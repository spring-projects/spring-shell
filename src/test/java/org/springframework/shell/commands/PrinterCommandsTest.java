/**
 * 
 */
package org.springframework.shell.commands;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

/**
 * Test demonstrating executing the same command but with different output formats (default and csv)
 *
 * @author robin
 */
public class PrinterCommandsTest extends AbstractShellIntegrationTest {
	
	@Test
	public void customOutput_WithDefaultOutput_PrintsRegularOutput() throws Exception {
		CommandResult cr = getShell().executeCommand("printer-test");
		String result = cr.getResult().toString();
		MatcherAssert.assertThat(result, Matchers.equalTo("TestPrinterResource [id=12345, name=TestName]"));
	}
	
	@Test
	public void customOutput_WithCsvChosen_PrintsCsvOutput() throws Exception {
		CommandResult cr = getShell().executeCommand("printer-test --p csv");
		String result = cr.getResult().toString();
		MatcherAssert.assertThat(result, Matchers.equalTo("12345,TestName"));
	}

}
