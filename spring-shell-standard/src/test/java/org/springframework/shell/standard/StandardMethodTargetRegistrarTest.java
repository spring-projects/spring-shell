/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.standard;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.Availability;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.util.ReflectionUtils;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link StandardMethodTargetRegistrar}.
 *
 * @author Eric Bottard
 */
public class StandardMethodTargetRegistrarTest {

    private StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
    private ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testRegistrations() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Sample.class);
        registrar.setApplicationContext(applicationContext);
        registrar.register(registry);

        MethodTarget methodTarget = registry.listCommands().get("say-hello");
        assertThat(methodTarget, notNullValue());
        assertThat(methodTarget.getHelp(), is("some command"));
        assertThat(methodTarget.getMethod(), is(ReflectionUtils.findMethod(Sample.class, "sayHello", String.class)));
        assertThat(methodTarget.getAvailability().isAvailable(), is(true));

        methodTarget = registry.listCommands().get("hi");
        assertThat(methodTarget, notNullValue());
        assertThat(methodTarget.getHelp(), is("method with alias"));
        assertThat(methodTarget.getMethod(), is(ReflectionUtils.findMethod(Sample.class, "greet", String.class)));
        assertThat(methodTarget.getAvailability().isAvailable(), is(true));
        methodTarget = registry.listCommands().get("alias");
        assertThat(methodTarget, notNullValue());
        assertThat(methodTarget.getHelp(), is("method with alias"));
        assertThat(methodTarget.getMethod(), is(ReflectionUtils.findMethod(Sample.class, "greet", String.class)));
        assertThat(methodTarget.getAvailability().isAvailable(), is(true));
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
        assertThat(methodTarget.getMethod(), is(ReflectionUtils.findMethod(SampleWithAvailability.class, "sayHello")));
        assertThat(methodTarget.getAvailability().isAvailable(), is(true));
        sample.available = false;
        assertThat(methodTarget.getAvailability().isAvailable(), is(false));
        assertThat(methodTarget.getAvailability().getReason(), is("sayHelloAvailability"));
        sample.available = true;

        methodTarget = registry.listCommands().get("hi");
        assertThat(methodTarget.getMethod(), is(ReflectionUtils.findMethod(SampleWithAvailability.class, "hi")));
        assertThat(methodTarget.getAvailability().isAvailable(), is(true));
        sample.available = false;
        assertThat(methodTarget.getAvailability().isAvailable(), is(false));
        assertThat(methodTarget.getAvailability().getReason(), is("customAvailabilityMethod"));
        sample.available = true;

        methodTarget = registry.listCommands().get("bonjour");
        assertThat(methodTarget.getMethod(), is(ReflectionUtils.findMethod(SampleWithAvailability.class, "bonjour")));
        assertThat(methodTarget.getAvailability().isAvailable(), is(true));
        sample.available = false;
        assertThat(methodTarget.getAvailability().isAvailable(), is(false));
        assertThat(methodTarget.getAvailability().getReason(), is("availabilityForSeveralCommands"));
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

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When set on a @ShellMethod method, the value of the @ShellMethodAvailability should be a single element");
        thrown.expectMessage("Found [one, two]");
        thrown.expectMessage("wrong()");

        registrar.register(registry);
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

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When using '*' as a wildcard for ShellMethodAvailability, this can be the only value. Found [one, *]");
        thrown.expectMessage("availability()");

        registrar.register(registry);
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

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Found several @ShellMethodAvailability");
        thrown.expectMessage("wrong()");
        thrown.expectMessage("availability()");
        thrown.expectMessage("otherAvailability()");

        registrar.register(registry);
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

}