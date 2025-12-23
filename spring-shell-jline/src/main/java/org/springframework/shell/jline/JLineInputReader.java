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

import org.jline.reader.LineReader;

import org.springframework.shell.core.InputReader;

/**
 * Implementation of {@link InputReader} that reads input using JLine's
 * {@link LineReader}.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class JLineInputReader implements InputReader {

	private final LineReader lineReader;

	/**
	 * Create a new {@link JLineInputReader} instance.
	 * @param lineReader the JLine line reader
	 */
	public JLineInputReader(LineReader lineReader) {
		this.lineReader = lineReader;
	}

	@Override
	public String readInput() {
		return lineReader.readLine();
	}

	@Override
	public String readInput(String prompt) {
		return lineReader.readLine(prompt);
	}

	@Override
	public char[] readPassword() {
		return lineReader.readLine('*').toCharArray();
	}

	@Override
	public char[] readPassword(String prompt) {
		return lineReader.readLine(prompt, '*').toCharArray();
	}

}
