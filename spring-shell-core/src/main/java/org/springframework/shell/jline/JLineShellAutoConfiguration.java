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

package org.springframework.shell.jline;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.shell.CompletingParsedLine;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.Shell;

/**
 * Shell implementation using JLine to capture input and trigger completions.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
@Configuration
class JLineShellAutoConfiguration {

	@Autowired
	@Qualifier("main")
	private  ResultHandler resultHandler;

	@Autowired
	private PromptProvider promptProvider;

	@Autowired
	private History history;

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
		return new Shell(new JLineInputProvider(lineReader(), promptProvider), resultHandler);
	}

	@Bean
	@ConditionalOnMissingBean(PromptProvider.class)
	public PromptProvider promptProvider() {
		return () -> new AttributedString("shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}

	/**
	 * Installs a default JLine history, and triggers saving to file on context shutdown. Filename is based on
	 * {@literal spring.application.name}.
	 *
	 * @author Eric Bottard
	 */
	@Configuration
	@ConditionalOnMissingBean(History.class)
	public static class HistoryConfiguration {

		@Autowired @Lazy
		private History history;

		@Bean
		public History history(LineReader lineReader, @Value("${spring.application.name:spring-shell}.log") String historyPath) {
			lineReader.setVariable(LineReader.HISTORY_FILE, Paths.get(historyPath));
			return new DefaultHistory(lineReader);
		}

		@EventListener
		public void onContextClosedEvent(ContextClosedEvent event) throws IOException {
			history.save();
		}
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
	public Parser parser() {
		ExtendedDefaultParser parser = new ExtendedDefaultParser();
		parser.setEofOnUnclosedQuote(true);
		parser.setEofOnEscapedNewLine(true);
		return parser;
	}

	@Bean
	public LineReader lineReader() {
		LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder()
				.terminal(terminal())
				.appName("Spring Shell")
				.completer(completer())
				.history(history)
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
				.parser(parser());

		return lineReaderBuilder.build();
	}

	/**
	 * Sanitize the buffer input given the customizations applied to the JLine parser (<em>e.g.</em> support for
	 * line continuations, <em>etc.</em>)
	 */
	static List<String> sanitizeInput(List<String> words) {
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

	public static class JLineInputProvider implements InputProvider {

		private final LineReader lineReader;

		private final PromptProvider promptProvider;

		public JLineInputProvider(LineReader lineReader, PromptProvider promptProvider) {
			this.lineReader = lineReader;
			this.promptProvider = promptProvider;
		}

		@Override
		public Input readInput() {
			try {
				AttributedString prompt = promptProvider.getPrompt();
				lineReader.readLine(prompt.toAnsi(lineReader.getTerminal()));
			}
			catch (UserInterruptException e) {
				if (e.getPartialLine().isEmpty()) {
					throw new ExitRequest(1);
				} else {
					return Input.EMPTY;
				}
			}
			return new ParsedLineInput(lineReader.getParsedLine());
		}


	}

}

