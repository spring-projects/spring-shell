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
 * Port of {@code fzf} {@code FuzzyMatchV1} algorithm.
 *
 * @author Janne Valkealahti
 */
class FuzzyMatchV1SearchMatchAlgorithm extends AbstractSearchMatchAlgorithm {

	@Override
	public SearchMatchResult match(boolean caseSensitive, boolean normalize, boolean forward, String text, String pattern) {
		int pidx = 0;
		int sidx = -1;
		int eidx = -1;
		int lenRunes = text.length();
		int lenPattern = pattern.length();

		for (int index = 0; index < lenRunes; index++) {
			char c = text.charAt(indexAt(index, lenRunes, forward));

			if (!caseSensitive) {
				if (c >= 'A' && c <= 'Z') {
					c += 32;
				}
			}

			if (normalize) {
				c = normalizeRune(c);
			}

			char pchar = pattern.charAt(indexAt(pidx, lenPattern, forward));

			if (c == pchar) {
				if (sidx < 0) {
					sidx = index;
				}

				pidx++;
				if (pidx == lenPattern) {
					eidx = index + 1;
					break;
				}
			}
		}

		if (sidx >= 0 && eidx >= 0) {
			pidx--;
			for (int i = eidx - 1; i >= sidx; i--) {
				int tidx = indexAt(i, lenRunes, forward);
				char c = text.charAt(tidx);
				if (!caseSensitive) {
					if (c >= 'A' && c <= 'Z') {
						c += 32;
					}
				}
				int pidx_ = indexAt(pidx, lenPattern, forward);
				char pchar = pattern.charAt(pidx_);
				if (c == pchar) {
					pidx--;
					if (pidx < 0) {
						sidx = i;
						break;
					}
				}
			}
			if (!forward) {
				int sidxOrig = sidx;
				sidx = lenRunes - eidx;
				eidx = lenRunes - sidxOrig;
			}
			CalculateScore calculateScore = calculateScore(caseSensitive, normalize, text, pattern, sidx, eidx);
			return SearchMatchResult.of(sidx, eidx, calculateScore.score, calculateScore.pos, this);
		}
		return SearchMatchResult.of(-1, -1, 0, new int[0], this);
	}
}
