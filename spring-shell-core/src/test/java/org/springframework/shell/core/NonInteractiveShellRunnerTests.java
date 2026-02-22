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
package org.springframework.shell.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;
import org.springframework.shell.core.command.ExitStatus;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NonInteractiveShellRunnerTests {

	@TempDir
	Path tempDir;

	private CommandRegistry commandRegistry;

	private DefaultCommandParser commandParser;

	private StringWriter outputBuffer;

	private NonInteractiveShellRunner runner;

	@BeforeEach
	void setUp() {
		commandRegistry = new CommandRegistry();
		commandParser = new DefaultCommandParser(commandRegistry);
		outputBuffer = new StringWriter();

		commandRegistry.registerCommand(new AbstractCommand("greet", "A greeting command") {
			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				commandContext.outputWriter().println("Hello!");
				commandContext.outputWriter().flush();
				return ExitStatus.OK;
			}
		});

		runner = new NonInteractiveShellRunner(commandParser, commandRegistry, new PrintWriter(outputBuffer));
	}

	@Test
	void shouldExecuteSingleCommand() throws Exception {
		// when
		runner.run(new String[] { "greet" });

		// then
		assertTrue(outputBuffer.toString().contains("Hello!"));
	}

	@Test
	void shouldHandleEmptyArgs() throws Exception {
		// when - should not throw
		runner.run(new String[] {});

		// then - output should be empty (error is logged)
		assertTrue(outputBuffer.toString().isEmpty());
	}

	@Test
	void shouldExecuteScriptFile() throws Exception {
		// given
		Path scriptFile = tempDir.resolve("commands.txt");
		Files.writeString(scriptFile, "greet\n");

		// when
		runner.run(new String[] { "@" + scriptFile.toAbsolutePath() });

		// then
		assertTrue(outputBuffer.toString().contains("Hello!"));
	}

	@Test
	void shouldThrowForUnknownCommand() {
		// when / then - CommandNotFoundException propagates from CommandExecutor
		org.junit.jupiter.api.Assertions.assertThrows(
				org.springframework.shell.core.command.CommandNotFoundException.class,
				() -> runner.run(new String[] { "unknown" }));
	}

}
