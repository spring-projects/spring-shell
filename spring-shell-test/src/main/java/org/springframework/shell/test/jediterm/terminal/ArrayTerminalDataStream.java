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

import org.springframework.shell.test.jediterm.terminal.util.CharUtils;

/**
 * Takes data from underlying char array.
 *
 * @author jediterm authors
 */
public class ArrayTerminalDataStream implements TerminalDataStream {

	protected char[] buf;
	protected int offset;
	protected int length;

	public ArrayTerminalDataStream(char[] buf, int offset, int length) {
		this.buf = buf;
		this.offset = offset;
		this.length = length;
	}

	public ArrayTerminalDataStream(char[] buf) {
		this(buf, 0, buf.length);
	}

	@Override
	public char getChar() throws IOException {
		if (this.length == 0) {
			throw new EOF();
		}

		this.length--;

		return this.buf[this.offset++];
	}

	@Override
	public void pushChar(final char c) throws EOF {
		if (this.offset == 0) {
			// Pushed back too many... shift it up to the end.

			char[] newBuf;
			if (this.buf.length - this.length == 0) {
				newBuf = new char[this.buf.length + 1];
			}
			else {
				newBuf = this.buf;
			}
			this.offset = newBuf.length - this.length;
			System.arraycopy(this.buf, 0, newBuf, this.offset, this.length);
			this.buf = newBuf;
		}

		this.length++;
		this.buf[--this.offset] = c;
	}

	@Override
	public String readNonControlCharacters(int maxChars) throws IOException {
		String nonControlCharacters = CharUtils.getNonControlCharacters(maxChars, this.buf, this.offset, this.length);

		this.offset += nonControlCharacters.length();
		this.length -= nonControlCharacters.length();

		return nonControlCharacters;
	}

	@Override
	public void pushBackBuffer(final char[] bytes, final int length) throws EOF {
		for (int i = length - 1; i >= 0; i--) {
			pushChar(bytes[i]);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.length == 0;
	}
}
