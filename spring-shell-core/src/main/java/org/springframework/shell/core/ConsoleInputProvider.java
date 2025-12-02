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

import org.jspecify.annotations.Nullable;

/**
 * Input provider based on the JVM's system {@link Console}.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class ConsoleInputProvider implements InputProvider {

	private final Console console;

	private String prompt = "$>";

	/**
	 * Create a new {@link ConsoleInputProvider} instance.
	 */
	public ConsoleInputProvider() {
		this.console = System.console();
	}

	/**
	 * Create a new {@link ConsoleInputProvider} instance.
	 * @param console the system console
	 */
	public ConsoleInputProvider(Console console) {
		this.console = console;
	}

	@Override
	public @Nullable String readInput() throws Exception {
		return this.console.readLine(this.prompt);
	}

	public Console getConsole() {
		return this.console;
	}

}
