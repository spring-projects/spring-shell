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
 * Interface defining a search match for {@code text} agains {@code pattern}.
 * Resulting match result gives information in a context of {@code text} how and
 * where matches happens and provides a {@code score} number which can be to
 * sort results.
 *
 * @author Janne Valkealahti
 */
@FunctionalInterface
public interface SearchMatch {

	/**
	 * Match a pattern into a given text.
	 *
	 * @param text the text to search
	 * @param pattern the search pattern
	 * @return a result
	 */
	SearchMatchResult match(String text, String pattern);

	/**
	 * Gets an instance of a builder for a {@link SearchMatch}.
	 *
	 * @return builder for search match
	 */
	public static Builder builder() {
		return new DefaultBuilder();
	}

	/**
	 * Defines an interface for {@link SearchMatch}.
	 */
	interface Builder {

		/**
		 * Set a flag for {@code caseSensitive}.
		 *
		 * @param caseSensitive the caseSensitive
		 * @return builder for chaining
		 */
		Builder caseSensitive(boolean caseSensitive);

		/**
		 * Set a flag for {@code normalize}.
		 *
		 * @param normalize the normalize
		 * @return builder for chaining
		 */
		Builder normalize(boolean normalize);

		/**
		 * Set a flag for {@code forward}.
		 *
		 * @param forward the forward
		 * @return builder for chaining
		 */
		Builder forward(boolean forward);

		/**
		 * Build instance of a {@link SearchMatch}.
		 *
		 * @return a build instance of {@link SearchMatch}
		 */
		SearchMatch build();
	}

	static class DefaultBuilder implements Builder {

		private boolean caseSensitive;
		private boolean normalize;
		private boolean forward;

		@Override
		public Builder caseSensitive(boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
			return this;
		}

		@Override
		public Builder normalize(boolean normalize) {
			this.normalize = normalize;
			return this;
		}

		@Override
		public Builder forward(boolean forward) {
			this.forward = forward;
			return this;
		}

		@Override
		public SearchMatch build() {
			return new DefaultSearchMatch(this.caseSensitive, this.normalize, this.forward);
		}
	}

	static class DefaultSearchMatch implements SearchMatch {

		private boolean caseSensitive;
		private boolean normalize;
		private boolean forward;

		DefaultSearchMatch(boolean caseSensitive, boolean normalize, boolean forward) {
			this.caseSensitive = caseSensitive;
			this.normalize = normalize;
			this.forward = forward;
		}

		@Override
		public SearchMatchResult match(String text, String pattern) {
			SearchMatchAlgorithm algo = null;
			if (pattern != null) {
				// algos are currently expecting to pass pattern as lower case if
				// case sensitivity is not enabled.
				if (!caseSensitive) {
					pattern = pattern.toLowerCase();
				}
				// algos are currently expecting to pass pattern as normalized if
				// it is enabled.
				if (normalize) {
					pattern = Normalize.normalizeRunes(pattern);
				}

				// pick algorithm based on pattern
				if (pattern.startsWith("'")) {
					// exact match starting with "'"
					algo = new ExactMatchNaiveSearchMatchAlgorithm();
					pattern = pattern.substring(1);
				}
			}

			// default algo is fuzzy match
			if (algo == null) {
				// algo = new FuzzyMatchV1SearchMatchAlgorithm();
				algo = new FuzzyMatchV2SearchMatchAlgorithm();
			}

			return algo.match(caseSensitive, normalize, forward, text, pattern);
		}
	}
}
