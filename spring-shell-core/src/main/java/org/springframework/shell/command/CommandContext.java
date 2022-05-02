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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.jline.terminal.Terminal;

import org.springframework.shell.command.CommandParser.CommandParserResult;
import org.springframework.shell.command.CommandParser.CommandParserResults;
import org.springframework.util.ObjectUtils;

/**
 * Interface containing information about current command execution.
 *
 * @author Janne Valkealahti
 */
public interface CommandContext {

	/**
	 * Gets a raw args passed into a currently executing command.
	 *
	 * @return raw command arguments
	 */
	String[] getRawArgs();

	/**
	 * Gets if option has been mapped.
	 *
	 * @param name the option name
	 * @return true if option has been mapped, false otherwise
	 */
	boolean hasMappedOption(String name);

	/**
	 * Gets a command option parser results.
	 *
	 * @return the command option parser results
	 */
	CommandParserResults getParserResults();

	/**
	 * Gets an mapped option value.
	 *
	 * @param <T> the type to map to
	 * @param name the option name
	 * @return mapped value
	 */
	<T> T getOptionValue(String name);

	/**
	 * Gets a terminal.
	 *
	 * @return a terminal
	 */
	Terminal getTerminal();

	/**
	 * Gets an instance of a default {@link CommandContext}.
	 *
	 * @param args the arguments
	 * @param results the results
	 * @param terminal the terminal
	 * @return a command context
	 */
	static CommandContext of(String[] args, CommandParserResults results, Terminal terminal) {
		return new DefaultCommandContext(args, results, terminal);
	}

	/**
	 * Default implementation of a {@link CommandContext}.
	 */
	static class DefaultCommandContext implements CommandContext {

		private String[] args;
		private CommandParserResults results;
		private Terminal terminal;

		DefaultCommandContext(String[] args, CommandParserResults results, Terminal terminal) {
			this.args = args;
			this.results = results;
			this.terminal = terminal;
		}

		@Override
		public String[] getRawArgs() {
			return args;
		}

		@Override
		public boolean hasMappedOption(String name) {
			return find(name).isPresent();
		}

		@Override
		public CommandParserResults getParserResults() {
			return results;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getOptionValue(String name) {
			Optional<CommandParserResult> find = find(name);
			if (find.isPresent()) {
				return (T) find.get().value();
			}
			return null;
		}

		@Override
		public Terminal getTerminal() {
			return terminal;
		}

		private Optional<CommandParserResult> find(String name) {
			return results.results().stream()
				.filter(r -> {
					Stream<String> l = Arrays.asList(r.option().getLongNames()).stream();
					Stream<String> s = Arrays.asList(r.option().getShortNames()).stream().map(n -> Character.toString(n));
					return Stream.concat(l, s)
						.filter(o -> ObjectUtils.nullSafeEquals(o, name))
						.findFirst()
						.isPresent();
				})
				.findFirst();
		}
	}
}
