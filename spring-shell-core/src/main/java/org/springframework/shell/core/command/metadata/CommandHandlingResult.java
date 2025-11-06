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
package org.springframework.shell.core.command.metadata;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.command.CommandExceptionResolver;

/**
 * Holds the outcome of handling, typically by a {@link CommandExceptionResolver}.
 *
 * <p>
 * A result may include a message, an exit code, or both.
 * </p>
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public record CommandHandlingResult(@Nullable String message, @Nullable Integer exitCode) {

	public CommandHandlingResult(String message) {
		this(message, null);
	}

	public CommandHandlingResult(int exitCode) {
		this(null, exitCode);
	}

	/**
	 * Returns whether this result contains a value.
	 * @return {@code true} if a message or an exit code is present
	 */
	public boolean isPresent() {
		return message != null || exitCode != null;
	}

	/**
	 * Returns whether this result is empty.
	 * @return {@code true} if no message and no exit code are present
	 */
	public boolean isEmpty() {
		return !isPresent();
	}

	/**
	 * Returns an empty {@code CommandHandlingResult}.
	 * @return an empty result
	 */
	public static CommandHandlingResult empty() {
		return new CommandHandlingResult(null, null);
	}

}
