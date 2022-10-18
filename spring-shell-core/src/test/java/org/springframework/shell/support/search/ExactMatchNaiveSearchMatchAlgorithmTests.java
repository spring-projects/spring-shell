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
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_CAMEL123;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_CONSECUTIVE;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.BONUS_FIRST_CHAR_MULTIPLIER;
import static org.springframework.shell.support.search.AbstractSearchMatchAlgorithm.SCORE_MATCH;

class ExactMatchNaiveSearchMatchAlgorithmTests {

	static Stream<Arguments> testExactMatch() {
		return Stream.of(
			Arguments.of(true, false, true, "fooBarbaz", "oBA", false, -1, -1, new int[] {},
				0),
			Arguments.of(true, false, true, "fooBarbaz", "fooBarbazz", false, -1, -1, new int[] {},
				0),
			Arguments.of(false, false, true, "fooBarbaz", "oBA", false, 2, 5, new int[] { 2, 3, 4 },
				SCORE_MATCH * 3 + BONUS_CAMEL123 + BONUS_CONSECUTIVE),
			Arguments.of(false, false, true, "/AutomatorDocument.icns", "rdoc", false, 9, 13, new int[] { 9, 10, 11, 12 },
				SCORE_MATCH * 4 + BONUS_CAMEL123 + BONUS_CONSECUTIVE * 2),
			Arguments.of(false, false, true, "/man1/zshcompctl.1", "zshc", false, 6, 10, new int[] { 6, 7, 8, 9 },
				SCORE_MATCH * 4 + BONUS_BOUNDARY_DELIMITER * (BONUS_FIRST_CHAR_MULTIPLIER + 3)),
			Arguments.of(false, false, true, "/.oh-my-zsh/cache", "zsh/c", false, 8, 13, new int[] { 8, 9, 10, 11, 12 },
				SCORE_MATCH * 5 + BONUS_BOUNDARY * (BONUS_FIRST_CHAR_MULTIPLIER + 3) + BONUS_BOUNDARY_DELIMITER),
			Arguments.of(false, false, true, "fooBarbaz", "o", false, 1, 2, new int[] { 1 },
				SCORE_MATCH * 1),
			Arguments.of(false, false, true, "/tmp/test/11/file11.txt", "e", false, 6, 7, new int[] { 6 },
				SCORE_MATCH * 1),
			Arguments.of(false, true, true, "Só Danço Samba", "So", false, 0, 2, new int[] { 0, 1 },
				62),
			Arguments.of(false, true, true, "Danço", "danco", false, 0, 5, new int[] { 0, 1, 2, 3, 4 },
				140)
			);
	}

	@ParameterizedTest
	@MethodSource
	void testExactMatch(boolean caseSensitive, boolean normalize, boolean forward, String text, String pattern,
			boolean withPos, int start, int end, int[] positions, int score) {
		if (!caseSensitive) {
			pattern = pattern.toLowerCase();
		}
		ExactMatchNaiveSearchMatchAlgorithm searchMatch = new ExactMatchNaiveSearchMatchAlgorithm();
		SearchMatchResult result = searchMatch.match(caseSensitive, normalize, forward, text, pattern);
		assertThat(result.getStart()).isEqualTo(start);
		assertThat(result.getEnd()).isEqualTo(end);
		assertThat(result.getScore()).isEqualTo(score);
		assertThat(result.getPositions()).containsExactly(positions);
	}
}
