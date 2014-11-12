package org.springframework.shell;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.support.logging.HandlerUtils;

public class BootstrapTest {

	@Test
	public void test() throws IOException {
		try {
			Bootstrap bootstrap = new Bootstrap(null);
			JLineShellComponent shell = bootstrap.getJLineShellComponent();
			
			//This is a brittle assertion - as additiona 'test' commands are added to the suite, this number will increase.
			Assert.assertEquals("Number of CommandMarkers is incorrect", 10, shell.getSimpleParser().getCommandMarkers().size());
			Assert.assertEquals("Number of Converters is incorrect", 16, shell.getSimpleParser().getConverters().size());			
		} catch (RuntimeException t) {
			throw t;
		} finally {
			HandlerUtils.flushAllHandlers(Logger.getLogger(""));
		}
		
	}

    @Test
    public void testCommandLineOptions() throws IOException {
        try {
            String[] args = {"--histsize", "20", "--disableInternalCommands", "--username", "joe", "--password", "mama"};
            Bootstrap bootstrap = new Bootstrap(args);
            JLineShellComponent shell = bootstrap.getJLineShellComponent();
            ApplicationContext ctx = bootstrap.getApplicationContext();
            CommandLine commandLine = ctx.getBean(CommandLine.class);
            Assert.assertEquals(20, commandLine.getHistorySize());
            Assert.assertEquals(true, commandLine.getDisableInternalCommands());
            Assert.assertEquals(7, commandLine.getArgs().length);
            Assert.assertArrayEquals(args, commandLine.getArgs());
            Map<String, String> parsedArgs = commandLine.getParsedArgs();
            Assert.assertEquals(4, parsedArgs.size());
            Assert.assertEquals(true, parsedArgs.containsKey("username"));
            Assert.assertEquals("joe", parsedArgs.get("username"));
            Assert.assertEquals(true, parsedArgs.containsKey("password"));
            Assert.assertEquals("mama", parsedArgs.get("password"));
        } catch (RuntimeException t) {
            throw t;
        } finally {
            HandlerUtils.flushAllHandlers(Logger.getLogger(""));
        }
    }

}
