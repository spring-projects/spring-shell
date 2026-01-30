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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultCommandParserTests {

	private CommandParser parser;

	private CommandRegistry commandRegistry;

	@BeforeEach
	void setUp() {
		this.commandRegistry = new CommandRegistry();
		this.parser = new DefaultCommandParser(this.commandRegistry);
	}

	@Test
	void testParseLongOptionWithoutValue() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> parser.parse("mycommand --optionA -b=value2 arg1"));
	}

	@Test
	void testParseShortOptionWithoutValue() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> parser.parse("mycommand -a --optionB=value2 arg1"));
	}

	@Test
	void testParseCommandWithoutSubCommand() {
		ParsedInput parsedInput = parser
			.parse("mycommand --optionA=value1 arg1 -b=value2 arg2 --optionC value3 -d value4");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(4, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandOption optionA = parsedInput.options().get(0);
		assertEquals(' ', optionA.shortName());
		assertEquals("optionA", optionA.longName());
		assertEquals("value1", optionA.value());

		CommandOption optionB = parsedInput.options().get(1);
		assertEquals('b', optionB.shortName());
		assertEquals("", optionB.longName());
		assertEquals("value2", optionB.value());

		CommandOption optionC = parsedInput.options().get(2);
		assertEquals(' ', optionC.shortName());
		assertEquals("optionC", optionC.longName());
		assertEquals("value3", optionC.value());

		CommandOption optionD = parsedInput.options().get(3);
		assertEquals('d', optionD.shortName());
		assertEquals("", optionD.longName());
		assertEquals("value4", optionD.value());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	@Test
	void testParseCommandWithSubCommand() {
		commandRegistry.registerCommand(createCommand("mycommand mysubcommand", "My test command"));
		ParsedInput parsedInput = parser.parse("mycommand mysubcommand --optionA=value1 arg1 -b=value2 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(1, parsedInput.subCommands().size());
		assertEquals("mysubcommand", parsedInput.subCommands().get(0));
		assertEquals(2, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandOption optionA = parsedInput.options().get(0);
		assertEquals(' ', optionA.shortName());
		assertEquals("optionA", optionA.longName());
		assertEquals("value1", optionA.value());

		CommandOption optionB = parsedInput.options().get(1);
		assertEquals('b', optionB.shortName());
		assertEquals("", optionB.longName());
		assertEquals("value2", optionB.value());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	@Test
	void testParseWithMultipleSubCommands() {
		commandRegistry.registerCommand(createCommand("mycommand mysubcommand1 mysubcommand2", "My test command"));
		ParsedInput parsedInput = parser
			.parse("mycommand mysubcommand1 mysubcommand2 --optionA=value1 arg1 -b=value2 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(2, parsedInput.subCommands().size());
		assertEquals("mysubcommand1", parsedInput.subCommands().get(0));
		assertEquals("mysubcommand2", parsedInput.subCommands().get(1));
		assertEquals(2, parsedInput.options().size());
		assertEquals(2, parsedInput.arguments().size());

		CommandOption optionA = parsedInput.options().get(0);
		assertEquals(' ', optionA.shortName());
		assertEquals("optionA", optionA.longName());
		assertEquals("value1", optionA.value());

		CommandOption optionB = parsedInput.options().get(1);
		assertEquals('b', optionB.shortName());
		assertEquals("", optionB.longName());
		assertEquals("value2", optionB.value());

		CommandArgument argument1 = parsedInput.arguments().get(0);
		assertEquals(0, argument1.index());
		assertEquals("arg1", argument1.value());

		CommandArgument argument2 = parsedInput.arguments().get(1);
		assertEquals(1, argument2.index());
		assertEquals("arg2", argument2.value());
	}

	@Test
	void testParseWithSubCommandWithoutOptions() {
		commandRegistry.registerCommand(createCommand("mycommand mysubcommand", "My test command"));
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

	// In this case, subcommands should still be parsed, and the rest considered as
	// arguments
	@Test
	void testParseWithSubCommandWithoutOptionsAndWithoutSeparator() {
		commandRegistry.registerCommand(createCommand("mycommand mysubcommand", "My test command"));
		ParsedInput parsedInput = parser.parse("mycommand mysubcommand arg1 arg2");
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(1, parsedInput.subCommands().size());
		assertEquals("mysubcommand", parsedInput.subCommands().get(0));
		assertEquals("arg1", parsedInput.arguments().get(0).value());
		assertEquals("arg2", parsedInput.arguments().get(1).value());
	}

	@ParameterizedTest
	@MethodSource("parseWithQuotedOptionData")
	void testParseWithQuotedOption(String input, String longName, char shortName, String expectedValue) {
		// when
		ParsedInput parsedInput = parser.parse(input);

		// then
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(1, parsedInput.options().size());
		assertEquals(longName, parsedInput.options().get(0).longName());
		assertEquals(shortName, parsedInput.options().get(0).shortName());
		assertEquals(expectedValue, parsedInput.options().get(0).value());
	}

	static Stream<Arguments> parseWithQuotedOptionData() {
		return Stream.of(Arguments.of("mycommand --option=value", "option", ' ', "value"),
				Arguments.of("mycommand --option=\\\"value\\\"", "option", ' ', "\\\"value\\\""),
				Arguments.of("mycommand --option=\"value\"", "option", ' ', "value"),
				Arguments.of("mycommand --option=\"value1 value2\"", "option", ' ', "value1 value2"),
				Arguments.of("mycommand --option=\"value1=value2\"", "option", ' ', "value1=value2"),
				Arguments.of("mycommand --option=value1\"inside\"value2", "option", ' ', "value1\"inside\"value2"),
				Arguments.of("mycommand --option=\"value1 \\\"inside\\\" value2\"", "option", ' ',
						"value1 \"inside\" value2"),
				Arguments.of("mycommand --option=value1'inside'value2", "option", ' ', "value1'inside'value2"),
				Arguments.of("mycommand --option=\"value1 'inside' value2\"", "option", ' ', "value1 'inside' value2"),
				Arguments.of("mycommand   --option=value", "option", ' ', "value"),
				Arguments.of("mycommand   --option=\"value\"", "option", ' ', "value"),

				Arguments.of("mycommand --option value", "option", ' ', "value"),
				Arguments.of("mycommand --option \\\"value\\\"", "option", ' ', "\\\"value\\\""),
				Arguments.of("mycommand --option \"value\"", "option", ' ', "value"),
				Arguments.of("mycommand --option \"value1 value2\"", "option", ' ', "value1 value2"),
				Arguments.of("mycommand --option \"value1=value2\"", "option", ' ', "value1=value2"),
				Arguments.of("mycommand --option value1\"inside\"value2", "option", ' ', "value1\"inside\"value2"),
				Arguments.of("mycommand --option \"value1 \\\"inside\\\" value2\"", "option", ' ',
						"value1 \"inside\" value2"),
				Arguments.of("mycommand --option value1'inside'value2", "option", ' ', "value1'inside'value2"),
				Arguments.of("mycommand --option \"value1 'inside' value2\"", "option", ' ', "value1 'inside' value2"),
				Arguments.of("mycommand   --option   value", "option", ' ', "value"),
				Arguments.of("mycommand   --option   \"value\"", "option", ' ', "value"),

				Arguments.of("mycommand -o=value", "", 'o', "value"),
				Arguments.of("mycommand -o=\\\"value\\\"", "", 'o', "\\\"value\\\""),
				Arguments.of("mycommand -o=\"value\"", "", 'o', "value"),
				Arguments.of("mycommand -o=\"value1 value2\"", "", 'o', "value1 value2"),
				Arguments.of("mycommand -o=\"value1=value2\"", "", 'o', "value1=value2"),
				Arguments.of("mycommand -o=value1\"inside\"value2", "", 'o', "value1\"inside\"value2"),
				Arguments.of("mycommand -o=\"value1 \\\"inside\\\" value2\"", "", 'o', "value1 \"inside\" value2"),
				Arguments.of("mycommand -o=value1'inside'value2", "", 'o', "value1'inside'value2"),
				Arguments.of("mycommand -o=\"value1 'inside' value2\"", "", 'o', "value1 'inside' value2"),
				Arguments.of("mycommand   -o=value", "", 'o', "value"),
				Arguments.of("mycommand   -o=\"value\"", "", 'o', "value"),

				Arguments.of("mycommand -o value", "", 'o', "value"),
				Arguments.of("mycommand -o \\\"value\\\"", "", 'o', "\\\"value\\\""),
				Arguments.of("mycommand -o \"value\"", "", 'o', "value"),
				Arguments.of("mycommand -o \"value1 value2\"", "", 'o', "value1 value2"),
				Arguments.of("mycommand -o \"value1=value2\"", "", 'o', "value1=value2"),
				Arguments.of("mycommand -o value1\"inside\"value2", "", 'o', "value1\"inside\"value2"),
				Arguments.of("mycommand -o \"value1 \\\"inside\\\" value2\"", "", 'o', "value1 \"inside\" value2"),
				Arguments.of("mycommand -o value1'inside'value2", "", 'o', "value1'inside'value2"),
				Arguments.of("mycommand -o \"value1 'inside' value2\"", "", 'o', "value1 'inside' value2"),
				Arguments.of("mycommand   -o   \"value\"", "", 'o', "value"));
	}

	@ParameterizedTest
	@MethodSource("parseWithQuotedArgumentData")
	void testParseWithQuotedArgument(String input, String expectedValue) {
		// when
		ParsedInput parsedInput = parser.parse(input);

		// then
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(expectedValue, parsedInput.arguments().get(0).value());
		assertEquals(1, parsedInput.arguments().size());
	}

	static Stream<Arguments> parseWithQuotedArgumentData() {
		return Stream.of(Arguments.of("mycommand -- value", "value"),
				Arguments.of("mycommand -- \\\"value\\\"", "\\\"value\\\""),
				Arguments.of("mycommand -- \"value\"", "value"),
				Arguments.of("mycommand -- \"value1 value2\"", "value1 value2"),
				Arguments.of("mycommand -- \"value1=value2\"", "value1=value2"),
				Arguments.of("mycommand -- value1\"inside\"value2", "value1\"inside\"value2"),
				Arguments.of("mycommand -- \"value1 \\\"inside\\\" value2\"", "value1 \"inside\" value2"),
				Arguments.of("mycommand -- value1'inside'value2", "value1'inside'value2"),
				Arguments.of("mycommand -- \"value1 'inside' value2\"", "value1 'inside' value2"),
				Arguments.of("mycommand  --  value", "value"), Arguments.of("mycommand  --  \"value\"", "value"));
	}

	@ParameterizedTest
	@MethodSource("parseWithBooleanOptionData")
	void testParseWithBooleanOption(String input, String longName, char shortName, Class<?> type,
			String expectedValue) {
		// given
		Command command = createCommand("mycommand", "My test command");
		command.getOptions().add(CommandOption.with().longName(longName).shortName(shortName).type(type).build());
		commandRegistry.registerCommand(command);
		// when
		ParsedInput parsedInput = parser.parse(input);

		// then
		assertEquals("mycommand", parsedInput.commandName());
		assertEquals(1, parsedInput.options().size());
		assertEquals(longName, parsedInput.options().get(0).longName());
		assertEquals(shortName, parsedInput.options().get(0).shortName());
		assertEquals(expectedValue, parsedInput.options().get(0).value());
	}

	static Stream<Arguments> parseWithBooleanOptionData() {
		return Stream.of(Arguments.of("mycommand --option=true", "option", ' ', boolean.class, "true"),
				Arguments.of("mycommand --option=false", "option", ' ', boolean.class, "false"),
				Arguments.of("mycommand --option true", "option", ' ', boolean.class, "true"),
				Arguments.of("mycommand --option false", "option", ' ', boolean.class, "false"),
				Arguments.of("mycommand --option", "option", ' ', boolean.class, "true"),

				Arguments.of("mycommand -on=true", "", 'o', boolean.class, "true"),
				Arguments.of("mycommand -o=false", "", 'o', boolean.class, "false"),
				Arguments.of("mycommand -o true", "", 'o', boolean.class, "true"),
				Arguments.of("mycommand -o false", "", 'o', boolean.class, "false"),
				Arguments.of("mycommand -o", "", 'o', boolean.class, "true"),

				Arguments.of("mycommand --option=true", "option", ' ', Boolean.class, "true"),
				Arguments.of("mycommand --option=false", "option", ' ', Boolean.class, "false"),
				Arguments.of("mycommand --option true", "option", ' ', Boolean.class, "true"),
				Arguments.of("mycommand --option false", "option", ' ', Boolean.class, "false"),
				Arguments.of("mycommand --option", "option", ' ', Boolean.class, "true"),

				Arguments.of("mycommand -on=true", "", 'o', Boolean.class, "true"),
				Arguments.of("mycommand -o=false", "", 'o', Boolean.class, "false"),
				Arguments.of("mycommand -o true", "", 'o', Boolean.class, "true"),
				Arguments.of("mycommand -o false", "", 'o', Boolean.class, "false"),
				Arguments.of("mycommand -o", "", 'o', Boolean.class, "true"));
	}

	private static Command createCommand(String name, String description) {
		return new AbstractCommand(name, description) {
			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		};
	}

}