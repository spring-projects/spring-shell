package org.springframework.shell;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.event.ShellStatus;
import org.springframework.shell.event.ShellStatus.Status;
import org.springframework.shell.support.logging.HandlerUtils;

public class BootstrapTest {

	@Test
	public void test() throws IOException, InterruptedException {
		try {
			Bootstrap bootstrap = new Bootstrap(null);
			JLineShellComponent shell = bootstrap.getJLineShellComponent();
			shell.start();
			while (shell.getShellStatus().getStatus() != ShellStatus.Status.USER_INPUT) {
				Thread.sleep(100);
			}
			Assert.assertEquals("Number of CommandMarkers is incorrect", 4, shell.getSimpleParser().getCommandMarkers().size());
			Assert.assertEquals("Number of Converters is incorrect", 16, shell.getSimpleParser().getConverters().size());
			shell.stop();
		} catch (RuntimeException t) {
			throw t;
		} finally {
			HandlerUtils.flushAllHandlers(Logger.getLogger(""));
		}
		
	}

}
