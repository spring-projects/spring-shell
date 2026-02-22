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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.shell.core.InputReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandExecutorTests {

	private CommandRegistry commandRegistry;

	private CommandExecutor commandExecutor;

	@BeforeEach
	void setUp() {
		this.commandRegistry = new CommandRegistry();
		this.commandExecutor = new CommandExecutor(this.commandRegistry);
	}

	@Test
	void executeCommandSuccessfully() {
		// given
		commandRegistry.registerCommand(new AbstractCommand("greet", "A greeting command") {
			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				commandContext.outputWriter().println("Hello!");
				return ExitStatus.OK;
			}
		});

		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("greet").build();
		CommandContext context = new CommandContext(parsedInput, commandRegistry, new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when
		ExitStatus exitStatus = commandExecutor.execute(context);

		// then
		assertEquals(ExitStatus.OK.code(), exitStatus.code());
	}

	@Test
	void executeCommandNotFoundShouldThrow() {
		// given
		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("unknown").build();
		CommandContext context = new CommandContext(parsedInput, commandRegistry, new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when / then
		assertThrows(CommandNotFoundException.class, () -> commandExecutor.execute(context));
	}

	@Test
	void executeCommandWithSubCommands() {
		// given
		commandRegistry.registerCommand(new AbstractCommand("git commit", "Commit changes") {
			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				commandContext.outputWriter().println("committed");
				return ExitStatus.OK;
			}
		});

		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("git").addSubCommand("commit").build();
		CommandContext context = new CommandContext(parsedInput, commandRegistry, new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when
		ExitStatus exitStatus = commandExecutor.execute(context);

		// then
		assertEquals(ExitStatus.OK.code(), exitStatus.code());
	}

	@Test
	void executeCommandWithExceptionShouldWrapInCommandExecutionException() {
		// given
		commandRegistry.registerCommand(new AbstractCommand("fail", "A failing command") {
			@Override
			public ExitStatus doExecute(CommandContext commandContext) throws Exception {
				throw new RuntimeException("boom");
			}
		});

		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("fail").build();
		CommandContext context = new CommandContext(parsedInput, commandRegistry, new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when / then
		CommandExecutionException exception = assertThrows(CommandExecutionException.class,
				() -> commandExecutor.execute(context));
		assertTrue(exception.getMessage().contains("fail"));
	}

	@Test
	void executeWithPrefixMatchShouldListSubCommands() {
		// given
		commandRegistry.registerCommand(new AbstractCommand("git commit", "Commit changes") {
			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		});
		commandRegistry.registerCommand(new AbstractCommand("git push", "Push changes") {
			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		});

		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("git").build();
		CommandContext context = new CommandContext(parsedInput, commandRegistry, new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when
		ExitStatus exitStatus = commandExecutor.execute(context);

		// then
		assertEquals(ExitStatus.OK.code(), exitStatus.code());
		String output = stringWriter.toString();
		assertTrue(output.contains("Available sub-commands"));
		assertTrue(output.contains("git commit"));
		assertTrue(output.contains("git push"));
	}

}
