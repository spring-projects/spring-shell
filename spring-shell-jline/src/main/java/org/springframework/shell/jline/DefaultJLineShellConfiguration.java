package org.springframework.shell.jline;

import java.io.File;
import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
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
		return new JLineShellRunner(inputProvider, new DefaultCommandParser(commandRegistry), commandRegistry);
	}

	@Bean
	public JLineInputProvider inputProvider(LineReader lineReader) {
		return new JLineInputProvider(lineReader);
	}

	@Bean
	public LineReader lineReader(Terminal terminal, Parser parser, CommandCompleter commandCompleter) {
		return LineReaderBuilder.builder()
			.terminal(terminal)
			.completer(commandCompleter)
			.parser(parser)
			// keep backslashes in the returned line, escapes are resolved by the
			// command parser
			.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
			.build();
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

	@Bean
	public Parser parser() {
		ExtendedDefaultParser parser = new ExtendedDefaultParser();
		parser.setEofOnUnclosedQuote(true);
		parser.setEofOnEscapedNewLine(true);
		// Backslash is both the DefaultParser escape character and the Windows path
		// separator. Disable escapes on Windows so path tab-completion works (see
		// gh-1169 / gh-240).
		if (File.separatorChar == '\\') {
			parser.setEscapeChars(null);
		}
		return parser;
	}

}
