package org.springframework.shell.test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a provider for inputs and passwords used in shell interactions. This class
 * uses two {@link Deque} instances to manage the queue of user inputs and passwords. It
 * serves as a utility for simulating user input during testing or automated shell
 * interactions.
 *
 * @param inputs Queue containing user input strings.
 * @param passwords Queue containing user password strings.
 */
public record ShellInputProvider(Deque<String> inputs, Deque<String> passwords) {

	static Builder builder() {
		return Builder.builder();
	}

	final static class Builder {

		private final List<String> inputs = new ArrayList<>();

		private final List<String> passwords = new ArrayList<>();

		static Builder builder() {
			return new Builder();
		}

		public Builder input(String... input) {
			inputs.addAll(List.of(input));
			return this;
		}

		public Builder password(String... password) {
			passwords.addAll(List.of(password));
			return this;
		}

		public ShellInputProvider build() {
			return new ShellInputProvider(new LinkedList<>(inputs), new LinkedList<>(passwords));
		}

	}
}