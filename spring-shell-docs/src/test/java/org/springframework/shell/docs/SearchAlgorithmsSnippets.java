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
package org.springframework.shell.docs;

import org.springframework.shell.support.search.SearchMatch;
import org.springframework.shell.support.search.SearchMatchResult;

class SearchAlgorithmsSnippets {

	@SuppressWarnings("unused")
	void dump1() {
		// tag::builder[]
		SearchMatch searchMatch = SearchMatch.builder()
			.caseSensitive(false)
			.normalize(false)
			.forward(true)
			.build();
		// end::builder[]
	}

	void dump2() {
		// tag::simple[]
		SearchMatch searchMatch = SearchMatch.builder()
			.caseSensitive(false)
			.normalize(false)
			.forward(true)
			.build();

		SearchMatchResult result = searchMatch.match("foo bar baz", "fbb");

		result.getStart();
		// 0 - start position inclusive
		result.getEnd();
		// 9 - end position exclusive
		result.getPositions();
		// 0,4,8 - positions, inclusive
		result.getScore();
		// 112 - score
		result.getAlgorithm();
		// FuzzyMatchV2SearchMatchAlgorithm - resolved algo
		// end::simple[]
	}

}
