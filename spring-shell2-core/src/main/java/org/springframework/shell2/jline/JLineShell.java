/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell2.jline;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell2.CompletingParsedLine;
import org.springframework.shell2.CompletionContext;
import org.springframework.shell2.CompletionProposal;
import org.springframework.shell2.ExitRequest;
import org.springframework.shell2.Input;
import org.springframework.shell2.ResultHandler;
import org.springframework.shell2.Shell;

/**
 * Shell implementation using JLine to capture input and trigger completions.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
@Configuration
public class JLineShell {

	@Autowired
	@Qualifier("main")
	private  ResultHandler resultHandler;

	@Bean
	public Terminal terminal() {
		try {
			return TerminalBuilder.builder().build();
		}
		catch (IOException e) {
			throw new BeanCreationException("Could not create Terminal: " + e.getMessage());
		}
	}

	@Bean
	public Shell shell() {
		return new Shell(new JLineInputProvider(lineReader()), resultHandler);
	}

	@Bean
	public CompleterAdapter completer() {
		return new CompleterAdapter();
	}

	/*
	 * Using setter injection to work around a circular dependency.
	 */
	@PostConstruct
	public void lateInit() {
		completer().setShell(shell());
	}

	@Bean
	public LineReader lineReader() {
		ExtendedDefaultParser parser = new ExtendedDefaultParser();
		parser.setEofOnUnclosedQuote(true);
		parser.setEofOnEscapedNewLine(true);

		LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder()
				.terminal(terminal())
				.appName("Foo")
				.completer(completer())
				.highlighter(new Highlighter() {

					@Override
					public AttributedString highlight(LineReader reader, String buffer) {
						int l = 0;
						String best = null;
						for (String command : shell().listCommands().keySet()) {
							if (buffer.startsWith(command) && command.length() > l) {
								l = command.length();
								best = command;
							}
						}
						if (best != null) {
							return new AttributedStringBuilder(buffer.length()).append(best, AttributedStyle.BOLD).append(buffer.substring(l)).toAttributedString();
						}
						else {
							return new AttributedString(buffer, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
						}
					}
				})
				.parser(parser);

		return lineReaderBuilder.build();
	}

	/**
	 * Sanitize the buffer input given the customizations applied to the JLine parser (<em>e.g.</em> support for
	 * line continuations, <em>etc.</em>)
	 */
	static private List<String> sanitizeInput(List<String> words) {
		words = words.stream()
			.map(s -> s.replaceAll("^\\n+|\\n+$", "")) // CR at beginning/end of line introduced by backslash continuation
			.map(s -> s.replaceAll("\\n+", " ")) // CR in middle of word introduced by return inside a quoted string
			.collect(Collectors.toList());
		return words;
	}

	/**
	 * A bridge between JLine's {@link Completer} contract and our own.
	 * @author Eric Bottard
	 */
	public static class CompleterAdapter implements Completer {

		private Shell shell;

		@Override
		public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
			CompletingParsedLine cpl = (line instanceof CompletingParsedLine) ? ((CompletingParsedLine) line) : t -> t;

			CompletionContext context = new CompletionContext(sanitizeInput(line.words()), line.wordIndex(), line.wordCursor());

			List<CompletionProposal> proposals = shell.complete(context);
			proposals.stream()
				.map(p -> new Candidate(
					p.dontQuote() ? p.value() : cpl.emit(p.value()).toString(),
					p.displayText(),
					p.category(),
					p.description(),
					null,
					null,
					true)
				)
				.forEach(candidates::add);
		}

		public void setShell(Shell shell) {
			this.shell = shell;
		}
	}

	public static class JLineInputProvider implements Shell.InputProvider {

		private final LineReader lineReader;

		public JLineInputProvider(LineReader lineReader) {
			this.lineReader = lineReader;
		}

		@Override
		public Input readInput() {
			try {
				lineReader.readLine(new AttributedString("shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)).toAnsi(lineReader.getTerminal()));
			}
			catch (UserInterruptException e) {
				if (e.getPartialLine().isEmpty()) {
					throw new ExitRequest(1);
				} else {
					return Input.EMPTY;
				}
			}
			return new JLineInput(lineReader.getParsedLine());
		}


	}

	private static class JLineInput implements Input {

		private final ParsedLine parsedLine;

		JLineInput(ParsedLine parsedLine) {
			this.parsedLine = parsedLine;
		}

		@Override
		public String rawText() {
			return parsedLine.line();
		}

		@Override
		public List<String> words() {
			return sanitizeInput(parsedLine.words());
		}
	}

}

