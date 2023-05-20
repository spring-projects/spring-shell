/*
 * Copyright 2017-2023 the original author or authors.
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

package org.springframework.shell.standard;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.Availability;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.context.DefaultShellContext;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.test1.GroupOneCommands;
import org.springframework.shell.standard.test2.GroupThreeCommands;
import org.springframework.shell.standard.test2.GroupTwoCommands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link StandardMethodTargetRegistrar}.
 *
 * @author Eric Bottard
 */
public class StandardMethodTargetRegistrarTests {

	private StandardMethodTargetRegistrar registrar;
	private AnnotationConfigApplicationContext applicationContext;
	private CommandCatalog catalog;
	private DefaultShellContext shellContext;
	private CommandRegistration.BuilderSupplier builder = () -> CommandRegistration.builder();

	@BeforeEach
	public void setup() {
		shellContext = new DefaultShellContext();
		catalog = CommandCatalog.of(null, shellContext);
	}

	@AfterEach
	public void cleanup() {
		if (applicationContext != null) {
			applicationContext.close();
		}
		applicationContext = null;
		catalog = null;
	}

	@Test
	public void testRegistrations() {
		applicationContext = new AnnotationConfigApplicationContext(Sample1.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);
		Map<String, CommandRegistration> registrations = catalog.getRegistrations();
		assertThat(registrations).hasSize(3);

		assertThat(registrations.get("say-hello")).isNotNull();
		assertThat(registrations.get("say-hello").getAvailability()).isNotNull();
		assertThat(registrations.get("say-hello").getOptions()).hasSize(1);
		assertThat(registrations.get("say-hello").getOptions().get(0).getLongNames()).containsExactly("what");
		assertThat(registrations.get("say-hello").getOptions().get(0).isRequired()).isTrue();

		assertThat(registrations.get("hi")).isNotNull();
		assertThat(registrations.get("hi").getAvailability()).isNotNull();

		assertThat(registrations.get("alias")).isNotNull();
		assertThat(registrations.get("alias").getAvailability()).isNotNull();
	}

	@ShellComponent
	public static class Sample1 {

		@ShellMethod("some command")
		public String sayHello(String what) {
			return "hello " + what;
		}

		@ShellMethod(value = "method with alias", key = {"hi", "alias"})
		public String greet(String what) {
			return "hi " + what;
		}
	}

	@Test
	public void testOptionRequiredWithAnnotation() {
		applicationContext = new AnnotationConfigApplicationContext(Sample2.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);
		Map<String, CommandRegistration> registrations = catalog.getRegistrations();
		assertThat(registrations).hasSize(1);

		assertThat(registrations.get("say-hello")).isNotNull();
		assertThat(registrations.get("say-hello").getOptions()).hasSize(1);
		assertThat(registrations.get("say-hello").getOptions().get(0).isRequired()).isTrue();
	}

	@ShellComponent
	public static class Sample2 {

		@ShellMethod("some command")
		public String sayHello(@ShellOption String what) {
			return "hello " + what;
		}
	}

	@Test
	public void testOptionOptionalWithAnnotation() {
		applicationContext = new AnnotationConfigApplicationContext(Sample3.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);
		Map<String, CommandRegistration> registrations = catalog.getRegistrations();
		assertThat(registrations).hasSize(1);

		assertThat(registrations.get("say-hello")).isNotNull();
		assertThat(registrations.get("say-hello").getOptions()).hasSize(1);
		assertThat(registrations.get("say-hello").getOptions().get(0).isRequired()).isFalse();
		assertThat(registrations.get("say-hello").getOptions().get(0).getDefaultValue()).isNull();
	}

	@ShellComponent
	public static class Sample3 {

		@ShellMethod("some command")
		public String sayHello(@ShellOption( defaultValue = ShellOption.NULL) String what) {
			return "hello " + what;
		}
	}

	@Test
	public void testAvailabilityIndicators() {
		applicationContext = new AnnotationConfigApplicationContext(SampleWithAvailability.class);
		SampleWithAvailability sample = applicationContext.getBean(SampleWithAvailability.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);
		Map<String, CommandRegistration> registrations = catalog.getRegistrations();

		assertThat(registrations.get("say-hello")).isNotNull();
		assertThat(registrations.get("say-hello").getAvailability().isAvailable()).isTrue();
		sample.available = false;
		assertThat(registrations.get("say-hello").getAvailability().isAvailable()).isFalse();
		assertThat(registrations.get("say-hello").getAvailability().getReason()).isEqualTo("sayHelloAvailability");
		sample.available = true;

		assertThat(registrations.get("hi")).isNotNull();
		assertThat(registrations.get("hi").getAvailability().isAvailable()).isTrue();
		sample.available = false;
		assertThat(registrations.get("hi").getAvailability().isAvailable()).isFalse();
		assertThat(registrations.get("hi").getAvailability().getReason()).isEqualTo("customAvailabilityMethod");
		sample.available = true;

		assertThat(registrations.get("bonjour")).isNotNull();
		assertThat(registrations.get("bonjour").getAvailability().isAvailable()).isTrue();
		sample.available = false;
		assertThat(registrations.get("bonjour").getAvailability().isAvailable()).isFalse();
		assertThat(registrations.get("bonjour").getAvailability().getReason()).isEqualTo("availabilityForSeveralCommands");
		sample.available = true;
	}

	@ShellComponent
	public static class SampleWithAvailability {

		private boolean available = true;

		@ShellMethod("some command with an implicit availability indicator")
		public void sayHello() {

		}
		public Availability sayHelloAvailability() {
			return available ? Availability.available() : Availability.unavailable("sayHelloAvailability");
		}


		@ShellMethodAvailability("customAvailabilityMethod")
		@ShellMethod("some method with an explicit availability indicator")
		public void hi() {

		}
		public Availability customAvailabilityMethod() {
			return available ? Availability.available() : Availability.unavailable("customAvailabilityMethod");
		}

		@ShellMethod(value = "some method with an explicit availability indicator", key = {"bonjour", "salut"})
		public void bonjour() {

		}

		@ShellMethodAvailability({"salut", "other"})
		public Availability availabilityForSeveralCommands() {
			return available ? Availability.available() : Availability.unavailable("availabilityForSeveralCommands");
		}

		@ShellMethod("a command whose availability indicator will come from wildcard")
		public void wild() {

		}

		@ShellMethodAvailability("*")
		private Availability availabilityFromWildcard() {
			return available ? Availability.available() : Availability.unavailable("availabilityFromWildcard");
		}
	}

	@Test
	public void testAvailabilityIndicatorErrorMultipleExplicit() {
		applicationContext = new AnnotationConfigApplicationContext(WrongAvailabilityIndicatorOnShellMethod.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);

		assertThatThrownBy(() -> {
			registrar.register(catalog);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("When set on a @ShellMethod method, the value of the @ShellMethodAvailability should be a single element")
				.hasMessageContaining("Found [one, two]")
				.hasMessageContaining("wrong()");
	}

	@ShellComponent
	public static class WrongAvailabilityIndicatorOnShellMethod {

		@ShellMethodAvailability({"one", "two"})
		@ShellMethod("foo")
		public void wrong() {
		}
	}

	@Test
	public void testAvailabilityIndicatorWildcardNotAlone() {
		applicationContext = new AnnotationConfigApplicationContext(WrongAvailabilityIndicatorWildcardNotAlone.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);

		assertThatThrownBy(() -> {
			registrar.register(catalog);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("When using '*' as a wildcard for ShellMethodAvailability, this can be the only value. Found [one, *]")
				.hasMessageContaining("availability()");
	}

	@ShellComponent
	public static class WrongAvailabilityIndicatorWildcardNotAlone {

		@ShellMethodAvailability({"one", "*"})
		public Availability availability() {
			return Availability.available();
		}

		@ShellMethod("foo")
		public void wrong() {

		}
	}

	@Test
	public void testAvailabilityIndicatorAmbiguous() {
		applicationContext = new AnnotationConfigApplicationContext(WrongAvailabilityIndicatorAmbiguous.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);

		assertThatThrownBy(() -> {
			registrar.register(catalog);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Found several @ShellMethodAvailability")
				.hasMessageContaining("wrong()")
				.hasMessageContaining("availability()")
				.hasMessageContaining("otherAvailability()");
	}

	@ShellComponent
	public static class WrongAvailabilityIndicatorAmbiguous {

		@ShellMethodAvailability({"one", "wrong"})
		public Availability availability() {
			return Availability.available();
		}

		@ShellMethodAvailability({"bar", "wrong"})
		public Availability otherAvailability() {
			return Availability.available();
		}

		@ShellMethod("foo")
		public void wrong() {

		}
	}

	@Test
	public void testGrouping() {
		applicationContext = new AnnotationConfigApplicationContext(GroupOneCommands.class,
				GroupTwoCommands.class, GroupThreeCommands.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("explicit1")).satisfies(registration -> {
			assertThat(registration).isNotNull();
			assertThat(registration.getGroup()).isEqualTo("Explicit Group Method Level 1");
		});
		assertThat(catalog.getRegistrations().get("explicit2")).satisfies(registration -> {
			assertThat(registration).isNotNull();
			assertThat(registration.getGroup()).isEqualTo("Explicit Group Method Level 2");
		});
		assertThat(catalog.getRegistrations().get("explicit3")).satisfies(registration -> {
			assertThat(registration).isNotNull();
			assertThat(registration.getGroup()).isEqualTo("Explicit Group Method Level 3");
		});
		assertThat(catalog.getRegistrations().get("implicit1")).satisfies(registration -> {
			assertThat(registration).isNotNull();
			assertThat(registration.getGroup()).isEqualTo("Implicit Group Package Level 1");
		});
		assertThat(catalog.getRegistrations().get("implicit2")).satisfies(registration -> {
			assertThat(registration).isNotNull();
			assertThat(registration.getGroup()).isEqualTo("Group Two Commands");
		});
		assertThat(catalog.getRegistrations().get("implicit3")).satisfies(registration -> {
			assertThat(registration).isNotNull();
			assertThat(registration.getGroup()).isEqualTo("Explicit Group 3 Class Level");
		});
	}

	@Test
	public void testInteractionModeInteractive() {
	    shellContext.setInteractionMode(InteractionMode.INTERACTIVE);
		applicationContext = new AnnotationConfigApplicationContext(InteractionModeCommands.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
	    registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo2")).isNull();
		assertThat(catalog.getRegistrations().get("foo3")).isNotNull();
	}

	@Test
	public void testInteractionModeNonInteractive() {
	    shellContext.setInteractionMode(InteractionMode.NONINTERACTIVE);
		applicationContext = new AnnotationConfigApplicationContext(InteractionModeCommands.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
	    registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNull();
		assertThat(catalog.getRegistrations().get("foo2")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo3")).isNotNull();
	}

	@ShellComponent
	public static class InteractionModeCommands {

		@ShellMethod(value = "foo1", interactionMode = InteractionMode.INTERACTIVE)
		public void foo1() {
		}

		@ShellMethod(value = "foo2", interactionMode = InteractionMode.NONINTERACTIVE)
		public void foo2() {
		}

		@ShellMethod(value = "foo3")
		public void foo3() {
		}
	}

	@Test
	public void testOptionUseDefaultValue() {
	    shellContext.setInteractionMode(InteractionMode.NONINTERACTIVE);
		applicationContext = new AnnotationConfigApplicationContext(DefaultValuesCommands.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
	    registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo1").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getDefaultValue()).isEqualTo("arg1Value");

		assertThat(catalog.getRegistrations().get("foo2")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo2").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo2").getOptions().get(0).getDefaultValue()).isNull();

		assertThat(catalog.getRegistrations().get("foo3")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo3").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo3").getOptions().get(0).getDefaultValue()).isNull();
	}

	@ShellComponent
	public static class DefaultValuesCommands {

		@ShellMethod(value = "foo1")
		public void foo1(@ShellOption(defaultValue = "arg1Value") String arg1) {
		}

		@ShellMethod(value = "foo2")
		public void foo2(@ShellOption(defaultValue = ShellOption.NONE) String arg1) {
		}

		@ShellMethod(value = "foo")
		public void foo3(@ShellOption(defaultValue = ShellOption.NULL) String arg1) {
		}
	}

	@Test
	public void testOptionValuesWithBoolean() {
		applicationContext = new AnnotationConfigApplicationContext(ValuesWithBoolean.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo1").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getDefaultValue()).isEqualTo("false");
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).isRequired()).isFalse();
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getArityMin()).isEqualTo(0);
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getArityMax()).isEqualTo(1);

		assertThat(catalog.getRegistrations().get("foo2")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo2").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo2").getOptions().get(0).getDefaultValue()).isEqualTo("true");
		assertThat(catalog.getRegistrations().get("foo2").getOptions().get(0).isRequired()).isFalse();
		assertThat(catalog.getRegistrations().get("foo2").getOptions().get(0).getArityMin()).isEqualTo(0);
		assertThat(catalog.getRegistrations().get("foo2").getOptions().get(0).getArityMax()).isEqualTo(1);

		assertThat(catalog.getRegistrations().get("foo3")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo3").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo3").getOptions().get(0).isRequired()).isFalse();
		assertThat(catalog.getRegistrations().get("foo3").getOptions().get(0).getDefaultValue()).isEqualTo("false");
		assertThat(catalog.getRegistrations().get("foo3").getOptions().get(0).getArityMin()).isEqualTo(0);
		assertThat(catalog.getRegistrations().get("foo3").getOptions().get(0).getArityMax()).isEqualTo(1);

		assertThat(catalog.getRegistrations().get("foo4")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo4").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo4").getOptions().get(0).isRequired()).isFalse();
		assertThat(catalog.getRegistrations().get("foo4").getOptions().get(0).getDefaultValue()).isEqualTo("false");
		assertThat(catalog.getRegistrations().get("foo4").getOptions().get(0).getArityMin()).isEqualTo(0);
		assertThat(catalog.getRegistrations().get("foo4").getOptions().get(0).getArityMax()).isEqualTo(1);
	}

	@ShellComponent
	public static class ValuesWithBoolean {

		@ShellMethod(value = "foo1")
		public void foo1(@ShellOption(defaultValue = "false") boolean arg1) {
		}

		@ShellMethod(value = "foo2")
		public void foo2(@ShellOption(defaultValue = "true") boolean arg1) {
		}

		@ShellMethod(value = "foo3")
		public void foo3(@ShellOption boolean arg1) {
		}

		@ShellMethod(value = "foo4")
		public void foo4(boolean arg1) {
		}
	}

	@Test
	public void testOptionWithoutHyphenRegisterFromDefaultPrefix() {
		applicationContext = new AnnotationConfigApplicationContext(OptionWithoutHyphenRegisterFromDefaultPrefix.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo1").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getLongNames()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getShortNames()).hasSize(0);
	}

	@ShellComponent
	public static class OptionWithoutHyphenRegisterFromDefaultPrefix {

		@ShellMethod(value = "foo1")
		public void foo1(@ShellOption("xxx") boolean arg1) {
		}
	}

	@Test
	public void testOptionWithoutHyphenRegisterFromChangedPrefix() {
		applicationContext = new AnnotationConfigApplicationContext(OptionWithoutHyphenRegisterFromChangedPrefix.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo1").getOptions()).hasSize(1);
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getLongNames()).hasSize(0);
		assertThat(catalog.getRegistrations().get("foo1").getOptions().get(0).getShortNames()).hasSize(1);
	}

	@ShellComponent
	public static class OptionWithoutHyphenRegisterFromChangedPrefix {

		@ShellMethod(value = "foo1", prefix = "-")
		public void foo1(@ShellOption("x") boolean arg1) {
		}
	}

	@Test
	void OptionWithCustomType() {
		applicationContext = new AnnotationConfigApplicationContext(OptionWithCustomType.class);
		registrar = new StandardMethodTargetRegistrar(applicationContext, builder);
		registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo1")).satisfies(reg -> {
			assertThat(reg.getOptions().get(0)).satisfies(option -> {
				assertThat(option.getType().getGeneric(0).getType()).isEqualTo(Pojo.class);
			});
		});

	}

	@ShellComponent
	public static class OptionWithCustomType {

		@ShellMethod(value = "foo1", prefix = "-")
		public void foo1(@ShellOption Set<Pojo> arg1) {
		}
	}

	public static class Pojo {

	}
}
