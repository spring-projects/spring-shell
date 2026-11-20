/*
 * Copyright 2026-present the original author or authors.
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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandExecutionException;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;
import org.springframework.shell.core.command.ExitStatus;

/**
 * Tests for {@link NonInteractiveShellRunner}.
 *
 * @author David Pilar
 */
class NonInteractiveShellRunnerTests {

	@TempDir(cleanup = CleanupMode.ALWAYS)
	private File tempDir;

	private StringWriter stringWriter;

	private NonInteractiveShellRunner shellRunner;

	@BeforeEach
	void setUp() {
		CommandRegistry commandRegistry = new CommandRegistry();
		Command sayHello = Command.builder()
			.name("say-hello")
			.description("Say hello")
			.group("Test")
			.execute(commandContext -> {
				commandContext.outputWriter().println("Hello World");
			});
		commandRegistry.registerCommand(sayHello);
		this.stringWriter = new StringWriter();
		this.shellRunner = new NonInteractiveShellRunner(new DefaultCommandParser(commandRegistry), commandRegistry,
				new PrintWriter(this.stringWriter, true));
	}

	@Test
	void testCommand() throws Exception {
		// when
		this.shellRunner.run(new String[] { "say-hello" });

		// then
		Assertions.assertEquals("Hello World\n", normalize(this.stringWriter.toString()));
	}

	@Test
	void testCommandWithParsingError() {
		// when
		CommandExecutionException exception = Assertions.assertThrows(CommandExecutionException.class,
				() -> this.shellRunner.run(new String[] { "say-hello", "\"unbalanced" }));

		// then
		Assertions.assertEquals(ExitStatus.USAGE_ERROR.code(), exception.getExitCode());
		Assertions.assertEquals("", this.stringWriter.toString());
	}

	@Test
	void testScriptWithParsingErrorSkipsNextCommands() throws Exception {
		// given
		File scriptFile = new File(this.tempDir, "script.txt");
		Files.writeString(scriptFile.toPath(), "say-hello\nsay-hello \"unbalanced\nsay-hello\n");

		// when
		CommandExecutionException exception = Assertions.assertThrows(CommandExecutionException.class,
				() -> this.shellRunner.run(new String[] { "@" + scriptFile.getAbsolutePath() }));

		// then
		Assertions.assertEquals(ExitStatus.USAGE_ERROR.code(), exception.getExitCode());
		Assertions.assertEquals("Hello World\n", normalize(this.stringWriter.toString()));
	}

	@Test
	void testScriptWithMissingFile() {
		// given
		File scriptFile = new File(this.tempDir, "does-not-exist.txt");

		// when & then
		Assertions.assertThrows(CommandExecutionException.class,
				() -> this.shellRunner.run(new String[] { "@" + scriptFile.getAbsolutePath() }));
	}

	private String normalize(String output) {
		return output.replaceAll("\\R", "\n");
	}

}
