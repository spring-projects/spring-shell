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
package org.springframework.shell.standard.completion;

import org.junit.jupiter.api.Test;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.completion.AbstractCompletions.CommandModel;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractCompletionsTests {

	@Test
	public void testBasicModelGeneration() {
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		CommandCatalog commandCatalog = CommandCatalog.of();

		TestCommands commands = new TestCommands();

		CommandRegistration registration1 = CommandRegistration.builder()
			.command("test1")
			.withTarget()
				.method(commands, "test1")
				.and()
			.withOption()
				.longNames("param1")
				.and()
			.build();

		CommandRegistration registration2 = CommandRegistration.builder()
			.command("test2")
			.withTarget()
				.method(commands, "test2")
				.and()
			.build();

		CommandRegistration registration3 = CommandRegistration.builder()
			.command("test3")
			.withTarget()
				.method(commands, "test3")
				.and()
			.build();

		CommandRegistration registration4 = CommandRegistration.builder()
			.command("test3", "test4")
			.withTarget()
				.method(commands, "test4")
				.and()
			.withOption()
				.longNames("param4")
				.and()
			.build();

		commandCatalog.register(registration1);
		commandCatalog.register(registration2);
		commandCatalog.register(registration3);
		commandCatalog.register(registration4);

		TestCompletions completions = new TestCompletions(resourceLoader, commandCatalog);
		CommandModel commandModel = completions.testCommandModel();
		assertThat(commandModel.getCommands()).hasSize(3);
		assertThat(commandModel.getCommands().stream().map(c -> c.getMainCommand())).containsExactlyInAnyOrder("test1", "test2",
				"test3");
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test1")).findFirst().get()
				.getOptions()).hasSize(1);
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test1")).findFirst().get()
				.getOptions().get(0).option()).isEqualTo("--param1");
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test2")).findFirst().get()
				.getOptions()).hasSize(0);
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test3")).findFirst().get()
				.getOptions()).hasSize(0);
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test3")).findFirst().get()
				.getCommands()).hasSize(1);
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test3")).findFirst().get()
				.getCommands().get(0).getMainCommand()).isEqualTo("test4");
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test3")).findFirst().get()
				.getCommands().get(0).getOptions()).hasSize(1);
		assertThat(commandModel.getCommands().stream().filter(c -> c.getMainCommand().equals("test3")).findFirst().get()
				.getCommands().get(0).getOptions().get(0).option()).isEqualTo("--param4");
	}

	@Test
	public void testBuilder() {
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		CommandCatalog commandCatalog = CommandCatalog.of();
		TestCompletions completions = new TestCompletions(resourceLoader, commandCatalog);

		String result = completions.testBuilder()
				.attribute("x", "command")
				.group("classpath:completion/test.stg")
				.appendGroup("a")
				.build();
		assertThat(result).contains("foocommand");
	}

	private static class TestCompletions extends AbstractCompletions {

		public TestCompletions(ResourceLoader resourceLoader, CommandCatalog commandCatalog) {
			super(resourceLoader, commandCatalog);
		}

		CommandModel testCommandModel() {
			return generateCommandModel();
		}

		Builder testBuilder() {
			return super.builder();
		}
	}

	private static class TestCommands {

		@ShellMethod
		void test1(@ShellOption String param1) {
		}

		@ShellMethod
		void test2() {
		}

		@ShellMethod
		void test3() {
		}

		@ShellMethod
		void test4(@ShellOption String param4) {
		}
	}
}
