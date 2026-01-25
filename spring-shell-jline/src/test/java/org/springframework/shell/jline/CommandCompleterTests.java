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
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David Pilar
 */
class CommandCompleterTests {

	private CommandCompleter completer;

	private Command command;

	private final CompletionProvider completionProvider = completionContext -> {
		CommandOption option = completionContext.getCommandOption();
		if (option == null) {
			return Collections.emptyList();
		}

		String word = completionContext.getWords().get(completionContext.getWords().size() - 1);
		String prefix = word.contains("=") ? word.substring(0, word.indexOf('=') + 1) : "";

		Stream<String> options = Stream.empty();

		if ("first".equals(option.longName()) || 'f' == option.shortName()) {
			options = Stream.of("Mary", "Paul", "Peter");
		}
		else if ("last".equals(option.longName()) || 'l' == option.shortName()) {
			options = Stream.of("Chan", "Noris");
		}

		return options.map(str -> new CompletionProposal(prefix + str).displayText(str)).toList();
	};

	@BeforeEach
	void before() {
		command = mock(Command.class);
		when(command.getName()).thenReturn("hello");
		when(command.getDescription()).thenReturn("Says Hello.");
		when(command.getCompletionProvider()).thenReturn(completionProvider);

		completer = new CommandCompleter(new CommandRegistry(Set.of(command)));
	}

	private List<String> toCandidateNames(List<Candidate> candidates) {
		return candidates.stream().map(Candidate::value).sorted().toList();
	}

	private List<String> toCandidateDisplayText(List<Candidate> candidates) {
		return candidates.stream().map(Candidate::displ).sorted().toList();
	}

	@ParameterizedTest
	@MethodSource("completeData")
	void testComplete(List<String> words, List<String> expectedValues) {
		// given
		when(command.getOptions())
			.thenReturn(List.of(new CommandOption.Builder().longName("first").shortName('f').build(),
					new CommandOption.Builder().longName("last").shortName('l').build()));

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

	static Stream<Arguments> completeData() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello")), Arguments.of(List.of("he"), List.of("hello")),
				Arguments.of(List.of("he", ""), List.of()), Arguments.of(List.of("hello"), List.of("hello")),

				Arguments.of(List.of("hello", ""), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "--"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "-"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "--fi"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "--la"), List.of("--first", "--last", "-f", "-l")),

				Arguments.of(List.of("hello", "-f"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "-f="), List.of("-f=Mary", "-f=Paul", "-f=Peter")),
				Arguments.of(List.of("hello", "-f=Pe"), List.of("-f=Mary", "-f=Paul", "-f=Peter")),
				Arguments.of(List.of("hello", "-f=Pe", ""), List.of("--last", "-l")),

				Arguments.of(List.of("hello", "--first"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "--first="), List.of("--first=Mary", "--first=Paul", "--first=Peter")),
				Arguments.of(List.of("hello", "--first=Pe"), List.of("--first=Mary", "--first=Paul", "--first=Peter")),
				Arguments.of(List.of("hello", "--first=Pe", ""), List.of("--last", "-l")),

				Arguments.of(List.of("hello", "-f", ""), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "--first", ""), List.of("Mary", "Paul", "Peter")),

				Arguments.of(List.of("hello", "-f", "Pe"), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "-f", "Pe", ""), List.of("--last", "-l")),
				Arguments.of(List.of("hello", "--first", "Pe"), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "--first", "Pe", ""), List.of("--last", "-l")),

				Arguments.of(List.of("hello", "-l"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "-l="), List.of("-l=Chan", "-l=Noris")),
				Arguments.of(List.of("hello", "-l=No"), List.of("-l=Chan", "-l=Noris")),
				Arguments.of(List.of("hello", "-l=No", ""), List.of("--first", "-f")),

				Arguments.of(List.of("hello", "--last"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello", "--last="), List.of("--last=Chan", "--last=Noris")),
				Arguments.of(List.of("hello", "--last=No"), List.of("--last=Chan", "--last=Noris")),
				Arguments.of(List.of("hello", "--last=No", ""), List.of("--first", "-f")),

				Arguments.of(List.of("hello", "-l", ""), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "--last", ""), List.of("Chan", "Noris")),

				Arguments.of(List.of("hello", "-l", "No"), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "-l", "No", ""), List.of("--first", "-f")),
				Arguments.of(List.of("hello", "--last", "No"), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "--last", "No", ""), List.of("--first", "-f")),

				Arguments.of(List.of("hello", "--first", "Paul", "--last", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first", "Paul", "-l", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "--last", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "-l", "Noris", ""), List.of()),

				Arguments.of(List.of("hello", "--first=Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first=Paul", "-l=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "-l=Noris", ""), List.of()),

				Arguments.of(List.of("hello", "--first=Paul", "--last", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first=Paul", "-l", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "--last", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "-l", "Noris", ""), List.of()),

				Arguments.of(List.of("hello", "--first", "Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first", "Paul", "-l=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "-l=Noris", ""), List.of()));
	}

	@ParameterizedTest
	@MethodSource("completeCommandWithLongNamesData")
	void testCompleteCommandWithLongNames(List<String> words, List<String> expectedValues) {
		// given
		when(command.getOptions()).thenReturn(List.of(new CommandOption.Builder().longName("first").build(),
				new CommandOption.Builder().longName("last").build()));

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

	static Stream<Arguments> completeCommandWithLongNamesData() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello")), Arguments.of(List.of("he"), List.of("hello")),
				Arguments.of(List.of("he", ""), List.of()), Arguments.of(List.of("hello"), List.of("hello")),

				Arguments.of(List.of("hello", ""), List.of("--first", "--last")),
				Arguments.of(List.of("hello", "--"), List.of("--first", "--last")),
				Arguments.of(List.of("hello", "-"), List.of("--first", "--last")),
				Arguments.of(List.of("hello", "--fi"), List.of("--first", "--last")),
				Arguments.of(List.of("hello", "--la"), List.of("--first", "--last")),

				Arguments.of(List.of("hello", "--first"), List.of("--first", "--last")),
				Arguments.of(List.of("hello", "--first", ""), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "--first", "Pe"), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "--first", "Pe", ""), List.of("--last")),

				Arguments.of(List.of("hello", "--first="), List.of("--first=Mary", "--first=Paul", "--first=Peter")),
				Arguments.of(List.of("hello", "--first=Pe"), List.of("--first=Mary", "--first=Paul", "--first=Peter")),
				Arguments.of(List.of("hello", "--first=Pe", ""), List.of("--last")),

				Arguments.of(List.of("hello", "--last"), List.of("--first", "--last")),
				Arguments.of(List.of("hello", "--last", ""), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "--last", "No"), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "--last", "No", ""), List.of("--first")),

				Arguments.of(List.of("hello", "--last="), List.of("--last=Chan", "--last=Noris")),
				Arguments.of(List.of("hello", "--last=No"), List.of("--last=Chan", "--last=Noris")),
				Arguments.of(List.of("hello", "--last=No", ""), List.of("--first")),

				Arguments.of(List.of("hello", "--first", "Paul", "--last", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first=Paul", "--last=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first=Paul", "--last", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "--first", "Paul", "--last=Noris", ""), List.of()));
	}

	@ParameterizedTest
	@MethodSource("completeCommandWithShortNamesData")
	void testCompleteCommandWithShortNames(List<String> words, List<String> expectedValues) {
		// given
		when(command.getOptions()).thenReturn(List.of(new CommandOption.Builder().shortName('f').build(),
				new CommandOption.Builder().shortName('l').build()));

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

	static Stream<Arguments> completeCommandWithShortNamesData() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello")), Arguments.of(List.of("he"), List.of("hello")),
				Arguments.of(List.of("he", ""), List.of()), Arguments.of(List.of("hello"), List.of("hello")),

				Arguments.of(List.of("hello", ""), List.of("-f", "-l")),
				Arguments.of(List.of("hello", "--"), List.of("-f", "-l")),
				Arguments.of(List.of("hello", "-"), List.of("-f", "-l")),

				Arguments.of(List.of("hello", "-f"), List.of("-f", "-l")),
				Arguments.of(List.of("hello", "-f", ""), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "-f", "Pe"), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "-f", "Pe", ""), List.of("-l")),

				Arguments.of(List.of("hello", "-f="), List.of("-f=Mary", "-f=Paul", "-f=Peter")),
				Arguments.of(List.of("hello", "-f=Pe"), List.of("-f=Mary", "-f=Paul", "-f=Peter")),
				Arguments.of(List.of("hello", "-f=Pe", ""), List.of("-l")),

				Arguments.of(List.of("hello", "-l"), List.of("-f", "-l")),
				Arguments.of(List.of("hello", "-l", ""), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "-l", "No"), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "-l", "No", ""), List.of("-f")),

				Arguments.of(List.of("hello", "-l="), List.of("-l=Chan", "-l=Noris")),
				Arguments.of(List.of("hello", "-l=No"), List.of("-l=Chan", "-l=Noris")),
				Arguments.of(List.of("hello", "-l=No", ""), List.of("-f")),

				Arguments.of(List.of("hello", "-f", "Paul", "-l", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "-l=Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f=Paul", "-l", "Noris", ""), List.of()),
				Arguments.of(List.of("hello", "-f", "Paul", "-l=Noris", ""), List.of()));
	}

	@ParameterizedTest
	@MethodSource("completeWithSubCommandsData")
	void testCompleteWithSubCommands(List<String> words, List<String> expectedValues) {
		// given
		when(command.getName()).thenReturn("hello world");
		when(command.getOptions())
			.thenReturn(List.of(new CommandOption.Builder().longName("first").shortName('f').build(),
					new CommandOption.Builder().longName("last").shortName('l').build()));

		Command command2 = mock(Command.class);
		when(command2.getName()).thenReturn("hello country");
		when(command2.getOptions()).thenReturn(List.of());

		completer = new CommandCompleter(new CommandRegistry(Set.of(command, command2)));

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

	static Stream<Arguments> completeWithSubCommandsData() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello country", "hello world")),
				Arguments.of(List.of("he"), List.of("hello country", "hello world")),
				Arguments.of(List.of("he", ""), List.of()),
				Arguments.of(List.of("hello"), List.of("hello country", "hello world")),
				Arguments.of(List.of("hello wo"), List.of("hello world")),
				Arguments.of(List.of("hello co"), List.of("hello country")),

				Arguments.of(List.of("hello world"), List.of("hello world")),
				Arguments.of(List.of("hello world", ""), List.of("--first", "--last", "-f", "-l")),

				Arguments.of(List.of("hello world", "--"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello world", "-"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello world", "--fi"), List.of("--first", "--last", "-f", "-l")),
				Arguments.of(List.of("hello world", "--la"), List.of("--first", "--last", "-f", "-l")),

				Arguments.of(List.of("hello world", "--first", ""), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello world", "--last", ""), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello world", "--first", "Paul", "--last", "Noris", ""), List.of()));
	}

	@ParameterizedTest
	@MethodSource("completeWithTwoOptionsWhereOneIsSubsetOfOtherData")
	void testCompleteWithTwoOptionsWhereOneIsSubsetOfOther(List<String> words, List<String> expectedValues) {
		// given
		when(command.getOptions()).thenReturn(List.of(new CommandOption.Builder().longName("first").build(),
				new CommandOption.Builder().longName("firstname").build()));

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

	static Stream<Arguments> completeWithTwoOptionsWhereOneIsSubsetOfOtherData() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello")), Arguments.of(List.of("he"), List.of("hello")),
				Arguments.of(List.of("he", ""), List.of()), Arguments.of(List.of("hello"), List.of("hello")),

				Arguments.of(List.of("hello", ""), List.of("--first", "--firstname")),
				Arguments.of(List.of("hello", "--"), List.of("--first", "--firstname")),
				Arguments.of(List.of("hello", "-"), List.of("--first", "--firstname")),
				Arguments.of(List.of("hello", "--fi"), List.of("--first", "--firstname")),

				Arguments.of(List.of("hello", "--first=Peter", ""), List.of("--firstname")),
				Arguments.of(List.of("hello", "--first", "Peter", ""), List.of("--firstname")),
				Arguments.of(List.of("hello", "--first", "Peter"), List.of("Mary", "Paul", "Peter")),

				Arguments.of(List.of("hello", "--firstname=Peter", ""), List.of("--first")),
				Arguments.of(List.of("hello", "--firstname=Peter"), List.of()),
				Arguments.of(List.of("hello", "--firstname", "Peter", ""), List.of("--first")),
				Arguments.of(List.of("hello", "--firstname", "Peter"), List.of()),

				Arguments.of(List.of("hello", "--firstname=Peter", "--first=Paul", ""), List.of()),
				Arguments.of(List.of("hello", "--firstname=Peter", "--first", "Paul"),
						List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "--firstname", "Peter", "--first=Paul", ""), List.of()), Arguments
					.of(List.of("hello", "--firstname", "Peter", "--first", "Paul"), List.of("Mary", "Paul", "Peter")));
	}

	@ParameterizedTest
	@MethodSource("completeWithHiddenCommandsData")
	void testCompleteWithHiddenCommands(List<String> words, List<String> expectedValues) {
		// given
		when(command.getName()).thenReturn("hello visible");
		when(command.getOptions()).thenReturn(List.of());

		Command visibleCommand = mock(Command.class);
		when(visibleCommand.getName()).thenReturn("hello shown");
		when(visibleCommand.getOptions()).thenReturn(List.of());

		Command hiddenCommand = mock(Command.class);
		when(hiddenCommand.getName()).thenReturn("hello hidden");
		when(hiddenCommand.getOptions()).thenReturn(List.of());
		when(hiddenCommand.isHidden()).thenReturn(true);

		completer = new CommandCompleter(new CommandRegistry(Set.of(command, visibleCommand, hiddenCommand)));

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

	static Stream<Arguments> completeWithHiddenCommandsData() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello shown", "hello visible")),
				Arguments.of(List.of("he"), List.of("hello shown", "hello visible")),
				Arguments.of(List.of("he", ""), List.of()),
				Arguments.of(List.of("hello"), List.of("hello shown", "hello visible")),
				Arguments.of(List.of("hello vi"), List.of("hello visible")),
				Arguments.of(List.of("hello hi"), List.of()));
	}

	@ParameterizedTest
	@MethodSource("completeForProposalDisplayText")
	void testCompleteForProposalDisplayText(List<String> words, List<String> expectedValues) {
		// given
		when(command.getOptions())
			.thenReturn(List.of(new CommandOption.Builder().longName("first").shortName('f').build(),
					new CommandOption.Builder().longName("last").shortName('l').build()));

		List<Candidate> candidates = new ArrayList<>();
		ParsedLine line = mock(ParsedLine.class);
		when(line.words()).thenReturn(words);
		when(line.word()).thenReturn(words.get(words.size() - 1));
		when(line.line()).thenReturn(String.join(" ", words));

		// when
		completer.complete(mock(LineReader.class), line, candidates);

		// then
		assertEquals(expectedValues, toCandidateDisplayText(candidates));
	}

	static Stream<Arguments> completeForProposalDisplayText() {
		return Stream.of(Arguments.of(List.of(""), List.of("hello: Says Hello.")),

				Arguments.of(List.of("hello"), List.of("hello: Says Hello.")),
				Arguments.of(List.of("hello", ""), List.of("--first", "--last", "-f", "-l")),

				Arguments.of(List.of("hello", "-f="), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "--first="), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "-l="), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "--last="), List.of("Chan", "Noris")),

				Arguments.of(List.of("hello", "-f", ""), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "--first", ""), List.of("Mary", "Paul", "Peter")),
				Arguments.of(List.of("hello", "-l", ""), List.of("Chan", "Noris")),
				Arguments.of(List.of("hello", "--last", ""), List.of("Chan", "Noris")));
	}

	@ParameterizedTest
	@MethodSource("completeForCommandAlias")
	void testCompleteForCommandAlias(List<String> words, List<String> expectedValues) {
		// given
		when(command.getAliases()).thenReturn(List.of("hi", "bye"));

		List<Candidate> candidates = new ArrayList<>();
		ParsedLine line = mock(ParsedLine.class);
		when(line.words()).thenReturn(words);
		when(line.word()).thenReturn(words.get(words.size() - 1));
		when(line.line()).thenReturn(String.join(" ", words));

		// when
		completer.complete(mock(LineReader.class), line, candidates);

		// then
		assertEquals(expectedValues, toCandidateDisplayText(candidates));
	}

	static Stream<Arguments> completeForCommandAlias() {
		return Stream.of(
				Arguments.of(List.of(""), List.of("bye: Says Hello.", "hello: Says Hello.", "hi: Says Hello.")),

				Arguments.of(List.of("h"), List.of("hello: Says Hello.", "hi: Says Hello.")),
				Arguments.of(List.of("he"), List.of("hello: Says Hello.")),
				Arguments.of(List.of("hello"), List.of("hello: Says Hello.")),
				Arguments.of(List.of("hi"), List.of("hi: Says Hello.")),
				Arguments.of(List.of("b"), List.of("bye: Says Hello.")),
				Arguments.of(List.of("bye"), List.of("bye: Says Hello.")),

				Arguments.of(List.of("hello", ""), List.of()), Arguments.of(List.of("hi", ""), List.of()),
				Arguments.of(List.of("bye", ""), List.of()));
	}

}