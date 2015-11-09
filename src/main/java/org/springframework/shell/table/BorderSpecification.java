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

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * This represents a directive to set some borders on cells of a table.
 * Multiple specifications can be combined on a single table.
 *
 * @author Eric Bottard
 */
public class BorderSpecification {

	public static final int NONE = 0;

	public static final int TOP = 1;

	public static final int BOTTOM = 2;

	public static final int LEFT = 4;

	public static final int RIGHT = 8;

	public static final int INNER_VERTICAL = 16;

	public static final int INNER_HORIZONTAL = 32;

	public static final int OUTLINE = TOP | BOTTOM | LEFT | RIGHT;

	public static final int FULL = OUTLINE | INNER_HORIZONTAL | INNER_VERTICAL;

	public static final int INNER = INNER_HORIZONTAL | INNER_VERTICAL;

	private final int row1, row2, column1, column2;

	private final int match;

	private final BorderStyle style;

	/**
	 * Specifications are created by {@link Table#addBorder(int, int, int, int, int, BorderStyle)}.
	 */
	/*default*/ BorderSpecification(int row1, int column1, int row2, int column2, int match, BorderStyle style) {
		this.row1 = row1;
		this.row2 = row2;
		this.column1 = column1;
		this.column2 = column2;
		this.match = match;
		this.style = style;
	}

	/**
	 * Does this specification result in the need to paint a vertical bar at row,column?
	 */
	/*default*/ char verticals(int row, int column) {
		boolean result = (match & LEFT) == LEFT && column == column1;
		result |= (match & INNER_VERTICAL) == INNER_VERTICAL && column > column1 && column < column2;
		result |= (match & RIGHT) == RIGHT && column == column2;

		result &= row >= row1;
		result &= row < row2;
		return result ? style.verticalGlyph() : BorderStyle.NONE;
	}

	/**
	 * Does this specification result in the need to paint an horizontal bar at row,column?
	 */
	/*default*/ char horizontals(int row, int column) {
		boolean result = (match & TOP) == TOP && row == row1;
		result |= (match & INNER_HORIZONTAL) == INNER_HORIZONTAL && row > row1 && row < row2;
		result |= (match & BOTTOM) == BOTTOM && row == row2;

		result &= column >= column1;
		result &= column < column2;
		return result ? style.horizontalGlyph() : BorderStyle.NONE;
	}

	@Override
	public String toString() {
		return String.format("%s[(%d, %d)->(%d, %d), %s, %s]", getClass().getSimpleName(), row1, column1, row2, column2, style, matchConstants());
	}

	private String matchConstants() {
		try {
			for (String field : new String[] {"NONE", "INNER", "FULL", "OUTLINE"}) {
				int value = ReflectionUtils.findField(getClass(), field).getInt(null);
				if (match  == value) {
					return field;
				}
			}
			List<String> constants = new ArrayList<String>();
			for (String field : new String[] {"TOP", "BOTTOM", "LEFT", "RIGHT", "INNER_HORIZONTAL", "INNER_VERTICAL"}) {
				int value = ReflectionUtils.findField(getClass(), field).getInt(null);
				if ((match & value) == value) {
					constants.add(field);
				}
			}
			return StringUtils.collectionToDelimitedString(constants, "|");
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}