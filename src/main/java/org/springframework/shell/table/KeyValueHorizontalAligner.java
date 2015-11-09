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
 * A text alignment strategy that aligns text horizontally so that all instances of some special character(s)
 * line up perfectly in a column.
 *
 * <p>Typically used to render numbers which may or may not have a decimal point, or series of key-value pairs</p>
 *
 * @author Eric Bottard
 */
public class KeyValueHorizontalAligner implements Aligner {

	private final String delimiter;

	public KeyValueHorizontalAligner(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String[] align(String[] text, int cellWidth, int cellHeight) {

		String[] result = new String[cellHeight];
		int alignOffset = 0;
		for (String line : text) {
			alignOffset = Math.max(alignOffset, line.trim().indexOf(delimiter));
		}
		int i = 0;
		for (String line : text) {
			String trimmed = line.trim();
			int offset = trimmed.indexOf(delimiter);
			if (offset >= 0) {
				// It is possible that aligning would trigger overflow
				// Make sure not to
				int offsetToUse = Math.min(alignOffset - offset, cellWidth - trimmed.length());
				result[i++] = pad(offsetToUse, cellWidth - trimmed.length() - offsetToUse, trimmed);
			}
			else {
				result[i++] = pad(0, cellWidth - line.length(), line);
			}

		}
		return result;
	}

	private String pad(int left, int right, String original) {
		StringBuilder sb = new StringBuilder(left + original.length() + right);
		for (int i = 0; i < left; i++) {
			sb.append(' ');
		}
		sb.append(original);
		for (int i = 0; i < right; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
}
