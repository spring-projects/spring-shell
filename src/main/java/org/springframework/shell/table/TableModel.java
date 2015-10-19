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
 * Abstracts away the contract a {@link Table} will use to retrieve tabular data.
 *
 * @author Eric Bottard
 */
public abstract class TableModel {

	/**
	 * Return the number of rows that can be queried.
	 * Values between 0 and {@code rowCount-1} inclusive are valid values.
	 */
	public abstract int getRowCount();

	/**
	 * Return the number of columns that can be queried.
	 * Values between 0 and {@code columnCount-1} inclusive are valid values.
	 */
	public abstract int getColumnCount();

	/**
	 * Return the data value to be displayed at a given row and column, which may be null.
	 */
	public abstract Object getValue(int row, int column);

	/**
	 * Return a transposed view of this model, where rows become columns and vice-versa.
	 */
	public TableModel transpose() {
		return new TableModel() {
			@Override
			public int getRowCount() {
				return TableModel.this.getColumnCount();
			}

			@Override
			public int getColumnCount() {
				return TableModel.this.getRowCount();
			}

			@Override
			public Object getValue(int row, int column) {
				return TableModel.this.getValue(column, row);
			}
		};
	}
}
