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
package org.springframework.shell.test;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

/**
 * Interface sequencing various things into terminal aware text types.
 *
 * @author Janne Valkealahti
 */
public interface ShellWriteSequence {

	/**
	 * Sequence terminal clear screen.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence clearScreen();

	/**
	 * Sequence terminal carriage return.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence carriageReturn();

	/**
	 * Sequence from command with expected {@code carriage return}.
	 *
	 * @param command the command
	 * @return a sequence for chaining
	 */
	ShellWriteSequence command(String command);

	/**
	 * Sequence terminal carriage return. Alias for {@link #carriageReturn}
	 *
	 * @return a sequence for chaining
	 * @see #carriageReturn()
	 */
	ShellWriteSequence cr();

	/**
	 * Sequence text.
	 *
	 * @param text the text
	 * @return a sequence for chaining
	 */
	ShellWriteSequence text(String text);

	/**
	 * Sequence terminal key down.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyDown();

	/**
	 * Sequence terminal key left.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyLeft();

	/**
	 * Sequence terminal key right.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyRight();

	/**
	 * Sequence terminal key up.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyUp();

	/**
	 * Sequence terminal space.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence space();

	/**
	 * Sequence terminal ctrl.
	 *
	 * @return a sequence for chaining
	 */
	ShellWriteSequence ctrl(char c);

	/**
	 * Build the result.
	 *
	 * @return the result
	 */
	String build();

	/**
	 * Get a new instance of a {@code ShellWriteSequence}.
	 *
	 * @param terminal the terminal
	 * @return instance of a write sequence
	 */
	static ShellWriteSequence of(Terminal terminal) {
		return new DefaultShellWriteSequence(terminal);
	}

	static class DefaultShellWriteSequence implements ShellWriteSequence {

		private final Terminal terminal;
		private StringBuilder buf = new StringBuilder();

		DefaultShellWriteSequence(Terminal terminal) {
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
}
