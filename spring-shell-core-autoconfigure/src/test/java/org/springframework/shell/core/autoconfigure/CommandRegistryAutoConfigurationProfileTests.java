/*
 * Copyright 2026-present the original author or authors.
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
package org.springframework.shell.core.autoconfigure;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CommandRegistryAutoConfiguration} profile filtering support on
 * {@code @Command} methods.
 *
 * @author David Pilar
 */
class CommandRegistryAutoConfigurationProfileTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withUserConfiguration(TestShellApplication.class)
		.withConfiguration(AutoConfigurations.of(SpringShellAutoConfiguration.class));

	@Test
	void commandWithoutProfileShouldAlwaysBeRegistered() {
		this.contextRunner.run(context -> {
			CommandRegistry registry = context.getBean(CommandRegistry.class);
			assertTrue(hasCommand(registry, "always-available"),
					"Command without @Profile should always be registered");
		});
	}

	@Test
	void commandWithProfileShouldNotBeRegisteredWhenProfileIsNotActive() {
		this.contextRunner.run(context -> {
			CommandRegistry registry = context.getBean(CommandRegistry.class);
			assertFalse(hasCommand(registry, "hello"),
					"Command with @Profile('greetings') should not be registered when profile is not active");
		});
	}

	@Test
	void commandWithProfileShouldBeRegisteredWhenProfileIsActive() {
		this.contextRunner.withPropertyValues("spring.profiles.active=greetings").run(context -> {
			CommandRegistry registry = context.getBean(CommandRegistry.class);
			assertTrue(hasCommand(registry, "hello"),
					"Command with @Profile('greetings') should be registered when profile is active");
		});
	}

	@Test
	void commandWithNegatedProfileShouldBeRegisteredWhenProfileIsNotActive() {
		this.contextRunner.run(context -> {
			CommandRegistry registry = context.getBean(CommandRegistry.class);
			assertTrue(hasCommand(registry, "debug-info"),
					"Command with @Profile('!production') should be registered when 'production' is not active");
		});
	}

	@Test
	void commandWithNegatedProfileShouldNotBeRegisteredWhenProfileIsActive() {
		this.contextRunner.withPropertyValues("spring.profiles.active=production").run(context -> {
			CommandRegistry registry = context.getBean(CommandRegistry.class);
			assertFalse(hasCommand(registry, "debug-info"),
					"Command with @Profile('!production') should not be registered when 'production' is active");
		});
	}

	private boolean hasCommand(CommandRegistry registry, String commandName) {
		return registry.getCommands().stream().anyMatch(command -> command.getName().equals(commandName));
	}

	@SpringBootApplication
	static class TestShellApplication {

		@org.springframework.shell.core.command.annotation.Command(name = "always-available",
				description = "Always available command")
		public void alwaysAvailable() {
		}

		@Profile("greetings")
		@org.springframework.shell.core.command.annotation.Command(name = "hello", description = "Say hello")
		public void sayHello() {
		}

		@Profile("!production")
		@org.springframework.shell.core.command.annotation.Command(name = "debug-info",
				description = "Debug information")
		public void debugInfo() {
		}

	}

}
