package org.springframework.shell.core.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.shell.core.InputReader;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CommandContextTests {

	private CommandContext context;

	@BeforeEach
	void setUp() {
		ParsedInput parsedInput = ParsedInput.builder()
			.addOption(CommandOption.with().longName("aWrong").description("command1").build())
			.addOption(CommandOption.with().longName("intendedA").shortName('a').description("command2").build())
			.addOption(CommandOption.with().shortName('b').description("command3").build())
			.addOption(CommandOption.with().longName("x").description("command4").build())
			.addOption(CommandOption.with().shortName('x').description("command5").build())
			.addOption(CommandOption.with().shortName('y').description("command6").build())
			.addOption(CommandOption.with().longName("y").description("command7").build())
			.build();
		context = new CommandContext(parsedInput, mock(CommandRegistry.class), mock(PrintWriter.class),
				mock(InputReader.class));
	}

	@ParameterizedTest
	@CsvSource({ "aWrong, command1", "intendedA, command2", "noSuchOption, null", "x, command4", "y, command7" })
	void testGetOptionByLongName(String longName, String description) {
		// when
		CommandOption commandOption = context.getOptionByLongName(longName);

		// then
		String result = commandOption == null ? "null" : commandOption.description();
		assertEquals(description, result);
	}

	@ParameterizedTest
	@CsvSource({ "a, command2", "b, command3", "n, null", "x, command5", "y, command6" })
	void testGetOptionByShortName(char shortName, String description) {
		// when
		CommandOption commandOption = context.getOptionByShortName(shortName);

		// then
		String result = commandOption == null ? "null" : commandOption.description();
		assertEquals(description, result);
	}

	@ParameterizedTest
	@CsvSource({ "aWrong, command1", "intendedA, command2", "noSuchOption, null", "a, command2", "b, command3",
			"n, null", "x, command4", "y, command7" })
	void testGetOptionByName(String optionName, String description) {
		// when
		CommandOption commandOption = context.getOptionByName(optionName);

		// then
		String result = commandOption == null ? "null" : commandOption.description();
		assertEquals(description, result);
	}

}