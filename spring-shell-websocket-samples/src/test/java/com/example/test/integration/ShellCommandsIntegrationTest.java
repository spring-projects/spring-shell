package com.example.test.integration;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.ShellCommands;
import com.example.test.BaseShellTest;
import com.example.test.TestShellConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = { InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=" + false })
@ContextConfiguration(classes = TestShellConfig.class)
public class ShellCommandsIntegrationTest extends BaseShellTest {

	private static final Class<ShellCommands> COMMAND_CLASS_UNDER_TEST = ShellCommands.class;

	@Autowired
	private Shell shell;

	@Test
	public void testGreet() {
		final String command = "greet";
		final String commandMethod = "greet";

		final MethodTarget commandTarget = lookupCommand(shell, command);
		assertThat(commandTarget, notNullValue());
		assertThat(commandTarget.getGroup(), is("System commands"));
		assertThat(commandTarget.getHelp(), is("Greet users"));
		assertThat(commandTarget.getMethod(),
				is(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, String.class)));
		assertThat(commandTarget.getAvailability().isAvailable(), is(true));
		assertThat(shell.evaluate(() -> command + " John"), is("Hello, John"));
	}

}
