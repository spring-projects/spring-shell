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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Base class for common search algorithms mostly based on {@code fzf}
 * algorithms.
 *
 * @author Janne Valkealahti
 */
abstract class AbstractSearchMatchAlgorithm implements SearchMatchAlgorithm {

	// points given to matches
	public static final int SCORE_MATCH = 16;
	public static final int SCORE_GAP_START = -3;
	public static final int SCORE_GAP_EXTENSION = -1;
	public static final int BONUS_BOUNDARY = SCORE_MATCH / 2;
	public static final int BONUS_NON_WORD = SCORE_MATCH / 2;
	public static final int BONUS_CAMEL123 = BONUS_BOUNDARY + SCORE_GAP_EXTENSION;
	public static final int BONUS_CONSECUTIVE = -(SCORE_GAP_START + SCORE_GAP_EXTENSION);
	public static final int BONUS_FIRST_CHAR_MULTIPLIER = 2;
	public static final int BONUS_BOUNDARY_WHITE = BONUS_BOUNDARY + 2; // default +2
	public static final int BONUS_BOUNDARY_DELIMITER = BONUS_BOUNDARY + 1 + 1; // default +1

	/**
	 * Enumeration of matched characters.
	 */
	static enum CharClass {
		WHITE,
		NONWORD,
		DELIMITER,
		LOWER,
		UPPER,
		LETTER,
		NUMBER
	}

	static class CalculateScore {
		int score;
		int pos[];

		CalculateScore(int score, int[] pos) {
			this.score = score;
			this.pos = pos;
		}
	}

	static int indexAt(int index, int max, boolean forward) {
		if (forward) {
			return index;
		}
		return max - index - 1;
	}

	private final static String DELIMITER_CHARS = "/,:;|";

	static CharClass charClassOfAscii(char c) {
		if (c >= 'a' && c <= 'z') {
			return CharClass.LOWER;
		}
		else if (c >= 'A' && c <= 'Z') {
			return CharClass.UPPER;
		}
		else if (c >= '0' && c <= '9') {
			return CharClass.NUMBER;
		}
		else if (Character.isWhitespace(c)) {
			return CharClass.WHITE;
		}
		else if (DELIMITER_CHARS.indexOf(c) >= 0) {
			return CharClass.DELIMITER;
		}
		return CharClass.NONWORD;
	}

	static CharClass charClassOfNonAscii(char c) {
		if (Character.isLowerCase(c)) {
			return CharClass.LOWER;
		}
		else if (Character.isUpperCase(c)) {
			return CharClass.UPPER;
		}
		else if (Character.isDigit(c)) {
			return CharClass.NUMBER;
		}
		else if (Character.isLetter(c)) {
			return CharClass.LETTER;
		}
		else if (Character.isWhitespace(c)) {
			return CharClass.WHITE;
		}
		else if (DELIMITER_CHARS.indexOf(c) >= 0) {
			return CharClass.DELIMITER;
		}
		return CharClass.NONWORD;
	}

	static int bonusFor(CharClass prevClass, CharClass clazz) {
		if (clazz.ordinal() > CharClass.NONWORD.ordinal()) {
			if (prevClass == CharClass.WHITE) {
				return BONUS_BOUNDARY_WHITE;
			}
			else if (prevClass == CharClass.DELIMITER) {
				return BONUS_BOUNDARY_DELIMITER;
			}
			else if (prevClass == CharClass.NONWORD) {
				return BONUS_BOUNDARY;
			}
		}
		if (prevClass == CharClass.LOWER && clazz == CharClass.UPPER ||
				prevClass != CharClass.NUMBER && clazz == CharClass.NUMBER) {
			return BONUS_CAMEL123;
		}
		else if (clazz == CharClass.NONWORD) {
			return BONUS_NON_WORD;
		}
		else if (clazz == CharClass.WHITE) {
			return BONUS_BOUNDARY_WHITE;
		}
		return 0;
	}

	static int bonusAt(String input, int idx) {
		if (idx == 0) {
			return BONUS_BOUNDARY_WHITE;
		}
		return bonusFor(charClassOfAscii(input.charAt(idx - 1)), charClassOfAscii(input.charAt(idx)));
	}

	static CalculateScore calculateScore(boolean caseSensitive, boolean normalize, String text, String pattern,
			int sidx, int eidx) {
		int pidx = 0;
		int score = 0;
		boolean inGap = false;
		int consecutive = 0;
		int firstBonus = 0;
		List<Integer> positions = new ArrayList<>();

		CharClass prevClass = CharClass.WHITE;
		if (sidx > 0) {
			prevClass = charClassOfAscii(text.charAt(sidx - 1));
		}

		for (int idx = sidx; idx < eidx; idx++) {
			char c = text.charAt(idx);
			CharClass clazz = charClassOfAscii(c);

			if (!caseSensitive) {
				if (c >= 'A' && c <= 'Z') {
					c += 32;
				}
			}

			if (normalize) {
				c = normalizeRune(c);
			}

			if (c == pattern.charAt(pidx)) {
				positions.add(idx);
				score += SCORE_MATCH;
				int bonus = bonusFor(prevClass, clazz);
				if (consecutive == 0) {
					firstBonus = bonus;
				}
				else {
					if (bonus >= BONUS_BOUNDARY && bonus > firstBonus) {
						firstBonus = bonus;
					}
					bonus = Math.max(Math.max(bonus, firstBonus), BONUS_CONSECUTIVE);
				}
				if (pidx == 0) {
					score += bonus * BONUS_FIRST_CHAR_MULTIPLIER;
				}
				else {
					score += bonus;
				}
				inGap = false;
				consecutive++;
				pidx++;
			}
			else {
				if (inGap) {
					score += SCORE_GAP_EXTENSION;
				}
				else {
					score += SCORE_GAP_START;
				}
				inGap = true;
				consecutive = 0;
				firstBonus = 0;
			}
			prevClass = clazz;

		}
		return new CalculateScore(score, positions.stream().mapToInt(Integer::intValue).toArray());
	}

	static int trySkip(String input, boolean caseSensitive, char b, int from) {
		String byteArray = input.substring(from);
		int idx = byteArray.indexOf(b);

		if (idx == 0) {
			return from;
		}

		if (!caseSensitive && b >= 'a' && b <= 'z') {
			if (idx > 0) {
				byteArray = byteArray.substring(idx);
			}
			int uidx = byteArray.indexOf(b - 32);
			if (uidx >= 0) {
				idx = uidx;
			}
		}

		if (idx < 0) {
			return -1;
		}

		return from + idx;
	}

	static int asciiFuzzyIndex(String input, String pattern, boolean caseSensitive) {
		if (!StringUtils.hasText(input)) {
			return 0;
		}

		if (!Charset.forName("US-ASCII").newEncoder().canEncode(input)) {
			return 0;
		}

		int firstIndex = 0;
		int idx = 0;

		for (int pidx = 0; pidx < pattern.length(); pidx++) {
			idx = trySkip(input, caseSensitive, pattern.charAt(pidx), idx);
			if (idx < 0) {
				return -1;
			}
			if (pidx == 0 && idx > 0) {
				firstIndex = idx - 1;
			}
			idx++;
		}

		return firstIndex;
	}

	static char normalizeRune(char r) {
		if (r < 0x00C0 || r > 0x2184) {
			return r;
		}
		Character n = Normalize.normalized.get(r);
		if (n != null && n > 0) {
			return n;
		}
		return r;
	}
}
