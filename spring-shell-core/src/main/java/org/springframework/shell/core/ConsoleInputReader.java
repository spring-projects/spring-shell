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

/**
 * Implementation of {@link InputReader} that reads input from the system console.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class ConsoleInputReader implements InputReader {

	private final Console console;

	/**
	 * Create a new {@link ConsoleInputReader} instance.
	 * @param console the system console
	 */
	public ConsoleInputReader(Console console) {
		this.console = console;
	}

	@Override
	public String readInput() {
		return this.console.readLine();
	}

	@Override
	public String readInput(String prompt) {
		return this.console.readLine(prompt);
	}

	@Override
	public char[] readPassword() {
		return this.console.readPassword();
	}

	@Override
	public char[] readPassword(String prompt) {
		return this.console.readPassword(prompt);
	}

}
