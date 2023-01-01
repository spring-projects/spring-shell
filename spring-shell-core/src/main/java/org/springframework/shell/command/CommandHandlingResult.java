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
package org.springframework.shell.command;

import org.springframework.lang.Nullable;

/**
 * Holder for handling some processing, typically with {@link CommandExceptionResolver}.
 *
 * @author Janne Valkealahti
 */
public interface CommandHandlingResult {

	/**
	 * Gets a message for this {@code CommandHandlingResult}.
	 *
	 * @return a message
	 */
	@Nullable
	String message();

	/**
	 * Gets an exit code for this {@code CommandHandlingResult}. Exit code only has meaning
	 * if shell is in non-interactive mode.
	 *
	 * @return an exit code
	 */
	Integer exitCode();

	/**
	 * Indicate whether this {@code CommandHandlingResult} has a result.
	 *
	 * @return true if result exist
	 */
	public boolean isPresent();

	/**
	 * Indicate whether this {@code CommandHandlingResult} does not have a result.
	 *
	 * @return true if result doesn't exist
	 */
	public boolean isEmpty();

	/**
	 * Gets an empty instance of {@code CommandHandlingResult}.
	 *
	 * @return empty instance of {@code CommandHandlingResult}
	 */
	public static CommandHandlingResult empty() {
		return of(null);
	}

	/**
	 * Gets an instance of {@code CommandHandlingResult}.
	 *
	 * @param message the message
	 * @return instance of {@code CommandHandlingResult}
	 */
	public static CommandHandlingResult of(@Nullable String message) {
		return of(message, null);
	}

	/**
	 * Gets an instance of {@code CommandHandlingResult}.
	 *
	 * @param message the message
	 * @param exitCode the exit code
	 * @return instance of {@code CommandHandlingResult}
	 */
	public static CommandHandlingResult of(@Nullable String message, Integer exitCode) {
		return new DefaultHandlingResult(message, exitCode);
	}

	static class DefaultHandlingResult implements CommandHandlingResult {

		private final String message;
		private final Integer exitCode;

		DefaultHandlingResult(String message) {
			this(message, null);
		}

		DefaultHandlingResult(String message, Integer exitCode) {
			this.message = message;
			this.exitCode = exitCode;
		}

		@Override
		public String message() {
			return message;
		}

		@Override
		public Integer exitCode() {
			return exitCode;
		}

		@Override
		public boolean isPresent() {
			return message != null || exitCode != null;
		}

		@Override
		public boolean isEmpty() {
			return !isPresent();
		}
	}
}
