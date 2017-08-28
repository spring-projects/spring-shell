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
 * A TableModel backed by a row-first array.
 *
 * @author Eric Bottard
 */
public class ArrayTableModel extends TableModel {

	private Object[][] data;

	public ArrayTableModel(Object[][] data) {
		this.data = data;
		int width = data.length > 0 ? data[0].length : 0;
		for (int row = 0; row < data.length; row++) {
			Assert.isTrue(width == data[row].length, "All rows of array data must be of same length");
		}
	}

	public int getRowCount() {
		return data.length;
	}

	public int getColumnCount() {
		return data.length > 0 ? data[0].length : 0;
	}

	public Object getValue(int row, int column) {
		return data[row][column];
	}
}
