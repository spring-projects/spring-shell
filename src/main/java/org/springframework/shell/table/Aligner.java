/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.table;

/**
 * A strategy interface for performing text alignment.
 *
 * @author Eric Bottard
 */
public interface Aligner {

	/**
	 * Perform text alignment, returning a String array that MUST contain {@code cellHeight}
	 * lines, each of which MUST be {@code cellWidth} chars in length.
	 *
	 * <p>
	 * Input array is guaranteed to contain lines that have length equal to {@code cellWidth}.
	 * There is no guarantee on the input number of lines though.
	 * </p>
	 * 
	 * @param text the text to align
	 * @param cellWidth the width of of the table cell
	 * @param cellHeight the height of the table cell
	 * @return the aligned text, in a {@code cellHeight} element array
	 */
	String[] align(String[] text, int cellWidth, int cellHeight);
}
