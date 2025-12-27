package org.springframework.shell.jline;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.completion.CompletionProposal;
import org.springframework.shell.core.command.completion.CompletionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandCompleterTest {

	private final CommandRegistry registry = new CommandRegistry();

	private CommandCompleter completer;

	private final CompletionProvider completionProvider = completionContext -> {
		CommandOption option = completionContext.getCommandOption();
		if (option == null) {
			return Collections.emptyList();
		}

		String word = completionContext.getWords().get(completionContext.getWords().size() - 1);
		if (word.contains("=")) {
			word = word.substring(0, word.indexOf('='));
		}
		String prefix = word.isEmpty() ? word : word + "=";

		Stream<String> options = Stream.empty();

		if ("first".equals(option.longName())) {
			options = Stream.of("Peter", "Paul", "Mary");
		}
		else if ("last".equals(option.longName())) {
			options = Stream.of("Chan", "Noris");
		}

		return options.map(str -> prefix + str).map(CompletionProposal::new).toList();
	};

	@BeforeEach
	public void before() {
		Command command = mock(Command.class);
		when(command.getOptions())
			.thenReturn(List.of(new CommandOption.Builder().longName("first").shortName('f').build(),
					new CommandOption.Builder().longName("last").shortName('l').build()));
		when(command.getName()).thenReturn("hello");
		when(command.getCompletionProvider()).thenReturn(completionProvider);
		registry.registerCommand(command);

		completer = new CommandCompleter(registry);
	}

	private List<String> toCandidateNames(List<Candidate> candidates) {
		return candidates.stream().map(Candidate::value).toList();
	}

	@ParameterizedTest
	@MethodSource("testCompleteData")
	public void testComplete(List<String> words, List<String> expectedValues) {
		// given
		List<Candidate> candidates = new ArrayList<>();
		ParsedLine line = mock(ParsedLine.class);
		when(line.words()).thenReturn(words);
		when(line.word()).thenReturn(words.get(words.size() - 1));
		when(line.line()).thenReturn(String.join(" ", words));

		// when
		completer.complete(mock(LineReader.class), line, candidates);

		// then
		assertEquals(expectedValues, toCandidateNames(candidates));
	}

	static Stream<Arguments> testCompleteData() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello")), Arguments.of(List.of("he"), List.of("hello")),
				Arguments.of(List.of("he", ""), List.of("hello")),

				Arguments.of(List.of("hello"), List.of("--first", "-f", "--last", "-l")),
				Arguments.of(List.of("hello", ""), List.of("--first", "-f", "--last", "-l")),

				Arguments.of(List.of("hello", "--"), List.of("--first", "-f", "--last", "-l")),
				Arguments.of(List.of("hello", "-"), List.of("--first", "-f", "--last", "-l")),
				Arguments.of(List.of("hello", "--fi"), List.of("--first", "-f", "--last", "-l")),
				Arguments.of(List.of("hello", "--la"), List.of("--first", "-f", "--last", "-l")),

				Arguments.of(List.of("hello", "-f"), List.of("-f=Peter", "-f=Paul", "-f=Mary")),
				Arguments.of(List.of("hello", "-f="), List.of("-f=Peter", "-f=Paul", "-f=Mary")),
				Arguments.of(List.of("hello", "-f=Pe"), List.of("-f=Peter", "-f=Paul", "-f=Mary")),
				Arguments.of(List.of("hello", "-f=Pe", ""), List.of("--last", "-l")),

				Arguments.of(List.of("hello", "--first"), List.of("--first=Peter", "--first=Paul", "--first=Mary")),
				Arguments.of(List.of("hello", "--first="), List.of("--first=Peter", "--first=Paul", "--first=Mary")),
				Arguments.of(List.of("hello", "--first=Pe"), List.of("--first=Peter", "--first=Paul", "--first=Mary")),
				Arguments.of(List.of("hello", "--first=Pe", ""), List.of("--last", "-l")),

				Arguments.of(List.of("hello", "-f", ""), List.of("Peter", "Paul", "Mary")),
				Arguments.of(List.of("hello", "--first", ""), List.of("Peter", "Paul", "Mary")),

				Arguments.of(List.of("hello", "-f", "Pe"), List.of("--last", "-l")),
				Arguments.of(List.of("hello", "--first", "Pe"), List.of("--last", "-l")),

				Arguments.of(List.of("hello", "-l"), List.of("-l=Chan", "-l=Noris")),
				Arguments.of(List.of("hello", "-l="), List.of("-l=Chan", "-l=Noris")),
				Arguments.of(List.of("hello", "-l=No"), List.of("-l=Chan", "-l=Noris")),
				Arguments.of(List.of("hello", "-l=No", ""), List.of("--first", "-f")),

				Arguments.of(List.of("hello", "--last"), List.of("--last=Chan", "--last=Noris")),
				Arguments.of(List.of("hello", "--last="), List.of("--last=Chan", "--last=Noris")),
				Arguments.of(List.of("hello", "--last=No"), List.of("--last=Chan", "--last=Noris")),
				Arguments.of(List.of("hello", "--last=No", ""), List.of("--first", "-f")),

				Arguments.of(List.of("hello", "-l", ""), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "--last", ""), List.of("Chan", "Noris")),

				Arguments.of(List.of("hello", "-l", "No"), List.of("--first", "-f")),
				Arguments.of(List.of("hello", "--last", "No"), List.of("--first", "-f")),

				Arguments.of(List.of("hello", "--first", "Paul", "--last", "Noris"), List.of()),
				Arguments.of(List.of("hello", "--first", "Paul", "-l", "Noris"), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "--last", "Noris"), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "-l", "Noris"), List.of()),

				Arguments.of(List.of("hello", "--first=Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first=Paul", "-l=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "-l=Noris", ""), List.of()),

				Arguments.of(List.of("hello", "--first=Paul", "--last", "Noris"), List.of()),
				Arguments.of(List.of("hello", "--first=Paul", "-l", "Noris"), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "--last", "Noris"), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "-l", "Noris"), List.of()),

				Arguments.of(List.of("hello", "--first", "Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first", "Paul", "-l=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "-l=Noris", ""), List.of()));
	}

}