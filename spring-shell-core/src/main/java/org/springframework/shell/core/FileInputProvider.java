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

import java.io.*;

import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

/**
 * An {@link InputProvider} that reads input from a file.
 * <p>
 * Supports backslashes at end of line to signal line continuation.
 * </p>
 *
 * @author Eric Bottard
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 * @author David Pilar
 */
public class FileInputProvider implements InputProvider, AutoCloseable {

	private static final String BACKSLASH_AT_EOL_REGEX = "(.*)\\\\\\s*$";

	private final BufferedReader reader;

	/**
	 * Create a new {@link FileInputProvider} instance.
	 * @param file the file to read input from
	 * @throws FileNotFoundException if the file does not exist
	 */
	public FileInputProvider(File file) throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(file));
	}

	@Override
	@Nullable public String readInput() throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean continued;
		String line;
		do {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			continued = line.matches(BACKSLASH_AT_EOL_REGEX);
			sb.append(line.replaceFirst(BACKSLASH_AT_EOL_REGEX, "$1 "));
		}
		while (continued);
		if (line == null) {
			return null;
		}
		else if (!StringUtils.hasLength(line) || isComment(line)) {
			return readInput();
		}
		else {
			return sb.toString();
		}
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	private boolean isComment(String line) {
		return line.matches("\\s*//.*");
	}

}
