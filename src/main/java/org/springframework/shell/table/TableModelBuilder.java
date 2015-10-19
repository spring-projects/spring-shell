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

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

/**
 * Helper class to build a TableModel incrementally.
 *
 * @author Eric Bottard
 */
public class TableModelBuilder<T> {

	public static final int DEFAULT_ROW_CAPACITY = 3;

	private List<List<T>> rows = new ArrayList<List<T>>();

	private int previousRowSize = -1;

	private boolean frozen;

	public TableModelBuilder<T> addRow() {
		Assert.isTrue(!frozen, "TableModel has already been built, builder can't be altered anymore");
		int nbRows = rows.size();
		if (previousRowSize != -1) {
			int currentRowSize = rows.get(nbRows - 1).size();
			Assert.isTrue(currentRowSize == previousRowSize,
					"Can't switch to next row, as the current one does not have as many elements as the previous one");
		}
		if (rows.size() > 0) {
			previousRowSize = rows.get(0).size();
		}
		rows.add(new ArrayList<T>(previousRowSize == -1 ? DEFAULT_ROW_CAPACITY : previousRowSize));
		return this;
	}

	public TableModelBuilder<T> addValue(T value) {
		Assert.isTrue(!frozen, "TableModel has already been built, builder can't be altered anymore");
		if (previousRowSize != -1 && rows.get(rows.size() - 1).size() == previousRowSize) {
			throw new IllegalArgumentException("Can't add another value to current row");
		}
		rows.get(rows.size() - 1).add(value);
		return this;
	}

	public TableModel build() {
		frozen = true;
		return new TableModel() {
			@Override
			public int getRowCount() {
				return rows.size();
			}

			@Override
			public int getColumnCount() {
				return rows.isEmpty() ? 0 : rows.get(0).size();
			}

			@Override
			public Object getValue(int row, int column) {
				return rows.get(row).get(column);
			}
		};

	}
}
