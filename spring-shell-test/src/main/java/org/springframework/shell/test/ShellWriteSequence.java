/*
 * Copyright 2022-present the original author or authors.
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
 * Interface sequencing various things into terminal aware text types.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
public interface ShellWriteSequence {

	/**
	 * Sequence terminal clear screen.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence clearScreen();

	/**
	 * Sequence terminal carriage return.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence carriageReturn();

	/**
	 * Sequence from command with expected {@code carriage return}.
	 * @param command the command
	 * @return a sequence for chaining
	 */
	ShellWriteSequence command(String command);

	/**
	 * Sequence terminal carriage return. Alias for {@link #carriageReturn}
	 * @return a sequence for chaining
	 * @see #carriageReturn()
	 */
	ShellWriteSequence cr();

	/**
	 * Sequence text.
	 * @param text the text
	 * @return a sequence for chaining
	 */
	ShellWriteSequence text(String text);

	/**
	 * Sequence terminal key down.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyDown();

	/**
	 * Sequence terminal key left.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyLeft();

	/**
	 * Sequence terminal key right.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyRight();

	/**
	 * Sequence terminal key up.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence keyUp();

	/**
	 * Sequence terminal space.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence space();

	/**
	 * Sequence terminal ctrl.
	 * @return a sequence for chaining
	 */
	ShellWriteSequence ctrl(char c);

	/**
	 * Build the result.
	 * @return the result
	 */
	String build();

}
