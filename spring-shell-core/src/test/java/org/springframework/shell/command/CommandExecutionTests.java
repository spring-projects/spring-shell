/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.command;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.shell.Availability;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.command.CommandExecution.CommandParserExceptionsException;
import org.springframework.shell.command.CommandRegistration.OptionArity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommandExecutionTests extends AbstractCommandTests {

	private CommandExecution execution;
	private CommandCatalog commandCatalog;

	@BeforeEach
	public void setupCommandExecutionTests() {
		commandCatalog = CommandCatalog.of();
		ConversionService conversionService = new DefaultConversionService();
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
		resolvers.add(new ArgumentHeaderMethodArgumentResolver(conversionService, null));
		resolvers.add(new CommandContextMethodArgumentResolver());
		execution = CommandExecution.of(resolvers, null, null, null, conversionService, commandCatalog);
	}

	@Test
	public void testFunctionExecution() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		commandCatalog.register(r1);
		Object result = execution.evaluate(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(result).isEqualTo("himyarg1value");
	}

	@Test
	public void testMethodExecution1() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withTarget()
				.method(pojo1, "method3", String.class)
				.and()
			.build();
		commandCatalog.register(r1);
		Object result = execution.evaluate(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(result).isEqualTo("himyarg1value");
		assertThat(pojo1.method3Count).isEqualTo(1);
	}

	@Test
	public void testMethodExecution2() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withTarget()
				.method(pojo1, "method1")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(pojo1.method1Count).isEqualTo(1);
		assertThat(pojo1.method1Ctx).isNotNull();
	}

	@Test
	public void testMixedWithCtx1() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.description("some arg1")
				.and()
			.withTarget()
				.method(pojo1, "method1Mixed1")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1" });
		assertThat(pojo1.method1Mixed1Count).isEqualTo(1);
		assertThat(pojo1.method1Mixed1Arg1).isNull();
		assertThat(pojo1.method1Mixed1Ctx).isNotNull();
		assertThat(pojo1.method1Mixed1Arg2).isNull();
	}

	@Test
	public void testMixedWithCtx2() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.description("some arg1")
				.and()
			.withTarget()
				.method(pojo1, "method1Mixed1")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(pojo1.method1Mixed1Count).isEqualTo(1);
		assertThat(pojo1.method1Mixed1Arg1).isEqualTo("myarg1value");
		assertThat(pojo1.method1Mixed1Ctx).isNotNull();
		assertThat(pojo1.method1Mixed1Arg2).isNull();
	}

	@Test
	public void testMixedWithCtx3() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.description("some arg1")
				.and()
			.withTarget()
				.method(pojo1, "method1Mixed1")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "myarg1value", "--arg2", "myarg2value" });
		assertThat(pojo1.method1Mixed1Count).isEqualTo(1);
		assertThat(pojo1.method1Mixed1Arg1).isEqualTo("myarg1value");
		assertThat(pojo1.method1Mixed1Ctx).isNotNull();
		assertThat(pojo1.method1Mixed1Arg2).isEqualTo("myarg2value");
	}

	@Test
	public void testMethodArgWithoutValue() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1" });
		assertThat(pojo1.method4Count).isEqualTo(1);
		assertThat(pojo1.method4Arg1).isNull();
	}

	@Test
	public void testMethodSinglePositionalArgs() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "myarg1value" });
		assertThat(pojo1.method4Count).isEqualTo(1);
		assertThat(pojo1.method4Arg1).isEqualTo("myarg1value");
	}

	@Test
	public void testMethodSingleWithNamedArgs() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		Object result = execution.evaluate(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(pojo1.method4Count).isEqualTo(1);
		assertThat(pojo1.method4Arg1).isEqualTo("myarg1value");
		assertThat(result).isEqualTo("himyarg1value");
	}

	@Test
	public void testMethodMultiPositionalArgs() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "myarg1value1", "myarg1value2" });
		assertThat(pojo1.method4Count).isEqualTo(1);
		assertThat(pojo1.method4Arg1).isEqualTo("myarg1value1");
	}

	@Test
	public void testMethodMultiPositionalArgsAll() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.arity(OptionArity.ONE_OR_MORE)
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "myarg1value1", "myarg1value2" });
		assertThat(pojo1.method4Count).isEqualTo(1);
		assertThat(pojo1.method4Arg1).isEqualTo("myarg1value1,myarg1value2");
	}

	@Test
	public void testMethodMultiPositionalArgsAllToArray1() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.arity(OptionArity.ONE_OR_MORE)
				.and()
			.withTarget()
				.method(pojo1, "method9")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "myarg1value1", "myarg1value2" });
		assertThat(pojo1.method9Count).isEqualTo(1);
		assertThat(pojo1.method9Arg1).isEqualTo(new String[] { "myarg1value1", "myarg1value2" });
	}

	@Test
	public void testMethodMultiPositionalArgsAllToArray2() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.arity(OptionArity.ONE_OR_MORE)
				.and()
			.withTarget()
				.method(pojo1, "method8")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "1", "2" });
		assertThat(pojo1.method8Count).isEqualTo(1);
		assertThat(pojo1.method8Arg1).isEqualTo(new float[] { 1, 2 });
	}

	@Test
	public void testMethodMultipleArgs() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.description("some arg2")
				.and()
			.withOption()
				.longNames("arg3")
				.description("some arg3")
				.and()
			.withTarget()
				.method(pojo1, "method6")
				.and()
			.build();

		commandCatalog.register(r1);
		execution.evaluate(
				new String[] { "command1", "--arg1", "myarg1value", "--arg2", "myarg2value", "--arg3", "myarg3value" });
		assertThat(pojo1.method6Count).isEqualTo(1);
		assertThat(pojo1.method6Arg1).isEqualTo("myarg1value");
		assertThat(pojo1.method6Arg2).isEqualTo("myarg2value");
		assertThat(pojo1.method6Arg3).isEqualTo("myarg3value");
	}


	@Test
	public void testMethodMultipleIntArgs() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.description("some arg2")
				.and()
			.withOption()
				.longNames("arg3")
				.description("some arg3")
				.and()
			.withTarget()
				.method(pojo1, "method7")
				.and()
			.build();

		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "1", "--arg2", "2", "--arg3", "3" });
		assertThat(pojo1.method7Count).isEqualTo(1);
		assertThat(pojo1.method7Arg1).isEqualTo(1);
		assertThat(pojo1.method7Arg2).isEqualTo(2);
		assertThat(pojo1.method7Arg3).isEqualTo(3);
	}

	@Test
	public void testMethodMultiplePositionalStringArgs() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withOption()
				.longNames("arg2")
				.description("some arg2")
				.position(1)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withOption()
				.longNames("arg3")
				.description("some arg3")
				.position(2)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withTarget()
				.method(pojo1, "method6")
				.and()
			.build();

		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "myarg1value", "myarg2value", "myarg3value" });
		assertThat(pojo1.method6Count).isEqualTo(1);
		assertThat(pojo1.method6Arg1).isEqualTo("myarg1value");
		assertThat(pojo1.method6Arg2).isEqualTo("myarg2value");
		assertThat(pojo1.method6Arg3).isEqualTo("myarg3value");
	}

	@ParameterizedTest
	@Disabled("concepts change")
	@ValueSource(strings = {
		"command1 myarg1value --arg2 myarg2value --arg3 myarg3value",
		"command1 --arg1 myarg1value myarg2value --arg3 myarg3value",
		"command1 --arg1 myarg1value --arg2 myarg2value myarg3value"
	})
	public void testMethodMultiplePositionalStringArgsMixed(String arg) {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.position(0)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withOption()
				.longNames("arg2")
				.description("some arg2")
				.position(1)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withOption()
				.longNames("arg3")
				.description("some arg3")
				.position(2)
				.arity(OptionArity.EXACTLY_ONE)
				.and()
			.withTarget()
				.method(pojo1, "method6")
				.and()
			.build();
		String[] args = arg.split(" ");
		commandCatalog.register(r1);
		execution.evaluate(args);
		assertThat(pojo1.method6Count).isEqualTo(1);
		assertThat(pojo1.method6Arg1).isEqualTo("myarg1value");
		assertThat(pojo1.method6Arg2).isEqualTo("myarg2value");
		assertThat(pojo1.method6Arg3).isEqualTo("myarg3value");
	}

	@Test
	public void testShortCombinedWithoutValue() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('a')
				.description("short arg a")
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('b')
				.description("short arg b")
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('c')
				.description("short arg c")
				.type(boolean.class)
				.and()
			.withTarget()
				.method(pojo1, "method5")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "-abc" });
		assertThat(pojo1.method5ArgA).isTrue();
		assertThat(pojo1.method5ArgB).isTrue();
		assertThat(pojo1.method5ArgC).isTrue();
	}

	@Test
	public void testShortCombinedSomeHavingValue() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('a')
				.description("short arg a")
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('b')
				.description("short arg b")
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('c')
				.description("short arg c")
				.type(boolean.class)
				.and()
			.withTarget()
				.method(pojo1, "method5")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "-ac", "-b", "false" });
		assertThat(pojo1.method5ArgA).isTrue();
		assertThat(pojo1.method5ArgB).isFalse();
		assertThat(pojo1.method5ArgC).isTrue();
	}

	@Test
	public void testFloatArrayOne() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.type(float[].class)
				.and()
			.withTarget()
				.method(pojo1, "method8")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "0.1" });
		assertThat(pojo1.method8Count).isEqualTo(1);
		assertThat(pojo1.method8Arg1).isEqualTo(new float[]{0.1f});
	}

	@Test
	public void testFloatArrayTwo() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.type(float[].class)
				.and()
			.withTarget()
				.method(pojo1, "method8")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "0.1", "0.2" });
		assertThat(pojo1.method8Count).isEqualTo(1);
		assertThat(pojo1.method8Arg1).isEqualTo(new float[]{0.1f, 0.2f});
	}

	@Test
	public void testDefaultValueAsNull() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1" });
		assertThat(pojo1.method4Count).isEqualTo(1);
		assertThat(pojo1.method4Arg1).isNull();
	}

	@Test
	public void testDefaultValue() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.defaultValue("defaultValue1")
				// .position(0)
				// .arity(OptionArity.EXACTLY_ONE)
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1" });
		assertThat(pojo1.method4Count).isEqualTo(1);
		assertThat(pojo1.method4Arg1).isEqualTo("defaultValue1");
	}

	@Test
	public void testRequiredArg() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.required()
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();

		commandCatalog.register(r1);
		assertThatThrownBy(() -> {
			execution.evaluate(new String[] { "command1" });
		}).isInstanceOf(CommandParserExceptionsException.class);
	}

	@Test
	public void testCommandNotAvailable() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.availability(() -> Availability.unavailable("fake reason"))
			.withTarget()
				.function(function1)
				.and()
			.build();
		commandCatalog.register(r1);
		Object result = execution.evaluate(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(result).isInstanceOf(CommandNotCurrentlyAvailable.class);
	}

	@Test
	public void testExecutionWithModifiedLongOption() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.nameModifier(orig -> "x" + orig)
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		commandCatalog.register(r1);
		Object result = execution.evaluate(new String[] { "command1", "--xarg1", "myarg1value" });
		assertThat(result).isEqualTo("himyarg1value");
	}

}
