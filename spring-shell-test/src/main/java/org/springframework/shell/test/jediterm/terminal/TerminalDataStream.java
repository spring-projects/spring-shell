/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.test.jediterm.terminal;

import java.io.IOException;

/**
 * Represents data communication interface for terminal.
 * It allows to {@link #getChar()} by one and {@link #pushChar(char)} back as well as requesting a chunk of plain ASCII
 * characters ({@link #readNonControlCharacters(int)} - for faster processing from buffer in the size {@literal <=maxChars}).
 *
 *
 * @author jediterm authors
 */
public interface TerminalDataStream {

	char getChar() throws IOException;

	void pushChar(char c) throws IOException;

	String readNonControlCharacters(int maxChars) throws IOException;

	void pushBackBuffer(char[] bytes, int length) throws IOException;

	boolean isEmpty();

	class EOF extends IOException {
		public EOF() {
			super("EOF: There is no more data or connection is lost");
		}
	}
}
