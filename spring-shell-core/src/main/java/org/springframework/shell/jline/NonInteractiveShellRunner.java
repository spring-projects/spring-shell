/*
 * Copyright 2021 the original author or authors.
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

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Non interactive {@link ShellRunner} which is meant to execute shell commands
 * without entering interactive shell.
 *
 * @author Janne Valkealahti
 */
@Order(InteractiveShellRunner.PRECEDENCE - 50)
public class NonInteractiveShellRunner implements ShellRunner {

	private final Shell shell;
	private final ShellContext shellContext;

	public NonInteractiveShellRunner(Shell shell, ShellContext shellContext) {
		this.shell = shell;
		this.shellContext = shellContext;
	}

	@Override
	public boolean canRun(ApplicationArguments args) {
		List<String> argsToShellCommand = Arrays.asList(args.getSourceArgs());
		return !ObjectUtils.isEmpty(argsToShellCommand);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		shellContext.setInteractionMode(InteractionMode.NONINTERACTIVE);
		List<String> argsToShellCommand = Arrays.asList(args.getSourceArgs());
		InputProvider inputProvider = new StringInputProvider(argsToShellCommand);
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
