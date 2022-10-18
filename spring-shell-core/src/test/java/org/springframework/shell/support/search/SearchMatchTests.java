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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchMatchTests {

	@Test
	void testAlgoType() {
		SearchMatch searchMatch = SearchMatch.builder()
				.caseSensitive(true)
				.normalize(true)
				.forward(true)
				.build();
		assertThat(searchMatch).isNotNull();
		SearchMatchResult result = null;

		result = searchMatch.match("fake", "fake");
		assertThat(result.getAlgorithm()).isInstanceOf(FuzzyMatchV2SearchMatchAlgorithm.class);

		result = searchMatch.match("fake", "'fake");
		assertThat(result.getAlgorithm()).isInstanceOf(ExactMatchNaiveSearchMatchAlgorithm.class);
	}
}
