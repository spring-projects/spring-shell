/*
 * Copyright 2025-present the original author or authors.
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

import java.util.concurrent.atomic.AtomicReference;

import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.shell.core.command.annotation.Command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author David Pilar
 */
class JLineShellAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withUserConfiguration(SpringShellApplication.class)
		.withConfiguration(AutoConfigurations.of(SpringShellAutoConfiguration.class))
		.withPropertyValues("spring.shell.interactive.enabled=false");

	@Test
	void terminalIsReusedAcrossContextsAndSurvivesContextClose() {
		AtomicReference<Terminal> firstTerminal = new AtomicReference<>();
		this.contextRunner.run(context -> firstTerminal.set(context.getBean(Terminal.class)));

		// First context is closed; the shared terminal must survive so a restart reuses
		// it.
		this.contextRunner.run(context -> {
			Terminal terminal = context.getBean(Terminal.class);
			assertSame(firstTerminal.get(), terminal);
			// getAttributes() throws if the terminal has been closed
			assertDoesNotThrow(terminal::getAttributes);
		});
	}

	@SpringBootApplication
	static class SpringShellApplication {

		@Command
		void hi() {
			System.out.println("Hello world!");
		}

	}

}
