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
package org.springframework.shell.test;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

/**
 * JLine based implementation of {@link ShellWriteSequence}.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
class JLineShellWriteSequence implements ShellWriteSequence {

	private final Terminal terminal;

	private final StringBuilder buf = new StringBuilder();

	JLineShellWriteSequence(Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public ShellWriteSequence carriageReturn() {
		this.buf.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
		return this;
	}

	@Override
	public ShellWriteSequence clearScreen() {
		String ansiClearScreen = KeyMap.key(this.terminal, InfoCmp.Capability.clear_screen);
		this.buf.append(ansiClearScreen);
		return this;
	}

	@Override
	public ShellWriteSequence ctrl(char c) {
		String ctrl = KeyMap.ctrl(c);
		this.buf.append(ctrl);
		return this;
	}

	@Override
	public ShellWriteSequence command(String command) {
		this.text(command);
		return carriageReturn();
	}

	@Override
	public ShellWriteSequence cr() {
		return carriageReturn();
	}

	@Override
	public ShellWriteSequence keyUp() {
		this.buf.append(KeyMap.key(this.terminal, InfoCmp.Capability.key_up));
		return this;
	}

	@Override
	public ShellWriteSequence keyDown() {
		this.buf.append(KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
		return this;
	}

	@Override
	public ShellWriteSequence keyLeft() {
		this.buf.append(KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
		return this;
	}

	@Override
	public ShellWriteSequence keyRight() {
		this.buf.append(KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
		return this;
	}

	@Override
	public ShellWriteSequence text(String text) {
		this.buf.append(text);
		return this;
	}

	@Override
	public ShellWriteSequence space() {
		return this.text(" ");
	}

	@Override
	public String build() {
		return buf.toString();
	}

}
