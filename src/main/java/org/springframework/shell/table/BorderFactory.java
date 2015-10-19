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

import static org.springframework.shell.table.BorderSpecification.*;

/**
 * Helper class for setting typical borders on a Table.
 *
 * @author Eric Bottard
 */
public class BorderFactory {

	private BorderFactory() {

	}

	/**
	 * Set a border on the outline of the whole table.
	 */
	public static Table outline(Table table, BorderStyle style) {
		TableModel model = table.getModel();
		table.withBorder(0, 0, model.getRowCount(), model.getColumnCount(), OUTLINE, style);
		return table;
	}

	/**
	 * Set a border on the outline of the whole table, as well as around the first row.
	 */
	public static Table header(Table table, BorderStyle style) {
		TableModel model = table.getModel();
		table.withBorder(0, 0, 1, model.getColumnCount(), OUTLINE, style);
		return outline(table, style);
	}

	/**
	 * Set a border around each and every cell of the table.
	 */
	public static Table full(Table table, BorderStyle style) {
		TableModel model = table.getModel();
		table.withBorder(0, 0, model.getRowCount(), model.getColumnCount(), FULL, style);
		return table;
	}

	/**
	 * Set a border on the outline of the whole table, around the first row and draw vertical lines
	 * around each column.
	 */
	public static Table headerAndVerticals(Table table, BorderStyle style) {
		TableModel model = table.getModel();
		table.withBorder(0, 0, 1, model.getColumnCount(), OUTLINE, style);
		table.withBorder(0, 0, model.getRowCount(), model.getColumnCount(), OUTLINE | INNER_VERTICAL, style);
		return table;
	}

}
