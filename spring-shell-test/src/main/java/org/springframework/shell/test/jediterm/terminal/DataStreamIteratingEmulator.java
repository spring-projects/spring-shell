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

import org.springframework.shell.test.jediterm.terminal.emulator.Emulator;

/**
 * @author jediterm authors
 */
public abstract class DataStreamIteratingEmulator implements Emulator {

	protected final TerminalDataStream myDataStream;
	protected final Terminal myTerminal;

	private boolean myEof = false;

	public DataStreamIteratingEmulator(TerminalDataStream dataStream, Terminal terminal) {
		myDataStream = dataStream;
		myTerminal = terminal;
	}

	@Override
	public boolean hasNext() {
		return !myEof;
	}

	@Override
	public void resetEof() {
		myEof = false;
	}

	@Override
	public void next() throws IOException {
		try {
			char b = myDataStream.getChar();
			processChar(b, myTerminal);
		}
		catch (TerminalDataStream.EOF e) {
			myEof = true;
		}
	}

	protected abstract void processChar(char ch, Terminal terminal) throws IOException;
}
