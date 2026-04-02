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

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ShellRunnerAutoConfiguration}.
 *
 * @author David Pilar
 */
class ShellRunnerAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(SpringShellAutoConfiguration.class))
		.withPropertyValues("spring.shell.interactive.enabled=false");

	@Test
	void springShellApplicationRunnerShouldBeRegistered() {
		this.contextRunner.withUserConfiguration(TestShellApplication.class).run(context -> {
			ApplicationRunner shellRunner = context.getBean("springShellApplicationRunner", ApplicationRunner.class);
			assertNotNull(shellRunner, "springShellApplicationRunner should be registered");
		});
	}

	@Test
	void springShellApplicationRunnerShouldBeRegisteredAlongsideUserApplicationRunner() {
		this.contextRunner.withUserConfiguration(TestShellApplicationWithRunner.class).run(context -> {
			ApplicationRunner shellRunner = context.getBean("springShellApplicationRunner", ApplicationRunner.class);
			assertNotNull(shellRunner,
					"springShellApplicationRunner should be registered even when another ApplicationRunner exists");

			assertTrue(context.containsBean("demoRunner"), "User-defined ApplicationRunner should also be registered");
		});
	}

	@SpringBootApplication
	static class TestShellApplication {

		@Command
		public void hi() {
			System.out.println("Hello!");
		}

	}

	@SpringBootApplication
	static class TestShellApplicationWithRunner {

		@Command
		public void hi() {
			System.out.println("Hello!");
		}

		@Component("demoRunner")
		static class DemoRunner implements ApplicationRunner {

			@Override
			public void run(ApplicationArguments args) {
				System.out.println("DemoRunner executed!");
			}

		}

	}

}
