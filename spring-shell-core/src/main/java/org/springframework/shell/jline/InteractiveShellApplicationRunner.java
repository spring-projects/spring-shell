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

import java.util.Collections;

import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;

/**
 * Default Boot runner that bootstraps the shell application in interactive mode.
 *
 * <p>
 *     Runs the REPL of the shell unless the {@literal spring.shell.interactive} property has been set to {@literal false}.
 * </p>
 *
 * @author Eric Bottard
 */
@Order(InteractiveShellApplicationRunner.PRECEDENCE)
public class InteractiveShellApplicationRunner implements ApplicationRunner {

	/**
	 * The precedence at which this runner is set. Highger precedence runners may effectively disable this one by setting
	 * the {@link #SPRING_SHELL_INTERACTIVE_ENABLED} property to {@literal false}.
	 */
	public static final int PRECEDENCE = 0;

	public static final String SPRING_SHELL_INTERACTIVE = "spring.shell.interactive";
	public static final String ENABLED = "enabled";

	/** The name of the property that controls whether this runner effectively does something. */
	public static final String SPRING_SHELL_INTERACTIVE_ENABLED = SPRING_SHELL_INTERACTIVE + "." + ENABLED;

	private final LineReader lineReader;

	private final PromptProvider promptProvider;

	private final Parser parser;

	private final Shell shell;

	private final Environment environment;

	public InteractiveShellApplicationRunner(LineReader lineReader, PromptProvider promptProvider, Parser parser, Shell shell, Environment environment) {
		this.lineReader = lineReader;
		this.promptProvider = promptProvider;
		this.parser = parser;
		this.shell = shell;
		this.environment = environment;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		boolean interactive = isEnabled();
		if (interactive) {
			InputProvider inputProvider = new JLineInputProvider(lineReader, promptProvider);
			shell.run(inputProvider);
		}
	}

	public boolean isEnabled() {
		return environment.getProperty(SPRING_SHELL_INTERACTIVE_ENABLED,boolean.class,  true);
	}

	/**
	 * Helper method to dynamically disable this runner.
	 */
	public static void disable(ConfigurableEnvironment environment) {
		environment.getPropertySources().addFirst(new MapPropertySource("interactive.override",
				Collections.singletonMap(SPRING_SHELL_INTERACTIVE_ENABLED, "false")));
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
