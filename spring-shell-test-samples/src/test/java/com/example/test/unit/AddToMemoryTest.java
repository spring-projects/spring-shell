package com.example.test.unit;

import java.lang.reflect.Field;

import com.example.CalculatorCommands;
import com.example.CalculatorState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

/**
 *
 * Illustrative unit tests for the Spring Shell Calculator application. The unit tests
 * test calculator commands as "plain-old Java objects" or POJOs, without relying on the
 * Spring Framework. Boundary conditions of individual methods are tested.
 *
 * @author Sualeh Fatehi
 */
public class AddToMemoryTest {

	private CalculatorCommands commands;

	private CalculatorState state;

	/**
	 * Setup test calculator commands as "plain-old Java objects" or POJOs, initialized for
	 * each test.
	 */
	@BeforeEach
	public void setup() {
		state = new CalculatorState();
		commands = new CalculatorCommands();
		final Field stateField = findField(CalculatorCommands.class, "state");
		makeAccessible(stateField);
		setField(stateField, commands, state);
	}

	/**
	 * Test "happy path" or basic addition with positive numbers and zeroes, using calculator
	 * memory (program state).
	 */
	@Test
	public void addToMemoryHappyPath() {
		assertThat(commands.addToMemory(1)).isEqualTo(1);
		assertThat(state.getMemory()).isEqualTo(1);

		assertThat(commands.addToMemory(2)).isEqualTo(3);
		assertThat(state.getMemory()).isEqualTo(3);

		assertThat(commands.addToMemory(0)).isEqualTo(3);
		assertThat(state.getMemory()).isEqualTo(3);
	}

	/**
	 * Test addition with negative numbers and zeroes, using calculator memory (program
	 * state).
	 */
	@Test
	public void addToMemoryNegatives() {
		assertThat(commands.addToMemory(-1)).isEqualTo(-1);
		assertThat(state.getMemory()).isEqualTo(-1);

		assertThat(commands.addToMemory(-2)).isEqualTo(-3);
		assertThat(state.getMemory()).isEqualTo(-3);

		assertThat(commands.addToMemory(0)).isEqualTo(-3);
		assertThat(state.getMemory()).isEqualTo(-3);
	}

}
