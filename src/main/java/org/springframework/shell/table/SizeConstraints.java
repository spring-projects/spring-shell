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

import org.springframework.util.Assert;

/**
 * Strategy for computing the dimensions of a table cell.
 *
 * @author Eric Bottard
 */
public interface SizeConstraints {

	/**
	 * Return the minimum and maximum width of the cell, given its raw content.
	 * @param raw the raw String representation of the cell contents (may be reformatted later, eg wrapped)
	 * @param tableWidth the whole available width for the table
	 * @param nbColumns the number of columns in the table
	 */
	Extent width(String[] raw, int tableWidth, int nbColumns);

	/**
	 * Holds both a minimum and maximum width.
	 *
	 * @author Eric Bottard
	 */
	class Extent {

		public final int min;

		public final int max;

		public Extent(int min, int max) {
			Assert.isTrue(min <= max, "min must be less than max");
			Assert.isTrue(0 <= min, "min and max must be positive");
			this.min = min;
			this.max = max;
		}
	}
}
