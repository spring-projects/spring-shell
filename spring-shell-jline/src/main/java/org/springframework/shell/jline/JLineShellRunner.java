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

import java.io.Console;
import java.io.PrintWriter;

import org.jline.terminal.Terminal;

import org.springframework.shell.core.InteractiveShellRunner;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;

/**
 * Interactive shell runner based on the JVM's system {@link Console}.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class JLineShellRunner extends InteractiveShellRunner {

	private final Terminal terminal;

	/**
	 * Create a new {@link JLineShellRunner} instance.
	 * @param inputProvider the JLine input provider
	 * @param commandParser the command parser
	 * @param commandRegistry the command registry
	 */
	public JLineShellRunner(JLineInputProvider inputProvider, CommandParser commandParser,
			CommandRegistry commandRegistry) {
		super(inputProvider, commandParser, commandRegistry);
		this.terminal = inputProvider.getTerminal();
	}

	@Override
	public void print(String message) {
		this.terminal.writer().println(message);
	}

	@Override
	public void flush() {
		this.terminal.flush();
	}

	@Override
	public PrintWriter getWriter() {
		return this.terminal.writer();
	}

}
