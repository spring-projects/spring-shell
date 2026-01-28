/*
 * Copyright 2017-present the original author or authors.
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
package org.springframework.shell.core.command;

import java.io.File;
import java.util.List;
import java.util.Objects;

import org.springframework.shell.core.FileInputProvider;
import org.springframework.shell.core.NonInteractiveShellRunner;

/**
 * A command that can read and execute other commands from a file.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 * @author David Pilar
 */
public class Script implements Command {

	private CommandParser commandParser = new DefaultCommandParser();

	@Override
	public String getDescription() {
		return "Execute commands from a script file";
	}

	@Override
	public String getGroup() {
		return "Built-In Commands";
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
		String scriptFile = commandContext.parsedInput()
			.options()
			.stream()
			.filter(o -> "file".equals(o.longName()) || 'f' == o.shortName())
			.map(CommandOption::value)
			.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(
					"Script command expects option --file or -f with exactly one argument: the absolute path to the script file to execute."));
		File file = new File(Objects.requireNonNull(scriptFile));
		try (FileInputProvider inputProvider = new FileInputProvider(file)) {
			String input;
			while ((input = inputProvider.readInput()) != null) {
				executeCommand(commandContext, input);
			}
		}
		return ExitStatus.OK;
	}

	private void executeCommand(CommandContext commandContext, String input) throws Exception {
		String[] commandTokens = input.split(" ");
		NonInteractiveShellRunner shellRunner = new NonInteractiveShellRunner(this.commandParser,
				commandContext.commandRegistry(), commandContext.outputWriter());
		shellRunner.run(commandTokens);
	}

	/**
	 * Set the command parser to use to parse commands in the script.
	 * @param commandParser the command parser to set
	 */
	public void setCommandParser(CommandParser commandParser) {
		this.commandParser = commandParser;
	}

}
