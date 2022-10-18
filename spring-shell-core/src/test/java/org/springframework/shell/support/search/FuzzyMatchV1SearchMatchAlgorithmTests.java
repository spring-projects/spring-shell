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
package org.springframework.shell.support.search;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_BOUNDARY;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_BOUNDARY_DELIMITER;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_BOUNDARY_WHITE;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_CAMEL123;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_CONSECUTIVE;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_FIRST_CHAR_MULTIPLIER;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_NON_WORD;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.SCORE_GAP_EXTENSION;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.SCORE_GAP_START;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.SCORE_MATCH;

class FuzzyMatchV1SearchMatchAlgorithmTests {

	private final FuzzyMatchV1SearchMatchAlgorithm searchMatch = new FuzzyMatchV1SearchMatchAlgorithm();

	static Stream<Arguments> testFuzzyMatchV1() {
		return Stream.of(
			Arguments.of(false, false, "fooBarbaz1", "oBZ", true, 2, 9, new int[] { 2, 3, 8 },
				SCORE_MATCH * 3 + BONUS_CAMEL123 + SCORE_GAP_START + SCORE_GAP_EXTENSION * 3),
			Arguments.of(false, false, "foo bar baz", "fbb", true, 0, 9, new int[] { 0, 4, 8 },
				SCORE_MATCH * 3 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_WHITE * 2 +
				2 * SCORE_GAP_START + 4 * SCORE_GAP_EXTENSION),
			Arguments.of(false, false, "/AutomatorDocument.icns", "rdoc", true, 9, 13, new int[] { 9, 10, 11, 12 },
				SCORE_MATCH * 4 + BONUS_CAMEL123 + BONUS_CONSECUTIVE * 2),
			Arguments.of(false, false, "/man1/zshcompctl.1", "zshc", true, 6, 10, new int[] { 6, 7, 8, 9 },
				SCORE_MATCH * 4 + BONUS_BOUNDARY_DELIMITER * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_DELIMITER * 3),
			Arguments.of(false, false, "/.oh-my-zsh/cache", "zshc", true, 8, 13, new int[] { 8, 9, 10 ,12 },
				SCORE_MATCH * 4 + BONUS_BOUNDARY * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY * 2 + SCORE_GAP_START + BONUS_BOUNDARY_DELIMITER),
			Arguments.of(false, false, "ab0123 456", "12356", true, 3, 10, new int[] { 3, 4, 5, 8, 9 },
				SCORE_MATCH * 5 + BONUS_CONSECUTIVE * 3 + SCORE_GAP_START + SCORE_GAP_EXTENSION),
			Arguments.of(false, false, "abc123 456", "12356", true, 3, 10, new int[] { 3, 4, 5, 8, 9 },
				SCORE_MATCH * 5 + BONUS_CAMEL123 * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_CAMEL123 * 2 + BONUS_CONSECUTIVE + SCORE_GAP_START + SCORE_GAP_EXTENSION),
			Arguments.of(false, false, "foo/bar/baz", "fbb", true, 0, 9, new int[] { 0, 4, 8 },
				SCORE_MATCH * 3 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_DELIMITER * 2 + SCORE_GAP_START * 2 + SCORE_GAP_EXTENSION * 4),
			Arguments.of(false, false, "fooBarBaz", "fbb", true, 0, 7, new int[] { 0, 3, 6 },
				SCORE_MATCH * 3 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_CAMEL123 * 2 + SCORE_GAP_START * 2 + SCORE_GAP_EXTENSION * 2),
			Arguments.of(false, false, "foo barbaz", "fbb", true, 0, 8, new int[] { 0, 4, 7 },
				SCORE_MATCH * 3 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_WHITE +SCORE_GAP_START * 2 + SCORE_GAP_EXTENSION * 3),
			Arguments.of(false, false, "fooBar Baz", "foob", true, 0, 4, new int[] { 0, 1, 2, 3 },
				SCORE_MATCH * 4 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_WHITE * 3),
			Arguments.of(false, false, "xFoo-Bar Baz", "foo-b", true, 1, 6, new int[] { 1, 2, 3, 4, 5 },
				SCORE_MATCH * 5 + BONUS_CAMEL123 * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_CAMEL123 * 2 + BONUS_NON_WORD + BONUS_BOUNDARY),
			Arguments.of(true, false, "fooBarbaz", "oBz", true, 2, 9, new int[] { 2, 3, 8 },
				SCORE_MATCH * 3 + BONUS_CAMEL123 + SCORE_GAP_START + SCORE_GAP_EXTENSION * 3),
			Arguments.of(true, false, "Foo/Bar/Baz", "FBB", true, 0, 9, new int[] { 0, 4, 8 },
				SCORE_MATCH * 3 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_DELIMITER * 2 + SCORE_GAP_START * 2 + SCORE_GAP_EXTENSION * 4),
			Arguments.of(true, false, "FooBarBaz", "FBB", true, 0, 7, new int[] { 0, 3, 6 },
				SCORE_MATCH * 3 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_CAMEL123 * 2 + SCORE_GAP_START * 2 + SCORE_GAP_EXTENSION * 2),
			Arguments.of(true, false, "FooBar Baz", "FooB", true, 0, 4, new int[] { 0, 1, 2, 3 },
				SCORE_MATCH * 4 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_WHITE * 2 + Math.max(BONUS_CAMEL123, BONUS_BOUNDARY_WHITE)),
			Arguments.of(true, false, "foo-bar", "o-ba", true, 2, 6, new int[] { 2, 3, 4, 5 },
				SCORE_MATCH * 4 + BONUS_BOUNDARY * 3),
			Arguments.of(true, false, "fooBarbaz", "oBZ", true, -1, -1, new int[0],
				0),
			Arguments.of(true, false, "Foo Bar Baz", "fbb", true, -1, -1, new int[0],
				0),
			Arguments.of(true, false, "fooBarbaz", "fooBarbazz", true, -1, -1, new int[0],
				0)
			);
	}

	@ParameterizedTest
	@MethodSource
	void testFuzzyMatchV1(boolean caseSensitive, boolean normalize, String text, String pattern, boolean withPos,
			int start, int end, int[] positions, int score) {
		if (!caseSensitive) {
			pattern = pattern.toLowerCase();
		}
		SearchMatchResult result;

		result = searchMatch.match(caseSensitive, normalize, true, text, pattern);
		assertThat(result.getStart()).isEqualTo(start);
		assertThat(result.getEnd()).isEqualTo(end);
		assertThat(result.getScore()).isEqualTo(score);
		assertThat(result.getPositions()).containsExactly(positions);

		result = searchMatch.match(caseSensitive, normalize, false, text, pattern);
		assertThat(result.getStart()).isEqualTo(start);
		assertThat(result.getEnd()).isEqualTo(end);
		assertThat(result.getScore()).isEqualTo(score);
		assertThat(result.getPositions()).containsExactly(positions);
	}


	static Stream<Arguments> testFuzzyMatchV1Backward() {
		return Stream.of(
			Arguments.of(false, true, "foobar fb", "fb", 0, 4, new int[] { 0, 3 },
				SCORE_MATCH * 2 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + SCORE_GAP_START + SCORE_GAP_EXTENSION),
			Arguments.of(false, false, "foobar fb", "fb", 7, 9, new int[] { 7, 8 },
				SCORE_MATCH * 2 + BONUS_BOUNDARY_WHITE * BONUS_FIRST_CHAR_MULTIPLIER + BONUS_BOUNDARY_WHITE)
			);
	}

	@ParameterizedTest
	@MethodSource
    void testFuzzyMatchV1Backward(boolean caseSensitive, boolean forward, String text, String pattern, int start,
            int end, int[] positions, int score) {
		if (!caseSensitive) {
			pattern = pattern.toLowerCase();
		}
		SearchMatchResult result;

		result = searchMatch.match(caseSensitive, false, forward, text, pattern);
		assertThat(result.getStart()).isEqualTo(start);
		assertThat(result.getEnd()).isEqualTo(end);
		assertThat(result.getScore()).isEqualTo(score);
		assertThat(result.getPositions()).containsExactly(positions);
	}

}
