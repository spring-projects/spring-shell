/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.table;

/**
 * A SizeConstraints implementation that is tailored to rendering a series
 * of {@literal key = value} pairs. Computes extents so that equal signs (or any other
 * configurable delimiter) line up vertically.
 *
 * @author Eric Bottard
 */
public class KeyValueSizeConstraints implements SizeConstraints {

	private final String delimiter;

	public KeyValueSizeConstraints(String delimiter) {
		this.delimiter = delimiter;
	}

	private static String leftTrim(String raw) {
		int start = 0;
		int length = raw.length();
		while (start < length && raw.charAt(start) == ' ') {
			start++;
		}
		return raw.substring(start);
	}

	private static String rightTrim(String raw) {
		int end = raw.length();
		while (end > 0 && raw.charAt(end - 1) == ' ') {
			end--;
		}
		return raw.substring(0, end);
	}

	@Override
	public Extent width(String[] raw, int tableWidth, int nbColumns) {

		// We need to make sure we take care of the case where we have
		//        k = long-value
		// long-key = v
		// as the real maximal extent is size(long-key) + size( = ) + size(long-value)

		// The minimal extent in the example above is size(long-value)

		int maxLeft = 0;
		int maxRight = 0;
		int min = 0;
		for (String line : raw) {
			String lineToConsider = line.trim();
			int offset = lineToConsider.indexOf(delimiter);

			if (offset != -1) {
				// Compute minimal case (line can be split, decide where to put the delimiter)
				String minimalLeftPart = lineToConsider.substring(0, offset).trim();
				String minimalRightPart = lineToConsider.substring(offset + delimiter.length()).trim();
				int left = minimalLeftPart.length();
				int right = minimalRightPart.length();
				int case1 = Math.max(left, right + leftTrim(delimiter).length());
				int case2 = Math.max(left + rightTrim(delimiter).length(), right);
				int bestMin = Math.min(case1, case2);
				min = Math.max(min, bestMin);

				// Compute maximal case (sum of worst case on left and right)
				maxLeft = Math.max(maxLeft, offset);
				int after = lineToConsider.length() - offset - delimiter.length();
				maxRight = Math.max(maxRight, after);
			}
			else {
				min = Math.max(min, lineToConsider.length());
			}

		}

		return new Extent(min, maxLeft + delimiter.length() + maxRight);
	}
}
