package com.example.test.unit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import com.example.ShellCommands;

/**
 *
 * Illustrative unit tests for the Spring Shell Calculator application. The unit tests
 * test calculator commands as "plain-old Java objects" or POJOs, without relying on the
 * Spring Framework. Boundary conditions of individual methods are tested.
 *
 * @author Sualeh Fatehi
 */
public class GreetTest {

	private ShellCommands commands;

	/**
	 * Setup test calculator commands as "plain-old Java objects" or POJOs, initialized for
	 * each test.
	 */
	@Before
	public void setup() {
		commands = new ShellCommands();
	}

	/**
	 * Test "happy path" or basic addition with positive numbers and zeroes.
	 */
	@Test
	public void testGreet() {
		//assertThat(commands.greet("John"), is("Hello, John"));
	}

}
