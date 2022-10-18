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
 * Interface to match given text with a pattern into a result.
 *
 * @author Janne Valkealahti
 */
@FunctionalInterface
public interface SearchMatchAlgorithm {

	/**
	 * Match given text with pattern as a result.
	 *
	 * @param caseSensitive the caseSensitive
	 * @param normalize the normalize
	 * @param forward the forward
	 * @param text the test
	 * @param pattern the pattern
	 * @return a search match result
	 */
	SearchMatchResult match(boolean caseSensitive, boolean normalize, boolean forward, String text, String pattern);
}
