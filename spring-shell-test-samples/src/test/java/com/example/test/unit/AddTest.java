package com.example.test.unit;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import com.example.CalculatorCommands;

/**
 *
 * Illustrative unit tests for the Spring Shell Calculator application.
 * The unit tests test calculator commands as "plain-old Java objects"
 * or POJOs, without relying on the Spring Framework.
 * Boundary conditions of individual methods are tested.
 *
 * @author Sualeh Fatehi <sualeh@hotmail.com>
 *
 **/
public class AddTest {

  private CalculatorCommands commands;

  /**
	 * Setup test calculator commands as "plain-old Java objects"
   * or POJOs, initialized for each test.
	 **/
  @Before
  public void setup() {
    commands = new CalculatorCommands();
  }

  /**
	 * Test "happy path" or basic addition with positive numbers
	 * and zeroes.
	 **/
  @Test
  public void addHappyPath() {
    assertThat(commands.add(0, 1), is(1));
    assertThat(commands.add(1, 2), is(3));
    assertThat(commands.add(1, 0), is(1));
  }

  /**
	 * Test addition with negative numbers and zeroes.
	 **/
  @Test
  public void addNegatives() {
    assertThat(commands.add(0, -1), is(-1));
    assertThat(commands.add(1, -2), is(-1));
    assertThat(commands.add(-1, 0), is(-1));
  }

}
