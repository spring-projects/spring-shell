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
 * Contains factory methods for commonly used {@link CellMatcher}s.
 *
 * @author Eric Bottard
 */
public class CellMatchers {

	/**
	 * @return a matcher that applies to every cell of the table.
	 */
	public static CellMatcher table() {
		return new CellMatcher() {
			public boolean matches(int row, int column, TableModel model) {
				return true;
			}
		};
	}

	/**
	 * @return a matcher that applies to every cell of some column of the table.
	 * @param col the column to select
	 */
	public static CellMatcher column(final int col) {
		return new CellMatcher() {
			public boolean matches(int row, int column, TableModel model) {
				return col == column;
			}
		};
	}

	/**
	 * @return a matcher that applies to every cell of some row of the table.
	 * @param theRow the row to select
	 */
	public static CellMatcher row(final int theRow) {
		return new CellMatcher() {
			public boolean matches(int row, int column, TableModel model) {
				return theRow == row;
			}
		};
	}

	/**
	 * @param theRow the row to select
	 * @param col    the column to select
	 * @return a matcher that applies to exactly one cell of the table, identified by its row and colum.
	 */
	public static CellMatcher at(final int theRow, final int col) {
		return new CellMatcher() {
			@Override
			public boolean matches(int row, int column, TableModel model) {
				return row == theRow && column == col;
			}
		};
	}

	/**
	 * @param clazz the type that cells should contain
	 * @return a matcher that matches cells whose content is of a certain type
	 */
	public static CellMatcher ofType(final Class<?> clazz) {
		return new CellMatcher() {
			@Override
			public boolean matches(int row, int column, TableModel model) {
				Object value = model.getValue(row, column);
				if (value == null) {
					return false;
				}
				else {
					return clazz.isAssignableFrom(value.getClass());
				}
			}
		};
	}
}
