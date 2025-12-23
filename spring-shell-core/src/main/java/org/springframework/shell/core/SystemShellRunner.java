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

import java.io.Console;
import java.io.PrintWriter;

import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;

/**
 * Interactive shell runner based on the JVM's system {@link Console}.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class SystemShellRunner extends InteractiveShellRunner {

	private final Console console;

	/**
	 * Create a new {@link SystemShellRunner} instance.
	 * @param inputProvider the console input provider
	 * @param commandParser the command parser
	 * @param commandRegistry the command registry
	 */
	public SystemShellRunner(ConsoleInputProvider inputProvider, CommandParser commandParser,
			CommandRegistry commandRegistry) {
		super(inputProvider, commandParser, commandRegistry);
		this.console = inputProvider.getConsole();
	}

	@Override
	public void print(String message) {
		try (PrintWriter outputWriter = this.console.writer()) {
			outputWriter.println(message);
		}
	}

	@Override
	public void flush() {
		this.console.flush();
	}

	@Override
	public PrintWriter getWriter() {
		return this.console.writer();
	}

	@Override
	public InputReader getReader() {
		return new ConsoleInputReader(this.console);
	}

}
