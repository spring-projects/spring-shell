/*
 * Copyright 2015-present the original author or authors.
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
package org.springframework.shell.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.utils.Utils;

/**
 * Tests for {@link Utils}.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine
 */
class UtilsTests {

	@Test
	void testFormatAvailableCommandsForEmptyRegistry() {
		CommandRegistry commandRegistry = new CommandRegistry();
		String availableCommands = Utils.formatAvailableCommands(commandRegistry);
		Assertions.assertEquals("AVAILABLE COMMANDS" + System.lineSeparator() + System.lineSeparator(),
				availableCommands);
	}

	@Test
	void testFormatAvailableCommands() {
		CommandRegistry commandRegistry = new CommandRegistry();
		Command helloCommand = new Command.Builder().name("hello")
			.group("greetings")
			.description("Say hello")
			.aliases("hi", "hey")
			.execute(commandContext -> "hello");
		commandRegistry.registerCommand(helloCommand);
		AbstractCommand secretCommand = new Command.Builder().name("secret")
			.group("greetings")
			.description("A hidden command")
			.hidden(true)
			.execute(commandContext -> "secret");
		commandRegistry.registerCommand(secretCommand);
		String availableCommands = Utils.formatAvailableCommands(commandRegistry);
		String expected = "AVAILABLE COMMANDS" + System.lineSeparator() + System.lineSeparator() + "greetings"
				+ System.lineSeparator() + "\thello, hi, hey: Say hello" + System.lineSeparator();
		Assertions.assertEquals(expected, availableCommands);
	}

}
