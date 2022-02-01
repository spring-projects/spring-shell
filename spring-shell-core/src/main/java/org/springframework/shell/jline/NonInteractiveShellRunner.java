/*
 * Copyright 2021-2022 the original author or authors.
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
import java.util.List;
import java.util.function.Function;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;
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
@Order(InteractiveShellRunner.PRECEDENCE - 50)
public class NonInteractiveShellRunner implements ShellRunner {

	private final Shell shell;

	private final ShellContext shellContext;

	private Function<ApplicationArguments, List<String>> argsToShellCommand = (args) -> Arrays.asList(args.getSourceArgs());

	public NonInteractiveShellRunner(Shell shell, ShellContext shellContext) {
		this.shell = shell;
		this.shellContext = shellContext;
	}

	public void setArgsToShellCommand(Function<ApplicationArguments, List<String>> argsToShellCommand) {
		this.argsToShellCommand = argsToShellCommand;
	}

	@Override
	public boolean canRun(ApplicationArguments args) {
		return !argsToShellCommand.apply(args).isEmpty();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		shellContext.setInteractionMode(InteractionMode.NONINTERACTIVE);
		List<String> commands = this.argsToShellCommand.apply(args);
		InputProvider inputProvider = new StringInputProvider(commands);
		shell.run(inputProvider);
	}

	private class StringInputProvider implements InputProvider {

		private final List<String> commands;

		private boolean done;

		StringInputProvider(List<String> commands) {
			this.commands = commands;
		}

		@Override
		public Input readInput() {
			if (!done) {
				done = true;
				return new Input() {
					@Override
					public List<String> words() {
						return commands;
					}

					@Override
					public String rawText() {
						return StringUtils.collectionToDelimitedString(commands, " ");
					}
				};
			}
			else {
				return null;
			}
		}
	}
}
