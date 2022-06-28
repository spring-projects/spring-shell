/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.command.CommandParser.CommandParserResults;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParserTests extends AbstractCommandTests {

	private CommandParser parser;

	@BeforeEach
	public void setupCommandParserTests() {
		ConversionService conversionService = new DefaultConversionService();
		parser = CommandParser.of(conversionService);
	}

	@Test
	public void testEmptyOptionsAndArgs() {
		CommandParserResults results = parser.parse(Collections.emptyList(), new String[0]);
		assertThat(results.results()).hasSize(0);
	}

	@Test
	public void testLongName() {
		CommandOption option1 = longOption("arg1");
		CommandOption option2 = longOption("arg2");
		List<CommandOption> options = Arrays.asList(option1, option2);
		String[] args = new String[]{"--arg1", "foo"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("foo");
	}

	@Test
	public void testShortName() {
		CommandOption option1 = shortOption('a');
		CommandOption option2 = shortOption('b');
		List<CommandOption> options = Arrays.asList(option1, option2);
		String[] args = new String[]{"-a", "foo"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("foo");
	}

	@Test
	public void testMultipleArgs() {
		CommandOption option1 = longOption("arg1");
		CommandOption option2 = longOption("arg2");
		List<CommandOption> options = Arrays.asList(option1, option2);
		String[] args = new String[]{"--arg1", "foo", "--arg2", "bar"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(2);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("foo");
		assertThat(results.results().get(1).option()).isSameAs(option2);
		assertThat(results.results().get(1).value()).isEqualTo("bar");
	}

	@Test
	public void testMultipleArgsWithMultiValues() {
		CommandOption option1 = longOption("arg1", null, false, null, 1, 2);
		CommandOption option2 = longOption("arg2", null, false, null, 1, 2);
		List<CommandOption> options = Arrays.asList(option1, option2);
		String[] args = new String[]{"--arg1", "foo1", "foo2", "--arg2", "bar1", "bar2"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(2);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("foo1 foo2");
		assertThat(results.results().get(1).option()).isSameAs(option2);
		assertThat(results.results().get(1).value()).isEqualTo("bar1 bar2");
		assertThat(results.positional()).isEmpty();
	}

	@Test
	public void testBooleanWithoutArg() {
		ResolvableType type = ResolvableType.forType(boolean.class);
		CommandOption option1 = shortOption('v', type);
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"-v"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo(true);
	}

	@Test
	public void testBooleanWithArg() {
		ResolvableType type = ResolvableType.forType(boolean.class);
		CommandOption option1 = shortOption('v', type);
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"-v", "false"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo(false);
	}

	@Test
	public void testMissingRequiredOption() {
		CommandOption option1 = longOption("arg1", true);
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.errors()).hasSize(1);
	}

	@Test
	public void testSpaceInArgWithOneArg() {
		CommandOption option1 = longOption("arg1");
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"--arg1", "foo bar"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("foo bar");
	}

	@Test
	public void testSpaceInArgWithMultipleArgs() {
		CommandOption option1 = longOption("arg1");
		CommandOption option2 = longOption("arg2");
		List<CommandOption> options = Arrays.asList(option1, option2);
		String[] args = new String[]{"--arg1", "foo bar", "--arg2", "hi"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(2);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("foo bar");
		assertThat(results.results().get(1).option()).isSameAs(option2);
		assertThat(results.results().get(1).value()).isEqualTo("hi");
	}

	@Test
	public void testNonMappedArgs() {
		String[] args = new String[]{"arg1", "arg2"};
		CommandParserResults results = parser.parse(Collections.emptyList(), args);
		assertThat(results.results()).hasSize(0);
		assertThat(results.positional()).containsExactly("arg1", "arg2");
	}

	@Test
	public void testNonMappedArgBeforeOption() {
		CommandOption option1 = longOption("arg1");
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"foo", "--arg1", "value"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("value");
		assertThat(results.positional()).containsExactly("foo");
	}

	@Test
	public void testNonMappedArgAfterOption() {
		CommandOption option1 = longOption("arg1");
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"--arg1", "value", "foo"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("value");
		assertThat(results.positional()).containsExactly("foo");
	}

	@Test
	public void testNonMappedArgWithoutOption() {
		CommandOption option1 = longOption("arg1", 0, 1, 2);
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"value", "foo"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("value foo");
		assertThat(results.positional()).containsExactly("value", "foo");
	}

	@Test
	public void testNonMappedArgWithoutOptionHavingType() {
		CommandOption option1 = longOption("arg1", ResolvableType.forType(String.class), false, 0, 1, 2);
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"value", "foo"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo("value foo");
		assertThat(results.positional()).containsExactly("value", "foo");
	}

	@Test
	public void testShortOptionsCombined() {
		CommandOption optionA = shortOption('a');
		CommandOption optionB = shortOption('b');
		CommandOption optionC = shortOption('c');
		List<CommandOption> options = Arrays.asList(optionA, optionB, optionC);
		String[] args = new String[]{"-abc"};

		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(3);
		assertThat(results.results().get(0).option()).isSameAs(optionA);
		assertThat(results.results().get(1).option()).isSameAs(optionB);
		assertThat(results.results().get(2).option()).isSameAs(optionC);
		assertThat(results.results().get(0).value()).isNull();
		assertThat(results.results().get(1).value()).isNull();
		assertThat(results.results().get(2).value()).isNull();
	}

	@Test
	public void testShortOptionsCombinedBooleanType() {
		CommandOption optionA = shortOption('a', ResolvableType.forType(boolean.class));
		CommandOption optionB = shortOption('b', ResolvableType.forType(boolean.class));
		CommandOption optionC = shortOption('c', ResolvableType.forType(boolean.class));
		List<CommandOption> options = Arrays.asList(optionA, optionB, optionC);
		String[] args = new String[]{"-abc"};

		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(3);
		assertThat(results.results().get(0).option()).isSameAs(optionA);
		assertThat(results.results().get(1).option()).isSameAs(optionB);
		assertThat(results.results().get(2).option()).isSameAs(optionC);
		assertThat(results.results().get(0).value()).isEqualTo(true);
		assertThat(results.results().get(1).value()).isEqualTo(true);
		assertThat(results.results().get(2).value()).isEqualTo(true);
	}

	@Test
	public void testShortOptionsCombinedBooleanTypeArgFalse() {
		CommandOption optionA = shortOption('a', ResolvableType.forType(boolean.class));
		CommandOption optionB = shortOption('b', ResolvableType.forType(boolean.class));
		CommandOption optionC = shortOption('c', ResolvableType.forType(boolean.class));
		List<CommandOption> options = Arrays.asList(optionA, optionB, optionC);
		String[] args = new String[]{"-abc", "false"};

		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(3);
		assertThat(results.results().get(0).option()).isSameAs(optionA);
		assertThat(results.results().get(1).option()).isSameAs(optionB);
		assertThat(results.results().get(2).option()).isSameAs(optionC);
		assertThat(results.results().get(0).value()).isEqualTo(false);
		assertThat(results.results().get(1).value()).isEqualTo(false);
		assertThat(results.results().get(2).value()).isEqualTo(false);
	}

	@Test
	public void testShortOptionsCombinedBooleanTypeSomeArgFalse() {
		CommandOption optionA = shortOption('a', ResolvableType.forType(boolean.class));
		CommandOption optionB = shortOption('b', ResolvableType.forType(boolean.class));
		CommandOption optionC = shortOption('c', ResolvableType.forType(boolean.class));
		List<CommandOption> options = Arrays.asList(optionA, optionB, optionC);
		String[] args = new String[]{"-ac", "-b", "false"};

		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(3);
		assertThat(results.results().get(0).option()).isSameAs(optionA);
		assertThat(results.results().get(1).option()).isSameAs(optionC);
		assertThat(results.results().get(2).option()).isSameAs(optionB);
		assertThat(results.results().get(0).value()).isEqualTo(true);
		assertThat(results.results().get(1).value()).isEqualTo(true);
		assertThat(results.results().get(2).value()).isEqualTo(false);
	}

	@Test
	public void testLongOptionsWithArray() {
		CommandOption option1 = longOption("arg1", ResolvableType.forType(int[].class));
		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{"--arg1", "1", "2"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo(new String[] { "1", "2" });
	}

	@Test
	public void testMapPositionalArgs1() {
		CommandOption option1 = longOption("arg1", 0, 1, 1);
		CommandOption option2 = longOption("arg2", 1, 1, 2);
		List<CommandOption> options = Arrays.asList(option1, option2);
		String[] args = new String[]{"--arg1", "1", "2"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(2);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(1).option()).isSameAs(option2);
		assertThat(results.results().get(0).value()).isEqualTo("1");
		assertThat(results.results().get(1).value()).isEqualTo("2");
	}

	@Test
	public void testMapPositionalArgs2() {
		CommandOption option1 = longOption("arg1", 0, 1, 1);
		CommandOption option2 = longOption("arg2", 1, 1, 2);
		List<CommandOption> options = Arrays.asList(option1, option2);
		String[] args = new String[]{"1", "2"};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(2);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(1).option()).isSameAs(option2);
		assertThat(results.results().get(0).value()).isEqualTo("1");
		assertThat(results.results().get(1).value()).isEqualTo("2");
	}

	@Test
	public void testBooleanWithDefault() {
		ResolvableType type = ResolvableType.forType(boolean.class);
		CommandOption option1 = CommandOption.of(new String[] { "arg1" }, new Character[0], "description", type, false,
				"true", null, null, null, null, null);

		List<CommandOption> options = Arrays.asList(option1);
		String[] args = new String[]{};
		CommandParserResults results = parser.parse(options, args);
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).option()).isSameAs(option1);
		assertThat(results.results().get(0).value()).isEqualTo(true);
	}

	private static CommandOption longOption(String name) {
		return longOption(name, null);
	}

	private static CommandOption longOption(String name, boolean required) {
		return longOption(name, null, required, null);
	}

	private static CommandOption longOption(String name, ResolvableType type) {
		return longOption(name, type, false, null);
	}

	private static CommandOption longOption(String name, int position, int arityMin, int arityMax) {
		return longOption(name, null, false, position, arityMin, arityMax);
	}

	private static CommandOption longOption(String name, ResolvableType type, boolean required, Integer position) {
		return longOption(name, type, required, position, null, null);
	}

	private static CommandOption longOption(String name, ResolvableType type, boolean required, Integer position, Integer arityMin, Integer arityMax) {
		return CommandOption.of(new String[] { name }, new Character[0], "desc", type, required, null, position,
				arityMin, arityMax, null, null);
	}

	private static CommandOption shortOption(char name) {
		return shortOption(name, null);
	}

	private static CommandOption shortOption(char name, ResolvableType type) {
		return CommandOption.of(new String[0], new Character[] { name }, "desc", type);
	}
}
