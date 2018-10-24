package com.example.test.unit;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;
import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;
import com.example.CalculatorCommands;
import com.example.CalculatorState;

public class AddToMemoryTest {

  private CalculatorCommands commands;
  private CalculatorState state;

  @Test
  public void addToMemoryHappyPath() {
    assertThat(commands.addToMemory(1), is(1));
    assertThat(state.getMemory(), is(1));

    assertThat(commands.addToMemory(2), is(3));
    assertThat(state.getMemory(), is(3));

    assertThat(commands.addToMemory(0), is(3));
    assertThat(state.getMemory(), is(3));
  }

  @Test
  public void addToMemoryNegatives() {
    assertThat(commands.addToMemory(-1), is(-1));
    assertThat(state.getMemory(), is(-1));

    assertThat(commands.addToMemory(-2), is(-3));
    assertThat(state.getMemory(), is(-3));

    assertThat(commands.addToMemory(0), is(-3));
    assertThat(state.getMemory(), is(-3));
  }

  @Before
  public void setup() {
    state = new CalculatorState();
    commands = new CalculatorCommands();
    final Field stateField = findField(CalculatorCommands.class, "state");
    makeAccessible(stateField);
    setField(stateField, commands, state);
  }

}
