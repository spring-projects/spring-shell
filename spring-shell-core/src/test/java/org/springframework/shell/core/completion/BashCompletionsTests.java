/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.core.completion;

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistration;

import static org.assertj.core.api.Assertions.assertThat;

class BashCompletionsTests {

	AnnotationConfigApplicationContext context;

	@BeforeEach
	void setup() {
		context = new AnnotationConfigApplicationContext();
		context.refresh();
	}

	@AfterEach
	void clean() {
		if (context != null) {
			context.close();
		}
		context = null;
	}

	@Test
	void testNoCommands() {
		CommandRegistry commandRegistry = CommandRegistry.of();
		BashCompletions completions = new BashCompletions(context, commandRegistry);
		String bash = completions.generate("root-command");
		assertThat(bash).contains("root-command");
	}

	@Test
	void testCommandFromMethod() {
		CommandRegistry commandRegistry = CommandRegistry.of();
		registerFromMethod(commandRegistry);
		BashCompletions completions = new BashCompletions(context, commandRegistry);
		String bash = completions.generate("root-command");
		System.out.println(bash);
		assertThat(bash).contains("root-command")
			.contains("commands+=(\"testmethod1\")")
			.contains("_root-command_testmethod1()")
			.contains("two_word_flags+=(\"--arg1\")");
	}

	@Test
	void testCommandFromFunction() {
		CommandRegistry commandRegistry = CommandRegistry.of();
		registerFromFunction(commandRegistry, "testmethod1");
		BashCompletions completions = new BashCompletions(context, commandRegistry);
		String bash = completions.generate("root-command");
		assertThat(bash).contains("root-command")
			.contains("commands+=(\"testmethod1\")")
			.contains("_root-command_testmethod1()")
			.contains("two_word_flags+=(\"--arg1\")");
	}

	private void registerFromMethod(CommandRegistry commandRegistry) {
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
		commandRegistry.register(registration);
	}

	private void registerFromFunction(CommandRegistry commandRegistry, String command) {
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
		commandRegistry.register(registration);
	}

	protected static class Pojo1 {

		void method1() {
		}

	}

}
