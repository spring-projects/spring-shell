package org.springframework.shell.core.command.completion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.shell.core.command.CommandOption;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David Pilar
 */
class DefaultCompletionProviderTests {

	@Test
	void testApplyWithNoCommandOption() {
		// given
		CompletionContext context = mock(CompletionContext.class);
		when(context.getCommandOption()).thenReturn(null);

		// when
		List<CompletionProposal> result = new DefaultCompletionProvider().apply(context);

		// then
		assertTrue(result.isEmpty());
	}

	@Test
	void testApplyWithNoEnumCommandOption() {
		// given
		CompletionContext context = mock(CompletionContext.class);
		when(context.getCommandOption()).thenReturn(CommandOption.with().type(String.class).build());

		// when
		List<CompletionProposal> result = new DefaultCompletionProvider().apply(context);

		// then
		assertTrue(result.isEmpty());
	}

	public enum TestEnum {

		VALUE1, VALUE2

	}

	@ParameterizedTest
	@MethodSource("applyWithEnumCommandOptionData")
	void testApplyWithEnumCommandOption(String currentWord, String longName, char shortName,
			List<String> expectedValues) {
		// given
		CompletionContext context = mock(CompletionContext.class);
		when(context.getCommandOption())
			.thenReturn(CommandOption.with().longName(longName).shortName(shortName).type(TestEnum.class).build());
		when(context.currentWord()).thenReturn(currentWord);

		// when
		List<CompletionProposal> result = new DefaultCompletionProvider().apply(context);
		List<String> resultValues = result.stream().map(CompletionProposal::value).toList();

		// then
		assertEquals(expectedValues, resultValues);
	}

	static Stream<Arguments> applyWithEnumCommandOptionData() {
		return Stream.of(Arguments.of(null, "name", 'n', List.of("VALUE1", "VALUE2")),
				Arguments.of("", "name", 'n', List.of("VALUE1", "VALUE2")),
				Arguments.of("different", "name", 'n', List.of("VALUE1", "VALUE2")),
				Arguments.of("VA", "name", 'n', List.of("VALUE1", "VALUE2")),

				Arguments.of("--name=", "name", 'n', List.of("--name=VALUE1", "--name=VALUE2")),
				Arguments.of("-n=", "name", 'n', List.of("-n=VALUE1", "-n=VALUE2")),

				Arguments.of("--name=", null, 'n', List.of("VALUE1", "VALUE2")),
				Arguments.of("-n=", "name", ' ', List.of("VALUE1", "VALUE2")));
	}

}