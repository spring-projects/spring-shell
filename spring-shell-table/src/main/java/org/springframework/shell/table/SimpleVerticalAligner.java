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

import java.util.Arrays;

/**
 * Alignment strategy that allows simple vertical alignment to top, middle or bottom.
 *
 * @author Eric Bottard
 */
public enum SimpleVerticalAligner implements Aligner {

	top, middle, bottom;

	@Override
	public String[] align(String[] text, int cellWidth, int cellHeight) {
		String[] result = new String[cellHeight];
		int blanksBefore = 0;
		int blanksAfter = 0;
		boolean atLeastOneNonEmptyRow = false;
		for (int row = 0; row < text.length; row++) {
			if (text[row] == null || text[row].trim().equals("")) {
				blanksBefore++;
			}
			else {
				atLeastOneNonEmptyRow = true;
				break;
			}
		}
		// In case of full blank, don't count blank rows twice
		if (atLeastOneNonEmptyRow) {
			for (int row = text.length - 1; row >= 0; row--) {
				if (text[row] == null || text[row].trim().equals("")) {
					blanksAfter++;
				}
				else {
					break;
				}
			}
		}
		String filler = spaces(cellWidth);

		int padBefore;
		int padAfter;
		int nonBlankLines = text.length - blanksAfter - blanksBefore;
		int paddingToDistribute = cellHeight - nonBlankLines;

		switch (this) {
			case middle: {
				int carry = paddingToDistribute % 2;
				paddingToDistribute = paddingToDistribute - carry;
				padBefore = padAfter = paddingToDistribute / 2;
				padAfter += carry;
				break;
			}
			case bottom: {
				padBefore = paddingToDistribute;
				padAfter = 0;
				break;
			}
			case top: {
				padBefore = 0;
				padAfter = paddingToDistribute;
				break;
			}
			default:
				throw new AssertionError();
		}

		Arrays.fill(result, 0, padBefore, filler);
		System.arraycopy(text, blanksBefore, result, padBefore, nonBlankLines);
		Arrays.fill(result, result.length - padAfter, result.length, filler);

		return result;
	}

	private String spaces(int width) {
		char[] data = new char[width];
		Arrays.fill(data, ' ');
		return new String(data);
	}

}
