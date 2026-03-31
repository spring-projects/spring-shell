/*
 * Copyright 2026-present the original author or authors.
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

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a provider for commands with their inputs and passwords used in shell
 * interactions. This class uses two {@link Deque} instances to manage the queue of user
 * inputs and passwords. It serves as a utility for simulating user input during testing
 * or automated shell interactions.
 *
 * @param command represents the command to be executed in the shell.
 * @param inputs Queue containing user input strings.
 * @param passwords Queue containing user password strings.
 * @author David Pilar
 * @author Mahmoud Ben Hassine
 * @since 4.0.2
 */
public record ShellInputProvider(String command, Deque<String> inputs, Deque<String> passwords) {

	/**
	 * Creates a new input provider for the specified command.
	 * @param command the command for which the input provider is to be created
	 * @return a new {@link Builder} instance initialized with the given command
	 */
	static Builder providerFor(String command) {
		return Builder.builder(command);
	}

	final static class Builder {

		private final String command;

		private final List<String> inputs = new ArrayList<>();

		private final List<String> passwords = new ArrayList<>();

		Builder(String command) {
			this.command = command;
		}

		static Builder builder(String command) {
			return new Builder(command);
		}

		public Builder withInput(String... input) {
			inputs.addAll(List.of(input));
			return this;
		}

		public Builder withPassword(String... password) {
			passwords.addAll(List.of(password));
			return this;
		}

		public ShellInputProvider build() {
			return new ShellInputProvider(command, new LinkedList<>(inputs), new LinkedList<>(passwords));
		}

	}
}