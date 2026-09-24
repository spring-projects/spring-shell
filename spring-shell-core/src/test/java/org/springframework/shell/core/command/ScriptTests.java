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
package org.springframework.shell.core.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.shell.core.InputReader;

/**
 * Tests for {@link Script}.
 *
 * @author Ezequiel Primon
 */
class ScriptTests {

	@TempDir(cleanup = CleanupMode.ALWAYS)
	private File tempDir;

	private CommandRegistry commandRegistry;

	private StringWriter stringWriter;

	private PrintWriter outputWriter;

	private InputReader inputReader = new InputReader() {
	};

	@BeforeEach
	void setUp() {
		this.commandRegistry = new CommandRegistry();
		Command sayHello = Command.builder()
			.name("say-hello")
			.description("Say hello")
			.group("Test")
			.execute(commandContext -> {
				commandContext.outputWriter().println("Hello World");
			});
		this.commandRegistry.registerCommand(sayHello);
		this.stringWriter = new StringWriter();
		this.outputWriter = new PrintWriter(this.stringWriter);
	}

	@Test
	void testScriptFromClasspathResource() throws Exception {
		// when
		executeScript("classpath:scripts/test-script.txt");

		// then
		Assertions.assertEquals("Hello World\nHello World\n", normalize(this.stringWriter.toString()));
	}

	@Test
	void testScriptFromAbsoluteFilePath() throws Exception {
		// given
		File scriptFile = new File(this.tempDir, "script.txt");
		Files.writeString(scriptFile.toPath(), "say-hello\n// This is a comment\nsay-hello\n");

		// when
		executeScript(scriptFile.getAbsolutePath());

		// then
		Assertions.assertEquals("Hello World\nHello World\n", normalize(this.stringWriter.toString()));
	}

	@Test
	void testScriptFromFileUrl() throws Exception {
		// given
		File scriptFile = new File(this.tempDir, "script.txt");
		Files.writeString(scriptFile.toPath(), "say-hello\n// This is a comment\nsay-hello\n");

		// when
		executeScript(scriptFile.toURI().toString());

		// then
		Assertions.assertEquals("Hello World\nHello World\n", normalize(this.stringWriter.toString()));
	}

	@Test
	void testScriptWithMissingFile() {
		// when & then
		Assertions.assertThrows(FileNotFoundException.class, () -> executeScript("does-not-exist.txt"));
	}

	private void executeScript(String scriptRef) throws Exception {
		CommandOption fileOption = CommandOption.with().shortName('f').longName("file").value(scriptRef).build();
		ParsedInput parsedInput = ParsedInput.builder().commandName("script").addOption(fileOption).build();
		CommandContext commandContext = new CommandContext(parsedInput, this.commandRegistry, this.outputWriter,
				this.inputReader);
		Script script = new Script(this.commandRegistry);
		script.execute(commandContext);
		this.outputWriter.flush();
	}

	private String normalize(String output) {
		return output.replaceAll("\\R", "\n");
	}

}
