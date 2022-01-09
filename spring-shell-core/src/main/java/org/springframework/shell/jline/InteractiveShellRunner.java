/*
 * Copyright 2017 the original author or authors.
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

import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;

/**
 * Default Boot runner that bootstraps the shell application in interactive
 * mode.
 *
 * Runs the REPL of the shell unless the {@literal spring.shell.interactive}
 * property has been set to {@literal false}.
 *
 * @author Eric Bottard
 */
@Order(InteractiveShellRunner.PRECEDENCE)
public class InteractiveShellRunner implements ShellRunner {

	/**
	 * The precedence at which this runner is set. Highger precedence runners may effectively disable this one by setting
	 * the {@link #SPRING_SHELL_INTERACTIVE_ENABLED} property to {@literal false}.
	 */
	public static final int PRECEDENCE = 0;

	private final LineReader lineReader;

	private final PromptProvider promptProvider;

	private final Shell shell;

	private final ShellContext shellContext;

	public InteractiveShellRunner(LineReader lineReader, PromptProvider promptProvider, Shell shell,
			ShellContext shellContext) {
		this.lineReader = lineReader;
		this.promptProvider = promptProvider;
		this.shell = shell;
		this.shellContext = shellContext;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		shellContext.setInteractionMode(InteractionMode.INTERACTIVE);
		InputProvider inputProvider = new JLineInputProvider(lineReader, promptProvider);
		shell.run(inputProvider);
	}

	@Override
	public boolean canRun(ApplicationArguments args) {
		return true;
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
