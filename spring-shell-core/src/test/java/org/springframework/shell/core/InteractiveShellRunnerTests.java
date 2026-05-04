package org.springframework.shell.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ParsedInput;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Pilar
 */
class InteractiveShellRunnerTests {

	@Test
	void errorMessageHasSpaceBetweenCommandAndSubCommands() throws Exception {
		// given: a command "foo bar" that throws when executed
		Consumer<CommandContext> failingExecutor = ctx -> {
			throw new RuntimeException("boom");
		};
		Command failingCommand = Command.builder().name("foo is bar").execute(failingExecutor);
		CommandRegistry registry = new CommandRegistry();
		registry.registerCommand(failingCommand);

		ParsedInput parsedInput = ParsedInput.builder()
			.commandName("foo")
			.addSubCommand("is")
			.addSubCommand("bar")
			.build();
		CommandParser parser = input -> parsedInput;

		InputProvider inputProvider = new SingleLineInputProvider("foo is bar");
		RecordingShellRunner runner = new RecordingShellRunner(inputProvider, parser, registry);

		// when
		runner.run(new String[0]);

		// then
		assertThat(runner.printed).anyMatch(line -> line.startsWith("Unable to run command foo is bar"));
		assertThat(runner.printed).noneMatch(line -> line.contains("Unable to run command foois bar"));
		assertThat(runner.printed).noneMatch(line -> line.contains("Unable to run command fooisbar"));
		assertThat(runner.printed).noneMatch(line -> line.contains("Unable to run command foo isbar"));
	}

	private static class SingleLineInputProvider implements InputProvider {

		private final String line;

		private boolean consumed;

		SingleLineInputProvider(String line) {
			this.line = line;
		}

		@Override
		public String readInput() {
			if (consumed) {
				return null;
			}
			consumed = true;
			return line;
		}

	}

	private static class RecordingShellRunner extends InteractiveShellRunner {

		private final List<String> printed = new ArrayList<>();

		private final PrintWriter writer = new PrintWriter(new StringWriter());

		RecordingShellRunner(InputProvider inputProvider, CommandParser commandParser,
				CommandRegistry commandRegistry) {
			super(inputProvider, commandParser, commandRegistry);
		}

		@Override
		public void print(String message) {
			printed.add(message);
		}

		@Override
		public void flush() {
		}

		@Override
		public PrintWriter getWriter() {
			return writer;
		}

		@Override
		public InputReader getReader() {
			return new InputReader() {
			};
		}

	}

}
