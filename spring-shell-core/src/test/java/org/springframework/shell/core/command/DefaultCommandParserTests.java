/*
 * Copyright 2025-present the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultCommandParserTests {

	private final CommandParser parser = new DefaultCommandParser();

	@Test
	void testParseWithoutSubCommand() {
		ParsedInput parsedInput = parser.parse("mycommand --option1=value1 -o2=value2 arg1 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(2, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandOption option1 = parsedInput.options().get(0);
		assertEquals(' ', option1.shortName());
		assertEquals("option1", option1.longName());
		assertEquals("value1", option1.value());

		CommandOption option2 = parsedInput.options().get(1);
		assertEquals('o', option2.shortName());
		assertEquals("", option2.longName());
		assertEquals("value2", option2.value());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	@Test
	void testParseWithoutSubCommandWithMixedOptionsAndArguments() {
		ParsedInput parsedInput = parser.parse("mycommand --option1=value1 arg1 -o2=value2 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(2, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandOption option1 = parsedInput.options().get(0);
		assertEquals(' ', option1.shortName());
		assertEquals("option1", option1.longName());
		assertEquals("value1", option1.value());

		CommandOption option2 = parsedInput.options().get(1);
		assertEquals('o', option2.shortName());
		assertEquals("", option2.longName());
		assertEquals("value2", option2.value());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	@Test
	void testParseWithSubCommand() {
		ParsedInput parsedInput = parser.parse("mycommand mysubcommand --option1=value1 arg1 -o2=value2 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(1, parsedInput.subCommands().size());
		assertEquals("mysubcommand", parsedInput.subCommands().get(0));
		assertEquals(2, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandOption option1 = parsedInput.options().get(0);
		assertEquals(' ', option1.shortName());
		assertEquals("option1", option1.longName());
		assertEquals("value1", option1.value());

		CommandOption option2 = parsedInput.options().get(1);
		assertEquals('o', option2.shortName());
		assertEquals("", option2.longName());
		assertEquals("value2", option2.value());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	@Test
	void testParseWithMultipleSubCommands() {
		ParsedInput parsedInput = parser
			.parse("mycommand mysubcommand1 mysubcommand2 --option1=value1 arg1 -o2=value2 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(2, parsedInput.subCommands().size());
		assertEquals("mysubcommand1", parsedInput.subCommands().get(0));
		assertEquals("mysubcommand2", parsedInput.subCommands().get(1));
		assertEquals(2, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandOption option1 = parsedInput.options().get(0);
		assertEquals(' ', option1.shortName());
		assertEquals("option1", option1.longName());
		assertEquals("value1", option1.value());

		CommandOption option2 = parsedInput.options().get(1);
		assertEquals('o', option2.shortName());
		assertEquals("", option2.longName());
		assertEquals("value2", option2.value());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	@Test
	void testParseWithSubCommandWithoutOptions() {
		ParsedInput parsedInput = parser.parse("mycommand mysubcommand -- arg1 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(1, parsedInput.subCommands().size());
		assertEquals("mysubcommand", parsedInput.subCommands().get(0));
		assertEquals(0, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	// In this case, arguments are considered as subcommands since there's no separator
	@Test
	void testParseWithSubCommandWithoutOptionsAndWithoutSeparator() {
		ParsedInput parsedInput = parser.parse("mycommand mysubcommand arg1 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(3, parsedInput.subCommands().size());
		assertEquals("mysubcommand", parsedInput.subCommands().get(0));
		assertEquals("arg1", parsedInput.subCommands().get(1));
		assertEquals("arg2", parsedInput.subCommands().get(2));
	}

}