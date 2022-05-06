/*
 * Copyright 2017-2022 the original author or authors.
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

	private StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
	private AnnotationConfigApplicationContext applicationContext;
	private CommandCatalog catalog;
	private DefaultShellContext shellContext;

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
		applicationContext = new AnnotationConfigApplicationContext(Sample.class);
		registrar.setApplicationContext(applicationContext);
		registrar.register(catalog);
		Map<String, CommandRegistration> registrations = catalog.getRegistrations();
		assertThat(registrations).hasSize(3);

		assertThat(registrations.get("say-hello")).isNotNull();
		assertThat(registrations.get("say-hello").getAvailability()).isNotNull();
		assertThat(registrations.get("say-hello").getOptions()).hasSize(1);
		assertThat(registrations.get("say-hello").getOptions().get(0).getLongNames()).containsExactly("what");

		assertThat(registrations.get("hi")).isNotNull();
		assertThat(registrations.get("hi").getAvailability()).isNotNull();

		assertThat(registrations.get("alias")).isNotNull();
		assertThat(registrations.get("alias").getAvailability()).isNotNull();
	}

	@ShellComponent
	public static class Sample {

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
	public void testAvailabilityIndicators() {
		applicationContext = new AnnotationConfigApplicationContext(SampleWithAvailability.class);
		SampleWithAvailability sample = applicationContext.getBean(SampleWithAvailability.class);
		registrar.setApplicationContext(applicationContext);
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
		registrar.setApplicationContext(applicationContext);

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
		registrar.setApplicationContext(applicationContext);

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
		registrar.setApplicationContext(applicationContext);

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
		registrar.setApplicationContext(applicationContext);
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
		registrar.setApplicationContext(applicationContext);
	    registrar.register(catalog);

		assertThat(catalog.getRegistrations().get("foo1")).isNotNull();
		assertThat(catalog.getRegistrations().get("foo2")).isNull();
		assertThat(catalog.getRegistrations().get("foo3")).isNotNull();
	}

	@Test
	public void testInteractionModeNonInteractive() {
	    shellContext.setInteractionMode(InteractionMode.NONINTERACTIVE);
		applicationContext = new AnnotationConfigApplicationContext(InteractionModeCommands.class);
		registrar.setApplicationContext(applicationContext);
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
}
