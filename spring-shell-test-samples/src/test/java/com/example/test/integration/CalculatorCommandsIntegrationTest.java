package com.example.test.integration;

import com.example.CalculatorCommands;
import com.example.CalculatorState;
import com.example.test.BaseCalculatorTest;
import com.example.test.TestCalculatorStateConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 *
 * Illustrative integration tests for the Spring Shell Calculator application. These
 * integration tests use Spring Shell auto-wired by the Spring Test Runner.
 *
 * @author Sualeh Fatehi
 */
@SpringBootTest(properties = { InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=" + false })
@ContextConfiguration(classes = TestCalculatorStateConfig.class)
public class CalculatorCommandsIntegrationTest extends BaseCalculatorTest {

	private static final Class<CalculatorCommands> COMMAND_CLASS_UNDER_TEST = CalculatorCommands.class;

	@Autowired
	private Shell shell;

	@Autowired
	private CommandRegistry commandRegistry;

	@Autowired
	private CalculatorState state;

	/**
	 * Test "happy path" or basic addition with positive numbers and zeroes. Use Spring Shell
	 * auto-wired by the Spring Test Runner.
	 */
	@Test
	public void testAdd() {
		final String command = "add";
		final String commandMethod = "add";

		final MethodTarget commandTarget = lookupCommand(commandRegistry, command);
		assertThat(commandTarget).isNotNull();
		assertThat(commandTarget.getGroup()).isEqualTo("Calculator Commands");
		assertThat(commandTarget.getHelp()).isEqualTo("Add two integers");
		assertThat(commandTarget.getMethod())
				.isEqualTo(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, int.class, int.class));
		assertThat(commandTarget.getAvailability().isAvailable()).isTrue();
		Object evaluate = shell.evaluate(() -> command + " 1 2");
		assertThat(evaluate).isEqualTo(3);
		assertThat(state.getMemory()).isEqualTo(0);
	}

	/**
	 * Test "happy path" or basic addition with positive numbers and zeroes, using calculator
	 * memory (program state). Use Spring Shell and calculator memory auto-wired by the Spring
	 * Test Runner.
	 */
	@Test
	public void testAddToMemory() {
		final String command = "add-to-memory";
		final String commandMethod = "addToMemory";

		final MethodTarget commandTarget = lookupCommand(commandRegistry, command);
		assertThat(commandTarget).isNotNull();
		assertThat(commandTarget.getGroup()).isEqualTo("Calculator Commands");
		assertThat(commandTarget.getHelp()).isEqualTo("Add an integer to the value in memory");
		assertThat(commandTarget.getMethod()).isEqualTo(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, int.class));
		assertThat(commandTarget.getAvailability().isAvailable()).isTrue();

		state.setMemory(1);
		Object evaluate = shell.evaluate(() -> command + " 2");
		assertThat(evaluate).isEqualTo(3);
		assertThat(state.getMemory()).isEqualTo(3);
	}

}
