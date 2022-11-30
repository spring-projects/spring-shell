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
 * Takes data from and sends it back to TTY input and output streams via {@link TtyConnector}
 *
 * @author jediterm authors
 */
public class TtyBasedArrayDataStream extends ArrayTerminalDataStream {

	private final TtyConnector ttyConnector;
	private final Runnable myOnBeforeBlockingWait;

	public TtyBasedArrayDataStream(final TtyConnector ttyConnector, final Runnable onBeforeBlockingWait) {
		super(new char[1024], 0, 0);
		this.ttyConnector = ttyConnector;
		myOnBeforeBlockingWait = onBeforeBlockingWait;
	}

	public TtyBasedArrayDataStream(final TtyConnector ttyConnector) {
		super(new char[1024], 0, 0);
		this.ttyConnector = ttyConnector;
		myOnBeforeBlockingWait = null;
	}

	@Override
	public char getChar() throws IOException {
		if (length == 0) {
			fillBuf();
		}
		return super.getChar();
	}

	@Override
	public String readNonControlCharacters(int maxChars) throws IOException {
		if (length == 0) {
			fillBuf();
		}

		return super.readNonControlCharacters(maxChars);
	}

	@Override
	public String toString() {
		return CharUtils.toHumanReadableText(new String(buf, offset, length));
	}

	private void fillBuf() throws IOException {
		offset = 0;

		if (!this.ttyConnector.ready() && myOnBeforeBlockingWait != null) {
			myOnBeforeBlockingWait.run();
		}
		length = this.ttyConnector.read(buf, offset, buf.length);

		if (length <= 0) {
			length = 0;
			throw new EOF();
		}
	}
}
