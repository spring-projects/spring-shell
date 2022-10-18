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

/**
 * Interface defining result used in {@link SearchMatch}.
 *
 * @author Janne Valkealahti
 */
public interface SearchMatchResult {

	/**
	 * Get start of a match.
	 *
	 * @return a start of a match
	 */
	int getStart();

	/**
	 * Get end of a match.
	 *
	 * @return a end of a matchh
	 */
	int getEnd();

	/**
	 * Get score of a match.
	 *
	 * @return a score of a match
	 */
	int getScore();

	/**
	 * Get positions of a match.
	 *
	 * @return a positions of a match
	 */
	int[] getPositions();

	/**
	 * Get {@link SearchMatchAlgorithm} handling a search.
	 *
	 * @return {@link SearchMatchAlgorithm} handling a search
	 */
	SearchMatchAlgorithm getAlgorithm();

	/**
	 * Construct {@link SearchMatchResult} with given parameters.
	 *
	 * @param start the start
	 * @param end the end
	 * @param score the score
	 * @param positions the positions
	 * @return a search match result
	 */
	public static SearchMatchResult of(int start, int end, int score, int[] positions, SearchMatchAlgorithm algo) {
		return new DefaultResult(start, end, score, positions, algo);
	}

	public static SearchMatchResult ofZeros() {
		return of(0, 0, 0, new int[0], null);
	}

	public static SearchMatchResult ofMinus() {
		return of(-1, -1, 0, new int[0], null);
	}

	static class DefaultResult implements SearchMatchResult {

		int start;
		int end;
		int score;
		int[] positions;
		SearchMatchAlgorithm algo;

		DefaultResult(int start, int end, int score, int[] positions, SearchMatchAlgorithm algo) {
			this.start = start;
			this.end = end;
			this.score = score;
			this.positions = positions;
			this.algo = algo;
		}

		@Override
		public int getStart() {
			return start;
		}

		@Override
		public int getEnd() {
			return end;
		}

		@Override
		public int getScore() {
			return score;
		}

		@Override
		public int[] getPositions() {
			return positions;
		}

		@Override
		public SearchMatchAlgorithm getAlgorithm() {
			return algo;
		}
	}
}
