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

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;

import static org.assertj.core.api.Assertions.assertThat;

public class BashCompletionsTests {

	AnnotationConfigApplicationContext context;

	@BeforeEach
	public void setup() {
		context = new AnnotationConfigApplicationContext();
		context.refresh();
	}

	@AfterEach
	public void clean() {
		if (context != null) {
			context.close();
		}
		context = null;
	}

	@Test
	public void testNoCommands() {
		CommandCatalog commandCatalog = CommandCatalog.of();
		BashCompletions completions = new BashCompletions(context, commandCatalog);
		String bash = completions.generate("root-command");
		assertThat(bash).contains("root-command");
	}

	@Test
	public void testCommandFromMethod() {
		CommandCatalog commandCatalog = CommandCatalog.of();
		registerFromMethod(commandCatalog);
		BashCompletions completions = new BashCompletions(context, commandCatalog);
		String bash = completions.generate("root-command");
		System.out.println(bash);
		assertThat(bash).contains("root-command");
		assertThat(bash).contains("commands+=(\"testmethod1\")");
		assertThat(bash).contains("_root-command_testmethod1()");
		assertThat(bash).contains("two_word_flags+=(\"--arg1\")");
	}

	@Test
	public void testCommandFromFunction() {
		CommandCatalog commandCatalog = CommandCatalog.of();
		registerFromFunction(commandCatalog, "testmethod1");
		BashCompletions completions = new BashCompletions(context, commandCatalog);
		String bash = completions.generate("root-command");
		assertThat(bash).contains("root-command");
		assertThat(bash).contains("commands+=(\"testmethod1\")");
		assertThat(bash).contains("_root-command_testmethod1()");
		assertThat(bash).contains("two_word_flags+=(\"--arg1\")");
	}

	private void registerFromMethod(CommandCatalog commandCatalog) {
		Pojo1 pojo1 = new Pojo1();
		CommandRegistration registration = CommandRegistration.builder()
			.command("testmethod1")
			.withTarget()
				.method(pojo1, "method1")
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.build();
		commandCatalog.register(registration);
	}

	private void registerFromFunction(CommandCatalog commandCatalog, String command) {
		Function<CommandContext, String> function = ctx -> {
			String arg1 = ctx.getOptionValue("arg1");
			return String.format("hi, arg1 value is '%s'", arg1);
		};
		CommandRegistration registration = CommandRegistration.builder()
			.command(command)
			.withTarget()
				.function(function)
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.build();
		commandCatalog.register(registration);
	}

	protected static class Pojo1 {

		void method1() {}
	}
}
