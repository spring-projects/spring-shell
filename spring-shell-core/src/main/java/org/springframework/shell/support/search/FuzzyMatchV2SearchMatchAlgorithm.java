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

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Port of {@code fzf} {@code FuzzyMatchV2} algorithm.
 *
 * @author Janne Valkealahti
 */
class FuzzyMatchV2SearchMatchAlgorithm extends AbstractSearchMatchAlgorithm {

	@Override
	public SearchMatchResult match(boolean caseSensitive, boolean normalize, boolean forward, String text,
			String pattern) {
		if (!StringUtils.hasText(pattern)) {
			return SearchMatchResult.ofZeros();
		}
		int M = pattern.length();
		int N = text.length();

		int idx = asciiFuzzyIndex(text, pattern, caseSensitive);
		if (idx < 0) {
			return SearchMatchResult.ofMinus();
		}

		List<Integer> H0 = create(N);
		List<Integer> C0 = create(N);
		List<Integer> B = create(N);
		List<Integer> F = create(M);
		String T = text;

		// Phase 2. Calculate bonus for each point
		int maxScore = 0;
		int maxScorePos = 0;
		int pidx = 0;
		int lastIdx = 0;

		char pchar0 = pattern.charAt(0);
		char pchar = pattern.charAt(0);
		int prevH0 = 0;
		CharClass prevClass = CharClass.WHITE;
		boolean inGap = false;

		int TsubIdxRestore = idx;
		String Tsub = T.substring(idx);

		List<Integer> H0sub = slicex(H0, idx, Tsub.length());
		List<Integer> C0sub = slicex(C0, idx, Tsub.length());
		List<Integer> Bsub = slicex(B, idx, Tsub.length());

		for (int off = 0; off < Tsub.length(); off++) {
			char c = Tsub.charAt(off);
			CharClass clazz;

			if (c >= 32 && c < 127) {
				clazz = charClassOfAscii(c);
				if (!caseSensitive && clazz == CharClass.UPPER) {
					c += 32;
				}
			}
			else {
				clazz = charClassOfNonAscii(c);
				if (!caseSensitive && clazz == CharClass.UPPER) {
					c = Character.toLowerCase(c);
				}
				if (normalize) {
					c = normalizeRune(c);
				}
			}

			// TODO: potential speed increase as go can directly modify underlying array/slice
			//       by access via for loop variables and so on. we create a lot of garbage here.
			Tsub = Tsub.substring(0, off) + c + Tsub.substring(off + 1);
			int bonus = bonusFor(prevClass, clazz);
			Bsub.set(off, bonus);
			prevClass = clazz;

			if (c == pchar) {
				if (pidx < M) {
					F.set(pidx, idx + off);
					pidx++;
					pchar = pattern.charAt(Math.min(pidx, M - 1));
				}
				lastIdx = idx + off;
			}

			if (c == pchar0) {
				int score = SCORE_MATCH + bonus * BONUS_FIRST_CHAR_MULTIPLIER;
				H0sub.set(off, score);
				C0sub.set(off, 1);
				if (M == 1 && (forward && score > maxScore || !forward && score >= maxScore)) {
					maxScore = score;
					maxScorePos = idx + off;
					if (forward && bonus >= BONUS_BOUNDARY) {
						break;
					}
				}
				inGap = false;
			}
			else {
				if (inGap) {
					H0sub.set(off, Math.max(prevH0 + SCORE_GAP_EXTENSION, 0));
				}
				else {
					H0sub.set(off, Math.max(prevH0 + SCORE_GAP_START, 0));
				}
				C0sub.set(off, 0);
				inGap = true;
			}
			prevH0 = H0sub.get(off);
		}

		T = T.substring(0, TsubIdxRestore) + Tsub;
		if (pidx != M) {
			return SearchMatchResult.ofMinus();
		}
		if (M == 1) {
			return SearchMatchResult.of(maxScorePos, maxScorePos + 1, maxScore, new int[] { maxScorePos }, this);
		}

		// Phase 3. Fill in score matrix (H)
		int f0 = F.get(0);
		int width = lastIdx - f0 + 1;
		List<Integer> H = create(width * M);
		copy(H, H0, f0, lastIdx + 1);

		List<Integer> C = create(width * M);
		copy(C, C0, f0, lastIdx + 1);

		List<Integer> Fsub = F.subList(1, F.size());
		String Psub = pattern.substring(1);
		Psub = Psub.substring(0, Fsub.size());

		for (int off = 0; off < Fsub.size(); off++) {
			int f = Fsub.get(off);
			char pchar2 = Psub.charAt(off);
			int pidx2 = off + 1;
			int row = pidx2 * width;
			boolean inGap2 = false;
			String Tsub2 = T.substring(f, lastIdx + 1);
			List<Integer> Bsub2 = slicex(B, f, Tsub2.length());
			List<Integer> Csub2 = slicex(C, row + f - f0, Tsub2.length());
			List<Integer> Cdiag = slicex(C, row + f - f0 - 1 - width, Tsub2.length());
			List<Integer> Hsub2 = slicex(H, row + f - f0, Tsub2.length());
			List<Integer> Hdiag = slicex(H, row + f - f0 - 1 - width, Tsub2.length());
			List<Integer> Hleft = slicex(H, row + f - f0 - 1, Tsub2.length());
			Hleft.set(0, 0);

			for (int off2 = 0; off2 < Tsub2.length(); off2++) {
				char c = Tsub2.charAt(off2);
				int col = off2 + f;
				int s1 = 0;
				int s2 = 0;
				int consecutive = 0;

				if (inGap2) {
					s2 = Hleft.get(off2) + SCORE_GAP_EXTENSION;
				}
				else {
					s2 = Hleft.get(off2) + SCORE_GAP_START;
				}

				if (pchar2 == c) {
					s1 = Hdiag.get(off2) + SCORE_MATCH;
					int b = Bsub2.get(off2);
					consecutive = Cdiag.get(off2) + 1;

					if (consecutive > 1) {
						int fb = B.get(col - consecutive + 1);
						if (b >= BONUS_BOUNDARY && b > fb) {
							consecutive = 1;
						}
						else {
							b = Math.max(b, Math.max(BONUS_CONSECUTIVE, fb));
						}
					}
					if (s1 + b < s2) {
						s1 += Bsub2.get(off2);
						consecutive = 0;
					}
					else {
						s1 += b;
					}
				}
				Csub2.set(off2, consecutive);
				inGap2 = s1 < s2;
				int score = Math.max(Math.max(s1, s2), 0);
				if (pidx2 == M - 1 && (forward && score > maxScore) || !forward && score >= maxScore) {
					maxScore = score;
					maxScorePos = col;
				}
				Hsub2.set(off2, score);
			}
		}

		// Phase 4. (Optional) Backtrace to find character positions
		int[] pos = new int[M];
		int j = f0;
		int i = M - 1;
		j = maxScorePos;
		boolean preferMatch = true;
		int posidx = pos.length - 1;
		for(;;) {
			int I = i * width;
			int j0 = j - f0;
			int s = H.get(I + j0);
			int s1 = 0;
			int s2 = 0;
			if (i > 0 && j >= F.get(i)) {
				s1 = H.get(I - width + j0 - 1);
			}
			if (j > F.get(i)) {
				s2 = H.get(I + j0 - 1);
			}
			if (s > s1 && (s > s2 || s == s2 && preferMatch)) {
				pos[posidx--] = j;
				if (i == 0) {
					break;
				}
				i--;
			}
			preferMatch = C.get(I + j0) > 1 || I + width + j0 + 1 < C.size() && C.get(I + width + j0 + 1) > 0;
			j--;
		}

		return SearchMatchResult.of(j, maxScorePos + 1, maxScore, pos, this);
	}

	private static List<Integer> slicex(List<Integer> from, int start, int length) {
		return from.subList(start, from.size()).subList(0, length);
	}

	private static void copy(List<Integer> dst, List<Integer> src, int start, int end) {
		int x = 0;
		for (int i = start; i < end; i++) {
			dst.set(x++, src.get(i));
		}
	}

	private static List<Integer> create(int size) {
		List<Integer> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			list.add(0);
		}
		return list;
	}
}
