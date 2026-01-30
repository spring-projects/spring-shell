/*
 * Copyright 2026-present the original author or authors.
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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.shell.core.InputReader;

class HelpTests {

	@Test
	void testDefaultHelpMessage() throws Exception {
		// given
		CommandRegistry commandRegistry = new CommandRegistry();
		StringWriter stringWriter = new StringWriter();
		PrintWriter outputWriter = new PrintWriter(stringWriter);
		InputReader inputReader = new InputReader() {
		};
		ParsedInput parsedInput = ParsedInput.builder().build();
		CommandContext commandContext = new CommandContext(parsedInput, commandRegistry, outputWriter, inputReader);

		// when
		Help help = new Help();
		help.execute(commandContext);

		// then
		String actualOutput = stringWriter.toString();
		String expectedOutput = """
				AVAILABLE COMMANDS

				Built-In Commands
					quit, exit: Exit the shell

				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

	@Test
	void testHelpMessageForCommand() throws Exception {
		// given
		CommandOption nameOption = CommandOption.with()
			.shortName('n')
			.longName("name")
			.type(String.class)
			.required(true)
			.description("Name of the person to greet")
			.build();
		CommandOption timesOption = CommandOption.with()
			.shortName('t')
			.longName("times")
			.type(int.class)
			.required(false)
			.defaultValue("1")
			.description("Number of times to greet")
			.build();
		Command command = Command.builder()
			.name("hi")
			.description("Say hi")
			.group("Greetings")
			.help("This command says hi to the user.")
			.options(nameOption, timesOption)
			.execute(commandContext -> {
			});
		ParsedInput parsedInput = ParsedInput.builder()
			.commandName("hi")
			.addArgument(new CommandArgument(0, "hi"))
			.build();
		CommandRegistry commandRegistry = new CommandRegistry();
		commandRegistry.registerCommand(command);
		StringWriter stringWriter = new StringWriter();
		PrintWriter outputWriter = new PrintWriter(stringWriter);
		InputReader inputReader = new InputReader() {
		};
		CommandContext commandContext = new CommandContext(parsedInput, commandRegistry, outputWriter, inputReader);

		// when
		Help help = new Help();
		help.execute(commandContext);

		// then
		String actualOutput = stringWriter.toString();
		String expectedOutput = """
				NAME
					hi - Say hi

				SYNOPSIS
					hi [--name String] --times int --help

				OPTIONS
					--name or -n String
					Name of the person to greet
					[Mandatory]

					--times or -t int
					Number of times to greet
					[Optional, default = 1]

					--help or -h
					help for hi
					[Optional]


				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

	@Test
	void testHelpMessageForCommandAlias() throws Exception {
		// given
		CommandOption nameOption = CommandOption.with()
			.shortName('n')
			.longName("name")
			.type(String.class)
			.required(true)
			.description("Name of the person to greet")
			.build();
		CommandOption timesOption = CommandOption.with()
			.shortName('t')
			.longName("times")
			.type(int.class)
			.required(false)
			.defaultValue("1")
			.description("Number of times to greet")
			.build();
		Command command = Command.builder()
			.name("hi")
			.description("Say hi")
			.aliases("hello", "hey")
			.group("Greetings")
			.help("This command says hi to the user.")
			.options(nameOption, timesOption)
			.execute(commandContext -> {
			});
		ParsedInput parsedInput = ParsedInput.builder()
			.commandName("hello")
			.addArgument(new CommandArgument(0, "hello"))
			.build();
		CommandRegistry commandRegistry = new CommandRegistry();
		commandRegistry.registerCommand(command);
		StringWriter stringWriter = new StringWriter();
		PrintWriter outputWriter = new PrintWriter(stringWriter);
		InputReader inputReader = new InputReader() {
		};
		CommandContext commandContext = new CommandContext(parsedInput, commandRegistry, outputWriter, inputReader);

		// when
		Help help = new Help();
		help.execute(commandContext);

		// then
		String actualOutput = stringWriter.toString();
		String expectedOutput = """
				NAME
					hi - Say hi

				SYNOPSIS
					hi [--name String] --times int --help

				OPTIONS
					--name or -n String
					Name of the person to greet
					[Mandatory]

					--times or -t int
					Number of times to greet
					[Optional, default = 1]

					--help or -h
					help for hi
					[Optional]

				ALIASES
					hello, hey

				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

}