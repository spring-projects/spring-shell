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
package org.springframework.shell.component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.shell.component.PathSearch.PathSearchContext;
import org.springframework.shell.style.PartsText;
import org.springframework.shell.style.PartsText.PartText;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSearchTests {

	static Stream<Arguments> testParts() {
		return Stream.of(
			Arguments.of("0", new int[] { 0 },
				Arrays.asList(PartText.of("0", true))),
			Arguments.of("01", new int[] { 0, 1 },
				Arrays.asList(
					PartText.of("0", true),
					PartText.of("1", true))),
			Arguments.of("012", new int[] { 0, 1, 2 },
				Arrays.asList(
					PartText.of("0", true),
					PartText.of("1", true),
					PartText.of("2", true))),
			Arguments.of("0123456789", new int[0],
				Arrays.asList(PartText.of("0123456789", false))),
			Arguments.of("0123456789", new int[] { 0 },
				Arrays.asList(
					PartText.of("0", true),
					PartText.of("123456789", false))),
			Arguments.of("0123456789", new int[] { 1 },
				Arrays.asList(
					PartText.of("0", false),
					PartText.of("1", true),
					PartText.of("23456789", false))),
			Arguments.of("0123456789", new int[] { 9 },
				Arrays.asList(
					PartText.of("012345678", false),
					PartText.of("9", true))),
			Arguments.of("0123456789", new int[] { 2, 5 },
				Arrays.asList(
					PartText.of("01", false),
					PartText.of("2", true),
					PartText.of("34", false),
					PartText.of("5", true),
					PartText.of("6789", false))),
			Arguments.of("0123456789", new int[] { 2, 3 },
				Arrays.asList(
					PartText.of("01", false),
					PartText.of("2", true),
					PartText.of("3", true),
					PartText.of("456789", false))),
			Arguments.of("0123456789", new int[] { 8, 9 },
				Arrays.asList(
					PartText.of("01234567", false),
					PartText.of("8", true),
					PartText.of("9", true))),
			Arguments.of("spring-shell-core/build/test-results/test/TEST-org.springframework.shell.support.search.FuzzyMatchV2SearchMatchAlgorithmTests.xml", new int[] { 13, 33, 59, 67, 73 },
				Arrays.asList(
					PartText.of("spring-shell-", false),
					PartText.of("c", true),
					PartText.of("ore/build/test-resu", false),
					PartText.of("l", true),
					PartText.of("ts/test/TEST-org.springfr", false),
					PartText.of("a", true),
					PartText.of("mework.", false),
					PartText.of("s", true),
					PartText.of("hell.", false),
					PartText.of("s", true),
					PartText.of("upport.search.FuzzyMatchV2SearchMatchAlgorithmTests.xml", false)))
			);
	}

	@ParameterizedTest
	@MethodSource
	void testParts(String text, int[] positions, List<PartText> parts) {
		PartsText partsText = PathSearchContext.ofPositions(text, positions);
		List<PartText> res = partsText.getParts();
		assertThat(res).hasSize(parts.size());
		for (int i = 0; i < parts.size(); i++) {
			assertThat(res.get(i).getText()).isEqualTo(parts.get(i).getText());
			assertThat(res.get(i).isMatch()).isEqualTo(parts.get(i).isMatch());
		}
	}
}
