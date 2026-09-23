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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Mahmoud Ben Hassine
 */
class CommandHelpRendererTests {

	@Test
	void testRenderHelpWithoutHelpText() {
		// given
		Command command = Command.builder()
			.name("hi")
			.description("Say hi")
			.group("Greetings")
			.execute(commandContext -> {
			});

		// when
		String actualOutput = CommandHelpRenderer.renderHelp(command);

		// then
		String expectedOutput = """
				NAME
					hi - Say hi

				SYNOPSIS
					hi --help

				OPTIONS
					--help or -h
					help for hi
					[Optional]

				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

	@Test
	void testRenderHelpWithMultiLineHelpText() {
		// given
		Command command = Command.builder()
			.name("hi")
			.description("Say hi")
			.group("Greetings")
			.help("This command says hi to the user.\nIt is meant to be friendly.")
			.execute(commandContext -> {
			});

		// when
		String actualOutput = CommandHelpRenderer.renderHelp(command);

		// then
		String expectedOutput = """
				NAME
					hi - Say hi

				DESCRIPTION
					This command says hi to the user.
					It is meant to be friendly.

				SYNOPSIS
					hi --help

				OPTIONS
					--help or -h
					help for hi
					[Optional]

				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

	@Test
	void testRenderHelpWithRequiredAndOptionalOptions() {
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

		// when
		String actualOutput = CommandHelpRenderer.renderHelp(command);

		// then
		String expectedOutput = """
				NAME
					hi - Say hi

				DESCRIPTION
					This command says hi to the user.

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
	void testRenderHelpWithOptionalOptionMissingDefaultValueUsesPrimitiveDefault() {
		// given
		CommandOption timesOption = CommandOption.with()
			.shortName('t')
			.longName("times")
			.type(int.class)
			.required(false)
			.description("Number of times to greet")
			.build();
		Command command = Command.builder()
			.name("hi")
			.description("Say hi")
			.group("Greetings")
			.options(timesOption)
			.execute(commandContext -> {
			});

		// when
		String actualOutput = CommandHelpRenderer.renderHelp(command);

		// then
		String expectedOutput = """
				NAME
					hi - Say hi

				SYNOPSIS
					hi --times int --help

				OPTIONS
					--times or -t int
					Number of times to greet
					[Optional, default = 0]

					--help or -h
					help for hi
					[Optional]

				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

	@Test
	void testRenderHelpWithArguments() {
		// given
		CommandArgument nameArgument = CommandArgument.with()
			.index(0)
			.type(String.class)
			.defaultValue("world")
			.description("the name of the person to greet")
			.build();
		Command command = Command.builder()
			.name("hi")
			.description("Say hi")
			.group("Greetings")
			.arguments(nameArgument)
			.execute(commandContext -> {
			});

		// when
		String actualOutput = CommandHelpRenderer.renderHelp(command);

		// then
		String expectedOutput = """
				NAME
					hi - Say hi

				SYNOPSIS
					hi [(String)] --help

				OPTIONS
					--help or -h
					help for hi
					[Optional]

				ARGUMENTS [Positional]
					[Index 0] String
					the name of the person to greet
					[default = world]

				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

	@Test
	void testRenderHelpWithVariadicArgument() {
		// given
		CommandArgument valuesArgument = CommandArgument.with()
			.index(0)
			.type(Integer.class)
			.description("the values to sum up")
			.variadic(true)
			.build();
		Command command = Command.builder()
			.name("sum")
			.description("Sum numbers")
			.group("Math")
			.arguments(valuesArgument)
			.execute(commandContext -> {
			});

		// when
		String actualOutput = CommandHelpRenderer.renderHelp(command);

		// then
		String expectedOutput = """
				NAME
					sum - Sum numbers

				SYNOPSIS
					sum (Integer...) --help

				OPTIONS
					--help or -h
					help for sum
					[Optional]

				ARGUMENTS [Positional]
					[Index 0...] Integer
					the values to sum up

				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

	@Test
	void testRenderHelpWithAliases() {
		// given
		Command command = Command.builder()
			.name("hi")
			.description("Say hi")
			.aliases("hello", "hey")
			.group("Greetings")
			.execute(commandContext -> {
			});

		// when
		String actualOutput = CommandHelpRenderer.renderHelp(command);

		// then
		String expectedOutput = """
				NAME
					hi - Say hi

				SYNOPSIS
					hi --help

				OPTIONS
					--help or -h
					help for hi
					[Optional]

				ALIASES
					hello, hey
				""";
		Assertions.assertEquals(expectedOutput.replaceAll("\\R", "\n"), actualOutput.replaceAll("\\R", "\n"));
	}

}
