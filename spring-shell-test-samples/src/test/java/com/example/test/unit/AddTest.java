package com.example.test.unit;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import com.example.CalculatorCommands;

public class AddTest {

  private CalculatorCommands commands;

  @Before
  public void setup() {
    commands = new CalculatorCommands();
  }

  @Test
  public void addHappyPath() {
    assertThat(commands.add(0, 1), is(1));
    assertThat(commands.add(1, 2), is(3));
    assertThat(commands.add(1, 0), is(1));
  }

  @Test
  public void addNegatives() {
    assertThat(commands.add(0, -1), is(-1));
    assertThat(commands.add(1, -2), is(-1));
    assertThat(commands.add(-1, 0), is(-1));
  }

}
