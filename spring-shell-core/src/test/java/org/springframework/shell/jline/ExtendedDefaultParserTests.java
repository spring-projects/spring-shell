/*
 * Copyright 2023 the original author or authors.
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

import java.util.stream.Stream;

import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtendedDefaultParserTests {

	private final ExtendedDefaultParser springParser = new ExtendedDefaultParser();
	private final DefaultParser jlineParser = new DefaultParser();

	static Stream<Arguments> args() {
		// cursor words wordIndex wordCursor line
		// We've left these tests here to show issues
		// differences between DefaultParser and
		// ExtendedDefaultParser. relates gh517
		return Stream.of(
			Arguments.of(0, 1, 0, 0, "one"),
			Arguments.of(3, 1, 0, 3, "one "),
			Arguments.of(4, 2, 1, 0, "one "),

			Arguments.of(0, 2, 0, 0, "one two"),
			Arguments.of(1, 2, 0, 1, "one two"),
			Arguments.of(2, 2, 0, 2, "one two"),
			Arguments.of(3, 2, 0, 3, "one two"),
			Arguments.of(7, 2, 1, 3, "one two"),
			Arguments.of(7, 2, 1, 3, "one two "),
			Arguments.of(8, 3, 2, 0, "one two "),

			Arguments.of(0, 1, 0, 0, "'one'"),
			// Arguments.of(5, 1, 0, 3, "'one' "),
			Arguments.of(6, 2, 1, 0, "'one' "),

			Arguments.of(0, 1, 0, 0, "'one'"),
			Arguments.of(1, 1, 0, 0, "'one'"),
			Arguments.of(2, 1, 0, 1, "'one'"),
			Arguments.of(3, 1, 0, 2, "'one'"),
			Arguments.of(4, 1, 0, 3, "'one'"),
			// Arguments.of(5, 1, 0, 3, "'one'"),

			Arguments.of(0, 1, 0, 0, "'one' "),
			Arguments.of(1, 1, 0, 0, "'one' "),
			Arguments.of(2, 1, 0, 1, "'one' "),
			Arguments.of(3, 1, 0, 2, "'one' "),
			Arguments.of(4, 1, 0, 3, "'one' "),
			// Arguments.of(5, 1, 0, 3, "'one' "),
			Arguments.of(6, 2, 1, 0, "'one' "),

			Arguments.of(0, 1, 0, 0, "\"one\""),
			Arguments.of(1, 1, 0, 0, "\"one\""),
			Arguments.of(2, 1, 0, 1, "\"one\""),
			Arguments.of(3, 1, 0, 2, "\"one\""),
			Arguments.of(4, 1, 0, 3, "\"one\""),
			// Arguments.of(5, 1, 0, 3, "\"one\""),

			Arguments.of(0, 1, 0, 0, "\"one\" "),
			Arguments.of(1, 1, 0, 0, "\"one\" "),
			Arguments.of(2, 1, 0, 1, "\"one\" "),
			Arguments.of(3, 1, 0, 2, "\"one\" "),
			Arguments.of(4, 1, 0, 3, "\"one\" "),
			// Arguments.of(5, 1, 0, 3, "\"one\" "),
			Arguments.of(6, 2, 1, 0, "\"one\" ")
			);
	}

	@ParameterizedTest
	@MethodSource("args")
	void testSpringExtendedDefaultParser(int cursor, int words, int wordIndex, int wordCursor, String line) {
		ParsedLine parse = springParser.parse(line, cursor);
		assertThat(parse.words()).as("words").hasSize(words);
		assertThat(parse.wordIndex()).as("wordIndex").isEqualTo(wordIndex);
		assertThat(parse.wordCursor()).as("wordCursor").isEqualTo(wordCursor);
	}

	@ParameterizedTest
	@MethodSource("args")
	void testJlineDefaultParser(int cursor, int words, int wordIndex, int wordCursor, String line) {
		ParsedLine parse = jlineParser.parse(line, cursor);
		assertThat(parse.words()).as("words").hasSize(words);
		assertThat(parse.wordIndex()).as("wordIndex").isEqualTo(wordIndex);
		assertThat(parse.wordCursor()).as("wordCursor").isEqualTo(wordCursor);
	}
}
