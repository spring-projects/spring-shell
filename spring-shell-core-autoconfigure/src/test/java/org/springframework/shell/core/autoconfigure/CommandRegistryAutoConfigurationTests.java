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
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CommandRegistryAutoConfiguration} command discovery.
 *
 * @author David Pilar
 */
class CommandRegistryAutoConfigurationTests {

	/**
	 * Regression test for
	 * <a href= "https://github.com/spring-projects/spring-shell/issues/1351">gh-1351</a>:
	 * registering annotated commands must not eagerly instantiate beans whose scope (e.g.
	 * {@code request}) is not available in a non-web shell application.
	 */
	@Test
	void requestScopedBeanDoesNotPreventCommandRegistration() {
		new ApplicationContextRunner().withUserConfiguration(TestConfiguration.class, RequestScopedBean.class)
			.withConfiguration(AutoConfigurations.of(SpringShellAutoConfiguration.class))
			.run(context -> {
				assertNull(context.getStartupFailure(), "Context should start without errors");
				CommandRegistry registry = context.getBean(CommandRegistry.class);
				assertTrue(registry.getCommands().stream().anyMatch(c -> c.getName().equals("greet")),
						"Annotated command should be registered");
			});
	}

	@Configuration
	static class TestConfiguration {

		@Command(name = "greet", description = "Say hello")
		public void sayHello() {
		}

	}

	@Component
	@Scope("request")
	static class RequestScopedBean {

	}

}
