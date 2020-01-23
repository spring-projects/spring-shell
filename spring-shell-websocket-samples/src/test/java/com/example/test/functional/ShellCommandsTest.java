package com.example.test.functional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.example.ShellCommands;
import com.example.test.BaseShellTest;
import com.example.test.TestShellConfig;

/**
 * Illustrative functional tests for the Spring Shell Calculator application. These
 * functional tests use Spring Shell commands auto-wired by the Spring Test Runner outside
 * of the shell, to test functionality of the commands.
 *
 * @author Sualeh Fatehi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestShellConfig.class, ShellCommands.class })
public class ShellCommandsTest extends BaseShellTest {

	private static final Class<ShellCommands> COMMAND_CLASS_UNDER_TEST = ShellCommands.class;

	private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();

	@Autowired
	private ApplicationContext context;

	@Before
	public void setup() {
		final StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
		registrar.setApplicationContext(context);
		registrar.register(registry);

		//state.clear();
	}

	@Test
	public void testGreet() {
		final String command = "greet";
		final String commandMethod = "greet";

		final MethodTarget commandTarget = lookupCommand(registry, command);
		assertThat(commandTarget, notNullValue());
		assertThat(commandTarget.getGroup(), is("System commands"));
		assertThat(commandTarget.getHelp(), is("Greet users"));
		assertThat(commandTarget.getMethod(),
				is(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, String.class)));
		assertThat(commandTarget.getAvailability().isAvailable(), is(true));
		assertThat(invoke(commandTarget, "John"), is("Hello, John"));
	}

}
