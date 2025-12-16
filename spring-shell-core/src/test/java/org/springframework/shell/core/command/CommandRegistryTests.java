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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandRegistryTests {

	private final CommandRegistry commandRegistry = new CommandRegistry();

	@Test
	void getCommandByName() {
		// given
		commandRegistry.registerCommand(new Command() {

			@Override
			public String getName() {
				return "cmd1";
			}

			@Override
			public ExitStatus execute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		});
		commandRegistry.registerCommand(new Command() {

			@Override
			public String getName() {
				return "cmd2";
			}

			@Override
			public boolean isHidden() {
				return true;
			}

			@Override
			public ExitStatus execute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		});

		// when
		Command cmd1 = commandRegistry.getCommandByName("cmd1");
		Command cmd2 = commandRegistry.getCommandByName("cmd2");
		Command cmd3 = commandRegistry.getCommandByName("cmd3");

		// then
		assertNotNull(cmd1);
		assertNull(cmd2); // hidden command should not be returned
		assertNull(cmd3); // non-existing command should return null
	}

	@Test
	void getCommandsByPrefix() {
		// given
		commandRegistry.registerCommand(new Command() {

			@Override
			public String getName() {
				return "start";
			}

			@Override
			public ExitStatus execute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		});
		commandRegistry.registerCommand(new Command() {

			@Override
			public String getName() {
				return "status";
			}

			@Override
			public ExitStatus execute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		});
		commandRegistry.registerCommand(new Command() {

			@Override
			public String getName() {
				return "stop";
			}

			@Override
			public boolean isHidden() {
				return true;
			}

			@Override
			public ExitStatus execute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		});

		// when
		var commands = commandRegistry.getCommandsByPrefix("st");

		// then
		assertEquals(2, commands.size());
		assertTrue(commands.stream().anyMatch(cmd -> cmd.getName().equals("start")));
		assertTrue(commands.stream().anyMatch(cmd -> cmd.getName().equals("status")));
	}

}