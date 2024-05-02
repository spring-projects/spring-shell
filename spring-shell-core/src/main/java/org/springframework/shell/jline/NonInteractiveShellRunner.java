/*
 * Copyright 2021-2024 the original author or authors.
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
package org.springframework.shell.jline;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.impl.DefaultParser;

import org.springframework.core.annotation.Order;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.Utils;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A {@link ShellRunner} that executes commands without entering interactive shell mode.
 *
 * <p>Has higher precedence than {@link InteractiveShellRunner} which gives it an opportunity to handle the shell
 * in non-interactive fashion.
 *
 * @author Janne Valkealahti
 * @author Chris Bono
 */
@Order(NonInteractiveShellRunner.PRECEDENCE)
public class NonInteractiveShellRunner implements ShellRunner {

	/**
	 * The precedence at which this runner is ordered by the DefaultApplicationRunner - which also controls
	 * the order it is consulted on the ability to handle the current shell.
	 */
	public static final int PRECEDENCE = InteractiveShellRunner.PRECEDENCE - 50;

	private final Shell shell;

	private final ShellContext shellContext;

	private Parser lineParser;

	private String primaryCommand;

    private static final String SINGLE_QUOTE = "\'";
    private static final String DOUBLE_QUOTE = "\"";

	private Function<String[], List<String>> commandsFromArgs = args -> {
		if (ObjectUtils.isEmpty(args)) {
			if (StringUtils.hasText(primaryCommand)) {
				Collections.singletonList(primaryCommand);
			}
			else {
				return Collections.emptyList();
			}
		}
		// re-quote if needed having whitespace
		String raw = Arrays.stream(args)
			.map(a -> {
				if (!isQuoted(a) && StringUtils.containsWhitespace(a)) {
					return "\"" + a + "\"";
				}
				return a;
			})
			.collect(Collectors.joining(" "));
		if (StringUtils.hasText(primaryCommand)) {
			return Collections.singletonList(primaryCommand + " " + raw);
		}
		else {
			return Collections.singletonList(raw);
		}
	};

	private static boolean isQuoted(String str) {
		if (str == null) {
			return false;
		}
		return str.startsWith(SINGLE_QUOTE) && str.endsWith(SINGLE_QUOTE)
				|| str.startsWith(DOUBLE_QUOTE) && str.endsWith(DOUBLE_QUOTE);
	}

	public NonInteractiveShellRunner(Shell shell, ShellContext shellContext) {
		this(shell, shellContext, null);
	}

	public NonInteractiveShellRunner(Shell shell, ShellContext shellContext, String primaryCommand) {
		this.shell = shell;
		this.shellContext = shellContext;
		this.primaryCommand = primaryCommand;
		this.lineParser = new DefaultParser();
	}

	/**
	 * Sets the function that creates the command() to run from the input application arguments.
	 *
	 * @param commandsFromArgs function that takes input application arguments and creates zero or more commands
	 *                                 where each command is a string that specifies the command and options
	 *                                 (eg. 'history --file myHistory.txt')
	 */
	public void setCommandsFromArgs(Function<String[], List<String>> commandsFromArgs) {
		this.commandsFromArgs = commandsFromArgs;
	}

	/**
	 * Sets the line parser used to parse commands.
	 *
	 * @param lineParser the line parser used to parse commands
	 */
	public void setLineParser(Parser lineParser) {
		this.lineParser = lineParser;
	}

	@Override
	public boolean run(String[] args) throws Exception {
		List<String> commands = commandsFromArgs.apply(args);
		if (commands.isEmpty()) {
			return false;
		}
		List<ParsedLine> parsedLines = commands.stream()
				.map(rawCommandLine -> lineParser.parse(rawCommandLine, rawCommandLine.length() + 1))
				.collect(Collectors.toList());
		MultiParsedLineInputProvider inputProvider = new MultiParsedLineInputProvider(parsedLines);
		shellContext.setInteractionMode(InteractionMode.NONINTERACTIVE);
		shell.run(inputProvider);
		return true;
	}

	/**
	 * An {@link InputProvider} that returns an input for each entry in a list of {@link ParsedLine parsed lines}.
	 */
	static class MultiParsedLineInputProvider implements InputProvider {

		private final List<ParsedLineInput> parsedLineInputs;
		private int inputIdx;

		MultiParsedLineInputProvider(List<ParsedLine> parsedLines) {
			this.parsedLineInputs = parsedLines.stream()
					.map(ParsedLineInput::new)
					.collect(Collectors.toList());
		}

		@Override
		public Input readInput() {
			if (inputIdx == parsedLineInputs.size()) {
				return null;
			}
			return parsedLineInputs.get(inputIdx++);
		}

		private static class ParsedLineInput implements Input {

			private final ParsedLine parsedLine;

			ParsedLineInput(ParsedLine parsedLine) {
				this.parsedLine = parsedLine;
			}

			@Override
			public String rawText() {
				return parsedLine.line();
			}

			@Override
			public List<String> words() {
				return Utils.sanitizeInput(parsedLine.words());
			}
		}
	}
}
