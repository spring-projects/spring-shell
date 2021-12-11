/*
 * Copyright 2017 the original author or authors.
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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.Availability;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.test1.GroupOneCommands;
import org.springframework.shell.standard.test2.GroupThreeCommands;
import org.springframework.shell.standard.test2.GroupTwoCommands;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link StandardMethodTargetRegistrar}.
 *
 * @author Eric Bottard
 */
public class StandardMethodTargetRegistrarTest {

    private StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
    private ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();

    @Test
    public void testRegistrations() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Sample.class);
        registrar.setApplicationContext(applicationContext);
        registrar.register(registry);

        MethodTarget methodTarget = registry.listCommands().get("say-hello");
        assertThat(methodTarget).isNotNull();
        assertThat(methodTarget.getHelp()).isEqualTo("some command");
        assertThat(methodTarget.getMethod()).isEqualTo(ReflectionUtils.findMethod(Sample.class, "sayHello", String.class));
        assertThat(methodTarget.getAvailability().isAvailable()).isTrue();

        methodTarget = registry.listCommands().get("hi");
        assertThat(methodTarget).isNotNull();
        assertThat(methodTarget.getHelp()).isEqualTo("method with alias");
        assertThat(methodTarget.getMethod()).isEqualTo(ReflectionUtils.findMethod(Sample.class, "greet", String.class));
        assertThat(methodTarget.getAvailability().isAvailable()).isTrue();
        methodTarget = registry.listCommands().get("alias");
        assertThat(methodTarget).isNotNull();
        assertThat(methodTarget.getHelp()).isEqualTo("method with alias");
        assertThat(methodTarget.getMethod()).isEqualTo(ReflectionUtils.findMethod(Sample.class, "greet", String.class));
        assertThat(methodTarget.getAvailability().isAvailable()).isTrue();
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
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SampleWithAvailability.class);
        registrar.setApplicationContext(applicationContext);
        registrar.register(registry);
        SampleWithAvailability sample = applicationContext.getBean(SampleWithAvailability.class);

        MethodTarget methodTarget = registry.listCommands().get("say-hello");
        assertThat(methodTarget.getMethod()).isEqualTo(ReflectionUtils.findMethod(SampleWithAvailability.class, "sayHello"));
        assertThat(methodTarget.getAvailability().isAvailable()).isTrue();
        sample.available = false;
        assertThat(methodTarget.getAvailability().isAvailable()).isFalse();
        assertThat(methodTarget.getAvailability().getReason()).isEqualTo("sayHelloAvailability");
        sample.available = true;

        methodTarget = registry.listCommands().get("hi");
        assertThat(methodTarget.getMethod()).isEqualTo(ReflectionUtils.findMethod(SampleWithAvailability.class, "hi"));
        assertThat(methodTarget.getAvailability().isAvailable()).isTrue();
        sample.available = false;
        assertThat(methodTarget.getAvailability().isAvailable()).isFalse();
        assertThat(methodTarget.getAvailability().getReason()).isEqualTo("customAvailabilityMethod");
        sample.available = true;

        methodTarget = registry.listCommands().get("bonjour");
        assertThat(methodTarget.getMethod()).isEqualTo(ReflectionUtils.findMethod(SampleWithAvailability.class, "bonjour"));
        assertThat(methodTarget.getAvailability().isAvailable()).isTrue();
        sample.available = false;
        assertThat(methodTarget.getAvailability().isAvailable()).isFalse();
        assertThat(methodTarget.getAvailability().getReason()).isEqualTo("availabilityForSeveralCommands");
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
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(WrongAvailabilityIndicatorOnShellMethod.class);
        registrar.setApplicationContext(applicationContext);

		assertThatThrownBy(() -> {
            registrar.register(registry);
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
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(WrongAvailabilityIndicatorWildcardNotAlone.class);
        registrar.setApplicationContext(applicationContext);

		assertThatThrownBy(() -> {
            registrar.register(registry);
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
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(WrongAvailabilityIndicatorAmbiguous.class);
        registrar.setApplicationContext(applicationContext);

		assertThatThrownBy(() -> {
            registrar.register(registry);
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
        ApplicationContext context = new AnnotationConfigApplicationContext(GroupOneCommands.class,
                GroupTwoCommands.class, GroupThreeCommands.class);
        registrar.setApplicationContext(context);
        registrar.register(registry);

        Map<String, MethodTarget> commands = registry.listCommands();
        Assertions.assertThat(commands.get("explicit1").getGroup()).isEqualTo("Explicit Group Method Level 1");
        Assertions.assertThat(commands.get("explicit2").getGroup()).isEqualTo("Explicit Group Method Level 2");
        Assertions.assertThat(commands.get("explicit3").getGroup()).isEqualTo("Explicit Group Method Level 3");
        Assertions.assertThat(commands.get("implicit1").getGroup()).isEqualTo("Implicit Group Package Level 1");
        Assertions.assertThat(commands.get("implicit2").getGroup()).isEqualTo("Group Two Commands");
        Assertions.assertThat(commands.get("implicit3").getGroup()).isEqualTo("Explicit Group 3 Class Level");
    }

}
