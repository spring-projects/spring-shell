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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandNotFoundException;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ShellTestClientTests {

	@Test
	void testCommandExecution(@Autowired ShellTestClient shellTestClient) throws Exception {
		// when
		ShellScreen shellScreen = shellTestClient.sendCommand("test");

		// then
		ShellAssertions.assertThat(shellScreen).containsText("Test command executed");
	}

	@Test
	void testUnknownCommandExecution(@Autowired ShellTestClient shellTestClient) {
		// when
		Assertions.assertThatThrownBy(() -> shellTestClient.sendCommand("foo"))
			.isInstanceOf(CommandNotFoundException.class);
	}

	@Test
	void testCommandExecutionWithReadingInput(@Autowired ShellTestClient client) throws Exception {
		// given
		ShellInputProvider inputProvider = ShellInputProvider.providerFor("hello").withInput("hi").build();

		// when
		ShellScreen screen = client.sendCommand(inputProvider);

		// then
		ShellAssertions.assertThat(screen).containsText("You said: hi");
	}

	@Test
	void testCommandExecutionWithReadingPassword(@Autowired ShellTestClient client) throws Exception {
		// given
		ShellInputProvider inputProvider = ShellInputProvider.providerFor("password").withPassword("secret123").build();

		// when
		ShellScreen screen = client.sendCommand(inputProvider);

		// then
		ShellAssertions.assertThat(screen).containsText("Your password is: secret123");
	}

	@Test
	void testCommandExecutionWithComplexInputs(@Autowired ShellTestClient client) throws Exception {
		// given
		ShellInputProvider inputProvider = ShellInputProvider.providerFor("complex")
			.withInput("One", "Two")
			.withPassword("secret1", "secret2")
			.build();

		// when
		ShellScreen screen = client.sendCommand(inputProvider);

		// then
		ShellAssertions.assertThat(screen).containsText("First input is: One");
		ShellAssertions.assertThat(screen).containsText("First password is: secret1");
		ShellAssertions.assertThat(screen).containsText("Second input is: Two");
		ShellAssertions.assertThat(screen).containsText("Second password is: secret2");
	}

	@Configuration
	static class TestCommands {

		@Bean
		public Command test() {
			return new AbstractCommand("test", "A test command") {
				@Override
				public ExitStatus doExecute(CommandContext commandContext) {
					commandContext.outputWriter().println("Test command executed");
					return ExitStatus.OK;
				}
			};
		}

		@Bean
		public Command hello() {
			return new AbstractCommand("hello", "A hello command") {
				@Override
				public ExitStatus doExecute(CommandContext commandContext) throws Exception {
					String message = commandContext.inputReader().readInput();
					commandContext.outputWriter().println("You said: " + message);
					return ExitStatus.OK;
				}
			};
		}

		@Bean
		public Command password() {
			return new AbstractCommand("password", "A password command") {
				@Override
				public ExitStatus doExecute(CommandContext commandContext) throws Exception {
					char[] chars = commandContext.inputReader().readPassword();
					commandContext.outputWriter().println("Your password is: " + new String(chars));
					return ExitStatus.OK;
				}
			};
		}

		@Bean
		public Command complexCommand() {
			return new AbstractCommand("complex", "A complex command") {
				@Override
				public ExitStatus doExecute(CommandContext commandContext) throws Exception {
					String message = commandContext.inputReader().readInput();
					commandContext.outputWriter().println("First input is: " + message);

					char[] chars = commandContext.inputReader().readPassword();
					commandContext.outputWriter().println("First password is: " + new String(chars));

					message = commandContext.inputReader().readInput();
					commandContext.outputWriter().println("Second input is: " + message);

					chars = commandContext.inputReader().readPassword();
					commandContext.outputWriter().println("Second password is: " + new String(chars));

					return ExitStatus.OK;
				}
			};
		}

		@Bean
		public CommandRegistry commandRegistry() {
			return new CommandRegistry();
		}

		@Bean
		public CommandParser commandParser(CommandRegistry commandRegistry) {
			return new DefaultCommandParser(commandRegistry);
		}

		@Bean
		public ShellTestClient shellTestClient(CommandParser commandParser, CommandRegistry commandRegistry) {
			return new ShellTestClient(commandParser, commandRegistry);
		}

	}

}