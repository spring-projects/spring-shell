/*
 * Copyright 2022-2023 the original author or authors.
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

	private final TestCommands commands = new TestCommands();

	private final CommandRegistration r1 = CommandRegistration.builder()
		.command("test1")
		.withTarget()
			.method(commands, "test1")
			.and()
		.withOption()
			.longNames("param1")
			.and()
		.build();

	private final CommandRegistration r2 = CommandRegistration.builder()
		.command("test2")
		.withTarget()
			.method(commands, "test2")
			.and()
		.build();

	private final CommandRegistration r3 = CommandRegistration.builder()
		.command("test3")
		.withTarget()
			.method(commands, "test3")
			.and()
		.build();

	private final CommandRegistration r3_4 = CommandRegistration.builder()
		.command("test3", "test4")
		.withTarget()
			.method(commands, "test4")
			.and()
		.withOption()
			.longNames("param4")
			.and()
		.build();

	private final CommandRegistration r3_5 = CommandRegistration.builder()
		.command("test3", "test5")
		.withTarget()
			.method(commands, "test4")
			.and()
		.withOption()
			.longNames("param4")
			.and()
		.build();

	private final CommandRegistration r3_4_5 = CommandRegistration.builder()
		.command("test3", "test4", "test5")
		.withTarget()
			.method(commands, "test4")
			.and()
		.withOption()
			.longNames("param4")
			.and()
		.build();

	private final CommandRegistration r3_4_6 = CommandRegistration.builder()
		.command("test3", "test4", "test6")
		.withTarget()
			.method(commands, "test4")
			.and()
		.withOption()
			.longNames("param4")
			.and()
		.build();

	private final CommandRegistration r3_5_5 = CommandRegistration.builder()
		.command("test3", "test5", "test5")
		.withTarget()
			.method(commands, "test4")
			.and()
		.withOption()
			.longNames("param4")
			.and()
		.build();

	private final CommandRegistration r3_5_6 = CommandRegistration.builder()
		.command("test3", "test5", "test6")
		.withTarget()
			.method(commands, "test4")
			.and()
		.withOption()
			.longNames("param4")
			.and()
		.build();

	@Test
	public void deepL3Commands() {
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		CommandCatalog commandCatalog = CommandCatalog.of();

		commandCatalog.register(r3_4_5);
		commandCatalog.register(r3_4_6);
		commandCatalog.register(r3_5_5);
		commandCatalog.register(r3_5_6);
		TestCompletions completions = new TestCompletions(resourceLoader, commandCatalog);
		CommandModel commandModel = completions.testCommandModel();

		assertThat(commandModel.getCommands()).satisfiesExactlyInAnyOrder(
			c3 -> {
				assertThat(c3.getMainCommand()).isEqualTo("test3");
				assertThat(c3.getOptions()).hasSize(0);
				assertThat(c3.getSubCommands()).hasSize(2);
				assertThat(c3.getCommands()).hasSize(2);
				assertThat(c3.getCommands()).satisfiesExactlyInAnyOrder(
					c34 -> {
						assertThat(c34.getMainCommand()).isEqualTo("test4");
						assertThat(c34.getCommands()).satisfiesExactlyInAnyOrder(
							c345 -> {
								assertThat(c345.getMainCommand()).isEqualTo("test5");
							},
							c346 -> {
								assertThat(c346.getMainCommand()).isEqualTo("test6");
							}
						);
					},
					c35 -> {
						assertThat(c35.getMainCommand()).isEqualTo("test5");
						assertThat(c35.getCommands()).satisfiesExactlyInAnyOrder(
							c355 -> {
								assertThat(c355.getMainCommand()).isEqualTo("test5");
							},
							c356 -> {
								assertThat(c356.getMainCommand()).isEqualTo("test6");
							}
						);
					}
				);
			}
		);
	}

	@Test
	public void deepL2Commands() {
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		CommandCatalog commandCatalog = CommandCatalog.of();

		commandCatalog.register(r3_4);
		commandCatalog.register(r3_5);
		TestCompletions completions = new TestCompletions(resourceLoader, commandCatalog);
		CommandModel commandModel = completions.testCommandModel();

		assertThat(commandModel.getCommands()).satisfiesExactlyInAnyOrder(
			c3 -> {
				assertThat(c3.getMainCommand()).isEqualTo("test3");
				assertThat(c3.getOptions()).hasSize(0);
				assertThat(c3.getSubCommands()).hasSize(2);
				assertThat(c3.getCommands()).hasSize(2);
				assertThat(c3.getCommands()).satisfiesExactlyInAnyOrder(
					c34 -> {
						assertThat(c34.getMainCommand()).isEqualTo("test4");
						assertThat(c34.getOptions()).hasSize(1);
						assertThat(c34.getOptions()).satisfiesExactly(
							o -> {
								assertThat(o.option()).isEqualTo("--param4");
							}
						);
					},
					c35 -> {
						assertThat(c35.getMainCommand()).isEqualTo("test5");
						assertThat(c35.getOptions()).hasSize(1);
						assertThat(c35.getOptions()).satisfiesExactly(
							o -> {
								assertThat(o.option()).isEqualTo("--param4");
							}
						);
					}
				);
			}
		);
	}

	@Test
	public void testBasicModelGeneration() {
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		CommandCatalog commandCatalog = CommandCatalog.of();

		commandCatalog.register(r1);
		commandCatalog.register(r2);
		commandCatalog.register(r3);
		commandCatalog.register(r3_4);

		TestCompletions completions = new TestCompletions(resourceLoader, commandCatalog);
		CommandModel commandModel = completions.testCommandModel();
		assertThat(commandModel.getCommands()).satisfiesExactlyInAnyOrder(
			c1 -> {
				assertThat(c1.getMainCommand()).isEqualTo("test1");
				assertThat(c1.getSubCommands()).hasSize(0);
				assertThat(c1.getOptions()).hasSize(1);
				assertThat(c1.getOptions()).satisfiesExactly(
					o -> {
						assertThat(o.option()).isEqualTo("--param1");
					}
				);
			},
			c2 -> {
				assertThat(c2.getMainCommand()).isEqualTo("test2");
				assertThat(c2.getSubCommands()).hasSize(0);
				assertThat(c2.getOptions()).hasSize(0);
			},
			c3 -> {
				assertThat(c3.getMainCommand()).isEqualTo("test3");
				assertThat(c3.getOptions()).hasSize(0);
				assertThat(c3.getSubCommands()).hasSize(1);
				assertThat(c3.getCommands()).hasSize(1);
				assertThat(c3.getCommands()).satisfiesExactly(
					c34 -> {
						assertThat(c34.getMainCommand()).isEqualTo("test4");
						assertThat(c34.getOptions()).hasSize(1);
						assertThat(c34.getOptions()).satisfiesExactly(
							o -> {
								assertThat(o.option()).isEqualTo("--param4");
							}
						);
					}
				);
			}
		);
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
