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
package org.springframework.shell.test.jediterm.terminal.emulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.shell.test.jediterm.terminal.TerminalDataStream;
import org.springframework.shell.test.jediterm.terminal.util.CharUtils;
import org.springframework.shell.test.jediterm.typeahead.Ascii;

/**
 * @author jediterm authors
 */
final class SystemCommandSequence {

	private static final char ST = 0x9c;

	private final List<Object> myArgs = new ArrayList<>();
	private final StringBuilder mySequence = new StringBuilder();

	public SystemCommandSequence( TerminalDataStream stream) throws IOException {
		StringBuilder argBuilder = new StringBuilder();
		boolean end = false;
		while (!end) {
			char ch = stream.getChar();
			mySequence.append(ch);
			end = isEnd();
			if (ch == ';' || end) {
				if (end && isTwoBytesEnd()) {
					argBuilder.deleteCharAt(argBuilder.length() - 1);
				}
				myArgs.add(parseArg(argBuilder.toString()));
				argBuilder.setLength(0);
			}
			else {
				argBuilder.append(ch);
			}
		}
	}

	private  Object parseArg( String arg) {
		if (arg.length() > 0 && Character.isDigit(arg.charAt(arg.length() - 1))) {
			// check isDigit to reduce amount of expensive NumberFormatException
			try {
				return Integer.parseInt(arg);
			}
			catch (NumberFormatException ignored) {
			}
		}
		return arg;
	}

	private boolean isEnd() {
		int len = mySequence.length();
		if (len > 0) {
			char ch = mySequence.charAt(len - 1);
			return ch == Ascii.BEL || ch == ST || isTwoBytesEnd();
		}
		return false;
	}

	private boolean isTwoBytesEnd() {
		int len = mySequence.length();
		return len > 1 && mySequence.charAt(len - 2) == Ascii.ESC && mySequence.charAt(len - 1) == '\\';
	}

	public  String getStringAt(int i) {
		if (i>=myArgs.size()) {
			return null;
		}
		Object val = myArgs.get(i);
		return val instanceof String ? (String)val : null;
	}

	public int getIntAt(int position, int defaultValue) {
		if (position < myArgs.size()) {
			Object val = myArgs.get(position);
			if (val instanceof Integer) {
				return (Integer) val;
			}
		}
		return defaultValue;
	}

	public  String format( String body) {
		return (char)Ascii.ESC + "]" + body + getTerminator();
	}

	@Override
	public String toString() {
		return CharUtils.toHumanReadableText(mySequence.toString());
	}

	/**
	 * <a href="https://invisible-island.net/xterm/ctlseqs/ctlseqs.html">
	 * XTerm accepts either BEL or ST for terminating OSC
	 * sequences, and when returning information, uses the same
	 * terminator used in a query. </a>
	 */
	private  String getTerminator() {
		int len = mySequence.length();
		if (isTwoBytesEnd()) {
			return mySequence.substring(len - 2);
		}
		return mySequence.substring(len - 1);
	}
}
