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

import java.io.PrintWriter;

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
		// when
		ShellScreen screen = client.sendCommand("hello", ShellInputProvider.builder().input("hi").build());

		// then
		ShellAssertions.assertThat(screen).containsText("You said: hi");
	}

	@Test
	void testCommandExecutionWithReadingPassword(@Autowired ShellTestClient client) throws Exception {
		// when
		ShellScreen screen = client.sendCommand("password", ShellInputProvider.builder().password("secret123").build());

		// then
		ShellAssertions.assertThat(screen).containsText("Your password is: secret123");
	}

	@Test
	void testCommandExecutionWithComplexInputs(@Autowired ShellTestClient client) throws Exception {
		// given
		ShellInputProvider inputProvider = ShellInputProvider.builder()
			.input("One", "Two")
			.password("secret1", "secret2")
			.build();

		// when
		ShellScreen screen = client.sendCommand("complex", inputProvider);

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