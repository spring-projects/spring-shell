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
package org.springframework.shell.jline;

import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

class JLineShellRunnerTests {

	private PipedInputStream pipedInputStream;

	private PipedOutputStream pipedOutputStream;

	private Terminal terminal;

	private JLineInputProvider inputProvider;

	private JLineShellRunner runner;

	@BeforeEach
	void setup() throws Exception {
		this.pipedInputStream = new PipedInputStream();
		this.pipedOutputStream = new PipedOutputStream();
		this.pipedInputStream.connect(this.pipedOutputStream);
		ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();
		this.terminal = new DumbTerminal("terminal", "ansi", this.pipedInputStream, consoleOut, StandardCharsets.UTF_8);
		LineReader lineReader = LineReaderBuilder.builder().terminal(this.terminal).build();
		this.inputProvider = new JLineInputProvider(lineReader);
		CommandRegistry commandRegistry = new CommandRegistry();
		this.runner = new JLineShellRunner(this.inputProvider, new DefaultCommandParser(commandRegistry),
				commandRegistry);
	}

	@AfterEach
	void cleanup() throws Exception {
		this.pipedOutputStream.close();
		this.pipedInputStream.close();
	}

	@Test
	void runShouldExitGracefullyWhenTerminalIsClosed() throws Exception {
		this.terminal.close();
		assertDoesNotThrow(() -> this.runner.run(new String[0]));
	}

	@Test
	void readInputShouldReturnNullWhenTerminalIsClosed() throws Exception {
		this.terminal.close();
		assertNull(this.inputProvider.readInput());
	}

	@Test
	void printShouldNotThrowWhenTerminalIsClosed() throws Exception {
		this.terminal.close();
		assertDoesNotThrow(() -> this.runner.print("anything"));
	}

	@Test
	void flushShouldNotThrowWhenTerminalIsClosed() throws Exception {
		this.terminal.close();
		assertDoesNotThrow(() -> this.runner.flush());
	}

}
