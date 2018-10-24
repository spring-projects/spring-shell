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
import com.example.CalculatorCommands;
import com.example.CalculatorState;
import com.example.test.BaseCalculatorTest;
import com.example.test.TestCalculatorStateConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestCalculatorStateConfig.class, CalculatorCommands.class})
public class CalculatorCommandsTest extends BaseCalculatorTest {

  private static final Class<CalculatorCommands> COMMAND_CLASS_UNDER_TEST =
      CalculatorCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();

  @Autowired
  private CalculatorState state;
  @Autowired
  private ApplicationContext context;

  @Before
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
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("Calculator Commands"));
    assertThat(commandTarget.getHelp(), is("Add two integers"));
    assertThat(commandTarget.getMethod(),
        is(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, int.class, int.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));
    assertThat(invoke(commandTarget, 1, 2), is(3));
    assertThat(state.getMemory(), is(0));
  }

  @Test
  public void testaddToMemory() {
    final String command = "add-to-memory";
    final String commandMethod = "addToMemory";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("Calculator Commands"));
    assertThat(commandTarget.getHelp(), is("Add an integer to the value in memory"));
    assertThat(commandTarget.getMethod(),
        is(findMethod(COMMAND_CLASS_UNDER_TEST, commandMethod, int.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    state.setMemory(1);
    assertThat(invoke(commandTarget, 2), is(3));
    assertThat(state.getMemory(), is(3));
  }

}
