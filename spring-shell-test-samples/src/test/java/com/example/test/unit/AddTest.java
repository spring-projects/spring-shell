package com.example.test.unit;

import com.example.CalculatorCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * Illustrative unit tests for the Spring Shell Calculator application. The unit tests
 * test calculator commands as "plain-old Java objects" or POJOs, without relying on the
 * Spring Framework. Boundary conditions of individual methods are tested.
 *
 * @author Sualeh Fatehi
 */
public class AddTest {

	private CalculatorCommands commands;

	/**
	 * Setup test calculator commands as "plain-old Java objects" or POJOs, initialized for
	 * each test.
	 */
	@BeforeEach
	public void setup() {
		commands = new CalculatorCommands();
	}

	/**
	 * Test "happy path" or basic addition with positive numbers and zeroes.
	 */
	@Test
	public void addHappyPath() {
		assertThat(commands.add(0, 1)).isEqualTo(1);
		assertThat(commands.add(1, 2)).isEqualTo(3);
		assertThat(commands.add(1, 0)).isEqualTo(1);
	}

	/**
	 * Test addition with negative numbers and zeroes.
	 */
	@Test
	public void addNegatives() {
		assertThat(commands.add(0, -1)).isEqualTo(-1);
		assertThat(commands.add(1, -2)).isEqualTo(-1);
		assertThat(commands.add(-1, 0)).isEqualTo(-1);
	}

}
