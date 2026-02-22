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
package org.springframework.shell.core.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParsedInputTests {

	@Test
	void builderShouldBuildWithDefaults() {
		// when
		ParsedInput parsedInput = ParsedInput.builder().build();

		// then
		assertEquals("", parsedInput.commandName());
		assertTrue(parsedInput.subCommands().isEmpty());
		assertTrue(parsedInput.options().isEmpty());
		assertTrue(parsedInput.arguments().isEmpty());
	}

	@Test
	void builderShouldBuildWithCommandName() {
		// when
		ParsedInput parsedInput = ParsedInput.builder().commandName("test").build();

		// then
		assertEquals("test", parsedInput.commandName());
	}

	@Test
	void builderShouldBuildWithSubCommands() {
		// when
		ParsedInput parsedInput = ParsedInput.builder()
			.commandName("git")
			.addSubCommand("commit")
			.addSubCommand("--amend")
			.build();

		// then
		assertEquals("git", parsedInput.commandName());
		assertEquals(2, parsedInput.subCommands().size());
		assertEquals("commit", parsedInput.subCommands().get(0));
		assertEquals("--amend", parsedInput.subCommands().get(1));
	}

	@Test
	void builderShouldBuildWithOptions() {
		// given
		CommandOption option = CommandOption.with().longName("verbose").shortName('v').build();

		// when
		ParsedInput parsedInput = ParsedInput.builder().commandName("cmd").addOption(option).build();

		// then
		assertEquals(1, parsedInput.options().size());
		assertEquals("verbose", parsedInput.options().get(0).longName());
	}

	@Test
	void builderShouldBuildWithArguments() {
		// given
		CommandArgument arg = new CommandArgument(0, "file.txt");

		// when
		ParsedInput parsedInput = ParsedInput.builder().commandName("cmd").addArgument(arg).build();

		// then
		assertEquals(1, parsedInput.arguments().size());
		assertEquals("file.txt", parsedInput.arguments().get(0).value());
		assertEquals(0, parsedInput.arguments().get(0).index());
	}

	@Test
	void builtListsShouldBeImmutable() {
		// when
		ParsedInput parsedInput = ParsedInput.builder()
			.commandName("test")
			.addSubCommand("sub")
			.addOption(CommandOption.with().longName("opt").build())
			.addArgument(new CommandArgument(0, "arg"))
			.build();

		// then - lists should be immutable copies
		org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
				() -> parsedInput.subCommands().add("new"));
		org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
				() -> parsedInput.options().add(CommandOption.with().build()));
		org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
				() -> parsedInput.arguments().add(new CommandArgument(1, "x")));
	}

}
