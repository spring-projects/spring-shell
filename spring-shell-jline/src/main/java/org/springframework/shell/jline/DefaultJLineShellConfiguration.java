package org.springframework.shell.jline;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.core.ShellConfigurationException;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;

@Configuration
public class DefaultJLineShellConfiguration {

	@Bean
	public ShellRunner shellRunner(JLineInputProvider inputProvider, CommandRegistry commandRegistry) {
		return new JLineShellRunner(inputProvider, new DefaultCommandParser(), commandRegistry);
	}

	@Bean
	public JLineInputProvider inputProvider(LineReader lineReader) {
		return new JLineInputProvider(lineReader);
	}

	@Bean
	public LineReader lineReader(Terminal terminal, CommandCompleter commandCompleter) {
		return LineReaderBuilder.builder().terminal(terminal).completer(commandCompleter).build();
	}

	@Bean
	public CommandCompleter commandCompleter(CommandRegistry commandRegistry) {
		return new CommandCompleter(commandRegistry);
	}

	@Bean
	public Terminal terminal() {
		try {
			return TerminalBuilder.builder().build();
		}
		catch (IOException e) {
			throw new ShellConfigurationException("Unable to configure JLine terminal", e);
		}
	}

}
