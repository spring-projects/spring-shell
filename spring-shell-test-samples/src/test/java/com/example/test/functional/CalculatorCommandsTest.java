package com.example.test.functional;

import com.example.CalculatorCommands;
import com.example.CalculatorState;
import com.example.test.BaseCalculatorTest;
import com.example.test.TestCalculatorStateConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.context.DefaultShellContext;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * Illustrative functional tests for the Spring Shell Calculator application. These
 * functional tests use Spring Shell commands auto-wired by the Spring Test Runner outside
 * of the shell, to test functionality of the commands.
 *
 * @author Sualeh Fatehi
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestCalculatorStateConfig.class, CalculatorCommands.class })
public class CalculatorCommandsTest extends BaseCalculatorTest {

	private static final Class<CalculatorCommands> COMMAND_CLASS_UNDER_TEST = CalculatorCommands.class;

	private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry(new DefaultShellContext());

	@Autowired
	private CalculatorState state;

	@Autowired
	private ApplicationContext context;

	@BeforeEach
	public void setup() {
		final StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
		registrar.setApplicationContext(context);
		registrar.register(registry);

		state.clear();
	}

	@Test
	public void testAdd() {
		final String command = "add";
		final String commandMethod = "add";

		final MethodTarget commandTarget = lookupCommand(registry, command);
		assertThat(commandTarget).isNotNull();
		assertThat(commandTarget.getGroup()).isEqualTo("Calculator Commands");
		assertThat(commandTarget.getHelp()).isEqualTo("Add two integers");
		assertThat(commandTarget.getMethod())
				.isEqualTo(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, int.class, int.class));
		assertThat(commandTarget.getAvailability().isAvailable()).isTrue();
		Object invoke = invoke(commandTarget, 1, 2);
		assertThat(invoke).isEqualTo(3);
		assertThat(state.getMemory()).isEqualTo(0);
	}

	@Test
	public void testaddToMemory() {
		final String command = "add-to-memory";
		final String commandMethod = "addToMemory";

		final MethodTarget commandTarget = lookupCommand(registry, command);
		assertThat(commandTarget).isNotNull();
		assertThat(commandTarget.getGroup()).isEqualTo("Calculator Commands");
		assertThat(commandTarget.getHelp()).isEqualTo("Add an integer to the value in memory");
		assertThat(commandTarget.getMethod()).isEqualTo(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, int.class));
		assertThat(commandTarget.getAvailability().isAvailable()).isTrue();

		state.setMemory(1);
		Object invoke = invoke(commandTarget, 2);
		assertThat(invoke).isEqualTo(3);
		assertThat(state.getMemory()).isEqualTo(3);
	}

}
