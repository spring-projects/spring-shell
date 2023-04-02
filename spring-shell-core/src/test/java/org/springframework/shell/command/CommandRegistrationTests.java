/*
 * Copyright 2022-2023 the original author or authors.
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

import org.junit.jupiter.api.Test;

import org.springframework.core.ResolvableType;
import org.springframework.shell.Availability;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.CommandRegistration.TargetInfo.TargetType;
import org.springframework.shell.context.InteractionMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommandRegistrationTests extends AbstractCommandTests {

	@Test
	public void testCommandMustBeSet() {
		assertThatThrownBy(() -> {
			CommandRegistration.builder().build();
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("command cannot be empty");
	}

	@Test
	public void testBasics() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getGroup()).isNull();
		assertThat(registration.getInteractionMode()).isEqualTo(InteractionMode.ALL);

		registration = CommandRegistration.builder()
			.command("command1")
			.interactionMode(InteractionMode.NONINTERACTIVE)
			.group("fakegroup")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getInteractionMode()).isEqualTo(InteractionMode.NONINTERACTIVE);
		assertThat(registration.getGroup()).isEqualTo("fakegroup");
	}

	@Test
	public void testCommandStructures() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");

		registration = CommandRegistration.builder()
			.command("command1", "command2")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1 command2");

		registration = CommandRegistration.builder()
			.command("command1 command2")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1 command2");

		registration = CommandRegistration.builder()
			.command(" command1  command2 ")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1 command2");
	}

	@Test
	public void testFunctionRegistration() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getTarget().getTargetType()).isEqualTo(TargetType.FUNCTION);
		assertThat(registration.getTarget().getFunction()).isNotNull();
		assertThat(registration.getTarget().getBean()).isNull();
		assertThat(registration.getTarget().getMethod()).isNull();
	}

	@Test
	public void testConsumerRegistration() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		assertThat(registration.getTarget().getTargetType()).isEqualTo(TargetType.CONSUMER);
		assertThat(registration.getTarget().getFunction()).isNull();
		assertThat(registration.getTarget().getConsumer()).isNotNull();
		assertThat(registration.getTarget().getBean()).isNull();
		assertThat(registration.getTarget().getMethod()).isNull();
	}

	@Test
	public void testMethodRegistration() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.method(pojo1, "method3", String.class)
				.and()
			.build();
		assertThat(registration.getTarget().getTargetType()).isEqualTo(TargetType.METHOD);
		assertThat(registration.getTarget().getFunction()).isNull();
		assertThat(registration.getTarget().getBean()).isNotNull();
		assertThat(registration.getTarget().getMethod()).isNotNull();
	}

	@Test
	public void testCanUseOnlyOneTarget() {
		assertThatThrownBy(() -> {
			CommandRegistration.builder()
				.command("command1")
				.withTarget()
					.method(pojo1, "method3", String.class)
					.function(function1)
					.and()
				.build();
		}).isInstanceOf(IllegalStateException.class).hasMessageContaining("only one target can exist");
	}

	@Test
	public void testSimpleFullRegistrationWithFunction() {
		CommandRegistration registration = CommandRegistration.builder()
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
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getDescription()).isEqualTo("help");
		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).getLongNames()).containsExactly("arg1");
	}

	@Test
	public void testSimpleFullRegistrationWithMethod() {
		CommandRegistration registration = CommandRegistration.builder()
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
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getDescription()).isEqualTo("help");
		assertThat(registration.getOptions()).hasSize(1);
	}

	@Test
	public void testOptionWithType() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('v')
				.type(boolean.class)
				.description("some arg1")
				.label("mylabel")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getDescription()).isEqualTo("help");
		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).getShortNames()).containsExactly('v');
		assertThat(registration.getOptions().get(0).getType()).isEqualTo(ResolvableType.forType(boolean.class));
		assertThat(registration.getOptions().get(0).getLabel()).isEqualTo("mylabel");
	}

	@Test
	public void testOptionWithResolvableType() {
		ResolvableType rtype = ResolvableType.forClassWithGenerics(List.class, String.class);
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg")
				.type(rtype)
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).getType()).satisfies(type -> {
			assertThat(type).isEqualTo(rtype);
		});
	}

	@Test
	public void testOptionWithRequired() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('v')
				.type(boolean.class)
				.description("some arg1")
				.required(true)
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getDescription()).isEqualTo("help");
		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).getShortNames()).containsExactly('v');
		assertThat(registration.getOptions().get(0).isRequired()).isTrue();

		registration = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('v')
				.type(boolean.class)
				.description("some arg1")
				.required(false)
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();

		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).isRequired()).isFalse();

		registration = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('v')
				.type(boolean.class)
				.description("some arg1")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();

		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).isRequired()).isFalse();

		registration = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('v')
				.type(boolean.class)
				.description("some arg1")
				.required()
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();

		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).isRequired()).isTrue();
	}

	@Test
	public void testOptionWithDefaultValue() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.shortNames('v')
				.type(boolean.class)
				.description("some arg1")
				.defaultValue("defaultValue")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getDescription()).isEqualTo("help");
		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).getDefaultValue()).isEqualTo("defaultValue");
	}

	@Test
	public void testOptionWithPositionValue() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.position(1)
				.and()
			.withOption()
				.longNames("arg2")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getOptions()).hasSize(2);
		assertThat(registration.getOptions().get(0).getPosition()).isEqualTo(1);
		assertThat(registration.getOptions().get(1).getPosition()).isEqualTo(-1);
	}

	@Test
	public void testArityViaInts() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.arity(0, 0)
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
			assertThat(registration.getOptions()).hasSize(1);
			assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
			assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(0);
	}

	@Test
	public void testArityViaEnum() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.arity(OptionArity.ZERO)
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
			assertThat(registration.getOptions()).hasSize(1);
			assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
			assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(0);
	}

	@Test
	public void testArityViaEnumSetNone() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.arity(OptionArity.NONE)
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
			assertThat(registration.getOptions()).hasSize(1);
			assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(-1);
			assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(-1);
	}

	@Test
	public void testAliases() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.group("Test Group")
			.withAlias()
				.command("alias1")
				.group("Alias Group")
				.and()
			.withAlias()
				.command("alias2")
				.group("Alias Group")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getCommand()).isEqualTo("command1");
		assertThat(registration.getGroup()).isEqualTo("Test Group");
		assertThat(registration.getAliases()).hasSize(2);
		assertThat(registration.getAliases().stream().map(CommandAlias::getCommand)).containsExactlyInAnyOrder("alias1",
				"alias2");
		assertThat(registration.getAliases().get(0).getGroup()).isEqualTo("Alias Group");
		assertThat(registration.getAliases().get(1).getGroup()).isEqualTo("Alias Group");
	}

	@Test
	public void testExitCodes() {
		CommandRegistration registration;
		registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getExitCode()).isNotNull();
		assertThat(registration.getExitCode().getMappingFunctions()).hasSize(0);

		registration = CommandRegistration.builder()
			.command("command1")
			.withExitCode()
				.map(RuntimeException.class, 1)
				.map(IllegalArgumentException.class, 2)
				.map(e -> 1)
				.map(e -> 2)
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getExitCode()).isNotNull();
		assertThat(registration.getExitCode().getMappingFunctions()).hasSize(4);
	}

	@Test
	public void testAvailability() {
		CommandRegistration registration;
		registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getAvailability()).isNotNull();
		assertThat(registration.getAvailability().isAvailable()).isTrue();

		registration = CommandRegistration.builder()
			.command("command1")
			.availability(() -> Availability.unavailable("fake"))
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getAvailability()).isNotNull();
		assertThat(registration.getAvailability().isAvailable()).isFalse();
	}

	@Test
	public void testOptionWithCompletion() {
		CommandRegistration registration;
		registration = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.completion(ctx -> {
					return new ArrayList<>();
				})
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).getCompletion()).isNotNull();
	}

	@Test
	public void testErrorHandling() {
		CommandExceptionResolver er1 = new CommandExceptionResolver() {
			@Override
			public CommandHandlingResult resolve(Exception e) {
				return CommandHandlingResult.empty();
			}
		};
		CommandRegistration registration;
		registration = CommandRegistration.builder()
			.command("command1")
			.withErrorHandling()
				.resolver(er1)
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getExceptionResolvers()).hasSize(1);
		assertThat(registration.getExceptionResolvers().get(0)).isSameAs(er1);

		registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.getExceptionResolvers()).hasSize(0);
	}

	@Test
	public void testHidden() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.isHidden()).isFalse();

		registration = CommandRegistration.builder()
			.command("command1")
			.hidden()
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.isHidden()).isTrue();

		registration = CommandRegistration.builder()
			.command("command1")
			.hidden(false)
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.isHidden()).isFalse();

		registration = CommandRegistration.builder()
			.command("command1")
			.hidden(true)
			.withTarget()
				.function(function1)
				.and()
			.build();
		assertThat(registration.isHidden()).isTrue();
	}

	@Test
	public void testHelpOption() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withHelpOptions()
				.enabled(true)
				.longNames(new String[] { "help" })
				.shortNames(new Character[] { 'h' })
				.command("help")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();

		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0).getLongNames()).containsExactly("help");
		assertThat(registration.getOptions().get(0).getShortNames()).containsExactly('h');
	}

	@Test
	void testOptionNameModifierInOption() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("command1")
			.withOption()
				.longNames("arg1")
				.nameModifier(name -> "x" + name)
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();

		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0)).satisfies(option -> {
			assertThat(option).isNotNull();
			assertThat(option.getLongNames()).isEqualTo(new String[] { "xarg1" });
		});
	}

	@Test
	void testOptionNameModifierFromDefault() {
		CommandRegistration registration = CommandRegistration.builder()
			.defaultOptionNameModifier(name -> "x" + name)
			.command("command1")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();

		assertThat(registration.getOptions()).hasSize(1);
		assertThat(registration.getOptions().get(0)).satisfies(option -> {
			assertThat(option).isNotNull();
			assertThat(option.getLongNames()).isEqualTo(new String[] { "xarg1" });
		});
	}

	@Test
	void optionShouldBeSameInstance() {
		CommandRegistration registration = CommandRegistration.builder()
			.defaultOptionNameModifier(name -> "x" + name)
			.command("command1")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		List<CommandOption> options1 = registration.getOptions();
		List<CommandOption> options2 = registration.getOptions();
		assertThat(options1).isEqualTo(options2);
	}
}
