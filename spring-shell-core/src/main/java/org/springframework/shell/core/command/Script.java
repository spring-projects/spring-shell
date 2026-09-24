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
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.shell.core.FileInputProvider;
import org.springframework.shell.core.NonInteractiveShellRunner;

/**
 * A command that can read and execute other commands from a file.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 * @author David Pilar
 * @author Ezequiel Primon
 */
public class Script extends AbstractCommand {

	private static final String CLASSPATH_URL_PREFIX = "classpath:";

	private static final String FILE_URL_PREFIX = "file:";

	private CommandParser commandParser;

	public Script(CommandRegistry commandRegistry) {
		super("script", "Execute commands from a script file", "Built-In Commands");
		this.commandParser = new DefaultCommandParser(commandRegistry);
	}

	@Override
	public String getHelp() {
		return "Execute commands from a script file. The file should contain one command per line.";
	}

	@Override
	public List<CommandOption> getOptions() {
		return List.of(CommandOption.with()
			.type(String.class)
			.shortName('f')
			.longName("file")
			.required(true)
			.description(
					"The script file to execute, as an absolute path, or a Spring resource reference such as classpath: or file:")
			.build());
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) throws Exception {
		String scriptFile = commandContext.parsedInput()
			.options()
			.stream()
			.filter(o -> "file".equals(o.longName()) || 'f' == o.shortName())
			.map(CommandOption::value)
			.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(
					"Script command expects option --file or -f with exactly one argument: the script file to execute."));
		File file = new File(Objects.requireNonNull(scriptFile));
		try (FileInputProvider inputProvider = createInputProvider(file, scriptFile)) {
			String input;
			while ((input = inputProvider.readInput()) != null) {
				executeCommand(commandContext, input);
			}
		}
		return ExitStatus.OK;
	}

	/**
	 * Create the {@link FileInputProvider} for the given script reference. An existing
	 * file is used as-is (backward compatible); otherwise the reference must be prefixed
	 * with {@code classpath:} or {@code file:} to be resolved as a Spring resource. Any
	 * other reference is rejected outright, without attempting to resolve it, so only
	 * local filesystem paths and {@code classpath:}/{@code file:} references are ever
	 * supported.
	 * @param file the script reference as a file
	 * @param scriptFile the raw script reference
	 * @return the input provider for the script
	 */
	private FileInputProvider createInputProvider(File file, String scriptFile) throws Exception {
		if (file.exists()) {
			return new FileInputProvider(file);
		}
		if (!scriptFile.startsWith(CLASSPATH_URL_PREFIX) && !scriptFile.startsWith(FILE_URL_PREFIX)) {
			throw new IllegalArgumentException("Unsupported script file reference: " + scriptFile
					+ ". It must be an existing local filesystem path, or be prefixed with \"" + CLASSPATH_URL_PREFIX
					+ "\" or \"" + FILE_URL_PREFIX + "\".");
		}
		Resource resource = new DefaultResourceLoader().getResource(scriptFile);
		if (!resource.exists()) {
			throw new FileNotFoundException("Script file does not exist: " + scriptFile);
		}
		return new FileInputProvider(new InputStreamReader(resource.getInputStream()));
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
