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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;

/**
 * Default Boot runner that bootstraps the shell application.
 *
 * <p>
 *     Default implementation has default priority and looks for application arguments that start with an {@literal @},
 *     assuming they are paths to script files. Executes them and quits if they are present, starts the shell interactively
 *     otherwise.
 * </p>
 *
 * @author Eric Bottard
 */
@Order(DefaultShellApplicationRunner.PRECEDENCE)
public class DefaultShellApplicationRunner implements ApplicationRunner {

	public static final int PRECEDENCE = 0;

	private final LineReader lineReader;

	private final PromptProvider promptProvider;

	private final Parser parser;

	private final Shell shell;

	public DefaultShellApplicationRunner(LineReader lineReader, PromptProvider promptProvider, Parser parser, Shell shell) {
		this.lineReader = lineReader;
		this.promptProvider = promptProvider;
		this.parser = parser;
		this.shell = shell;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<File> scriptsToRun = args.getNonOptionArgs().stream()
				.filter(s -> s.startsWith("@"))
				.map(s -> new File(s.substring(1)))
				.collect(Collectors.toList());

		if (scriptsToRun.isEmpty()) {
			InputProvider inputProvider = new JLineInputProvider(lineReader, promptProvider);
			shell.run(inputProvider);
		} else {
			for (File file : scriptsToRun) {
				try (Reader reader = new FileReader(file); FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {
					shell.run(inputProvider);
				}
			}
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
