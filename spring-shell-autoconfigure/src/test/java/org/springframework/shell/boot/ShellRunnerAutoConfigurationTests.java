/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.boot;

import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.shell.Shell;
import org.springframework.shell.command.execution.CommandExecution.CommandExecutionHandlerMethodArgumentResolvers;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.context.ShellContext;
import org.springframework.shell.jline.InteractiveShellRunner;
import org.springframework.shell.jline.NonInteractiveShellRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.jline.ScriptShellRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ShellRunnerAutoConfiguration}.
 */
class ShellRunnerAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(ShellRunnerAutoConfiguration.class))
			.withBean(Shell.class, () -> mock(Shell.class))
			.withBean(PromptProvider.class, () -> mock(PromptProvider.class))
			.withBean(LineReader.class, () -> mock(LineReader.class))
			.withBean(Parser.class, () -> mock(Parser.class))
			.withBean(ShellContext.class, () -> mock(ShellContext.class))
			.withBean(CompletionResolver.class, () -> mock(CompletionResolver.class))
			.withBean(CommandExecutionHandlerMethodArgumentResolvers.class, () -> mock(CommandExecutionHandlerMethodArgumentResolvers.class));

	@Nested
	class Interactive {
		@Test
		void enabledByDefault() {
			contextRunner.run(context -> assertThat(context).hasSingleBean(InteractiveShellRunner.class));
		}

		@Test
		void disabledWhenPropertySet() {
			contextRunner.withPropertyValues("spring.shell.interactive.enabled:false")
					.run(context -> assertThat(context).doesNotHaveBean(InteractiveShellRunner.class));
		}
	}

	@Nested
	class NonInteractive {
		@Test
		void enabledByDefault() {
			contextRunner.run(context -> assertThat(context).hasSingleBean(NonInteractiveShellRunner.class));
		}

		@Test
		void disabledWhenPropertySet() {
			contextRunner.withPropertyValues("spring.shell.noninteractive.enabled:false")
					.run(context -> assertThat(context).doesNotHaveBean(NonInteractiveShellRunner.class));
		}

		@Test
		void canBeCustomized() {
			NonInteractiveShellRunnerCustomizer customizer = mock(NonInteractiveShellRunnerCustomizer.class);
			contextRunner.withBean(NonInteractiveShellRunnerCustomizer.class, () -> customizer)
					.run(context -> {
						NonInteractiveShellRunner runner = context.getBean(NonInteractiveShellRunner.class);
						verify(customizer).customize(runner);
					});
		}
	}

	@Nested
	class Script {
		@Test
		void enabledByDefault() {
			contextRunner.run(context -> assertThat(context).hasSingleBean(ScriptShellRunner.class));
		}

		@Test
		void disabledWhenPropertySet() {
			contextRunner.withPropertyValues("spring.shell.script.enabled:false")
					.run(context -> assertThat(context).doesNotHaveBean(ScriptShellRunner.class));
		}
	}
}
