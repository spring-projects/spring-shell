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
 * An horizontal alignment strategy that allows alignment to the left, center or right.
 *
 * @author Eric Bottard
 */
public enum SimpleHorizontalAligner implements Aligner {

	left, center, right;

	@Override
	public String[] align(String[] text, int cellWidth, int cellHeight) {
		String[] result = new String[cellHeight];
		for (int i = 0; i < cellHeight; i++) {
			String line = (i < text.length && text[i] != null) ? text[i].trim() : "";

			int paddingToDistribute = cellWidth - line.length();

			int padLeft;
			int padRight;

			switch (this) {
				case center: {
					int carry = paddingToDistribute % 2;
					paddingToDistribute = paddingToDistribute - carry;
					padLeft = padRight = paddingToDistribute / 2;
					padRight += carry;
					break;
				}
				case right: {
					padLeft = paddingToDistribute;
					padRight = 0;
					break;
				}
				case left: {
					padLeft = 0;
					padRight = paddingToDistribute;
					break;
				}
				default:
					throw new AssertionError();
			}
			StringBuilder sb = new StringBuilder(cellWidth);
			for (int j = 0; j < padLeft; j++) {
				sb.append(' ');
			}
			sb.append(line);
			for (int j = 0; j < padRight; j++) {
				sb.append(' ');
			}

			result[i] = sb.toString();
		}
		return result;
	}

}
