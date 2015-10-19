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
 * A strategy for applying text wrapping/cropping given a cell width.
 */
public interface TextWrapper {

	/**
	 * Return a list of lines where each line length MUST be equal to {@code columnWidth} (padding with spaces if
	 * appropriate). There is no constraint on the number of lines returned however (typically, will be greater than
	 * the input number if wrapping occurred).
	 */
	String[] wrap(String[] original, int columnWidth);
}
