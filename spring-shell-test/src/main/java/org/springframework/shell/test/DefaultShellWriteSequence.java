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

/**
 * Default implementation of {@link ShellWriteSequence}.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
class DefaultShellWriteSequence implements ShellWriteSequence {

	private final StringBuilder buf = new StringBuilder();

	@Override
	public ShellWriteSequence carriageReturn() {
		this.buf.append("\r");
		return this;
	}

	@Override
	public ShellWriteSequence clearScreen() {
		this.buf.append("\033[H\033[2J");
		return this;
	}

	@Override
	public ShellWriteSequence ctrl(char c) {
		this.buf.append((char) (c & 0x1f));
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
		this.buf.append("\033[A");
		return this;
	}

	@Override
	public ShellWriteSequence keyDown() {
		this.buf.append("\033[B");
		return this;
	}

	@Override
	public ShellWriteSequence keyLeft() {
		this.buf.append("\033[D");
		return this;
	}

	@Override
	public ShellWriteSequence keyRight() {
		this.buf.append("\033[C");
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
