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

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.shell.standard.commands.Completion;

import static org.assertj.core.api.Assertions.assertThat;

public class StandardCommandsAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(StandardCommandsAutoConfiguration.class));

	@Test
	public void testCompletionCommand() {
		this.contextRunner
				.with(disableCommands("help", "clear", "quit", "stacktrace", "script", "history"))
				.run((context) -> {assertThat(context).doesNotHaveBean(Completion.class);
		});
		this.contextRunner
				.with(disableCommands("help", "clear", "quit", "stacktrace", "script", "history", "completion"))
				.withPropertyValues("spring.shell.command.completion.root-command=fake")
				.run((context) -> {assertThat(context).doesNotHaveBean(Completion.class);
		});
		this.contextRunner
				.with(disableCommands("help", "clear", "quit", "stacktrace", "script", "history"))
				.withPropertyValues("spring.shell.command.completion.root-command=fake")
				.run((context) -> {assertThat(context).hasSingleBean(Completion.class);
		});
	}

	private static Function<ApplicationContextRunner, ApplicationContextRunner> disableCommands(String... commands) {
		return (cr) -> {
			for (String command : commands) {
				cr = cr.withPropertyValues(String.format("spring.shell.command.%s.enabled=false", command));
			}
			return cr;
		};
	}
}
