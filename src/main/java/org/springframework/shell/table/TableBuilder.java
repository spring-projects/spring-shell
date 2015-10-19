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

import static org.springframework.shell.table.BorderSpecification.FULL;
import static org.springframework.shell.table.BorderSpecification.INNER;
import static org.springframework.shell.table.BorderSpecification.INNER_VERTICAL;
import static org.springframework.shell.table.BorderSpecification.OUTLINE;
import static org.springframework.shell.table.SimpleHorizontalAligner.left;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * A builder class to incrementally configure a Table.
 * @author Eric Bottard
 */
public class TableBuilder {

	private final TableModel model;

	private final Map<CellMatcher, Formatter> formatters = new LinkedHashMap<CellMatcher, Formatter>();

	private final Map<CellMatcher, SizeConstraints> sizeConstraints = new LinkedHashMap<CellMatcher, SizeConstraints>();

	private final Map<CellMatcher, TextWrapper> wrappers = new LinkedHashMap<CellMatcher, TextWrapper>();

	private final LinkedHashMap<CellMatcher, Aligner> aligners = new LinkedHashMap<CellMatcher, Aligner>();

	private final List<BorderSpecification> borderSpecifications = new ArrayList<BorderSpecification>();

	private final int rows;

	private final int columns;

	/**
	 * Construct a table with the given model. The table will use the following
	 * strategies for all cells, unless overridden:<ul>
	 * <li>{@link DefaultFormatter default formatting} using {@literal toString()}</li>
	 * <li>{@link AutoSizeConstraints sizing strategy} trying to use the maximum space, resorting to splitting lines on
	 * spaces</li>
	 * <li>{@link DelimiterTextWrapper wrapping text} on space characters</li>
	 * <li>{@link SimpleHorizontalAligner left aligning} text.</li>
	 * </ul>
	 */

	public TableBuilder(TableModel model) {
		this.model = model;
		rows = model.getRowCount();
		columns = model.getColumnCount();

		formatters.put(CellMatchers.table(), new DefaultFormatter());
		sizeConstraints.put(CellMatchers.table(), new AutoSizeConstraints());
		wrappers.put(CellMatchers.table(), new DelimiterTextWrapper());
		aligners.put(CellMatchers.table(), left);

	}

	private TableBuilder addBorder(int top, int left, int bottom, int right, int match, BorderStyle style) {
		Assert.isTrue(top >= 0 && top < rows, "top row must be positive and less than total number of rows");
		Assert.isTrue(left >= 0 && left < columns, "left column must be positive and less than total number of columns");
		Assert.isTrue(bottom > top && bottom <= rows, "bottom row must be greater than top and less than total number of rows");
		Assert.isTrue(right >= left && right <= columns, "right column must be greater than left and less than total number of columns");
		Assert.notNull(style, "style cannot be null");
		borderSpecifications.add(new BorderSpecification(top, left, bottom, right, match, style));
		return this;
	}

	public TableModel getModel() {
		return model;
	}

	public CellMatcherStub on(CellMatcher matcher) {
		return new CellMatcherStub(matcher);
	}

	public Table build() {
		return new Table(model,
				reverse(formatters),
				reverse(sizeConstraints),
				reverse(wrappers),
				aligners,
				borderSpecifications);
	}

	public BorderStub paintBorder(BorderStyle style, int match) {
		return new BorderStub(style, match);
	}

	// Convenience methods for borders

	/**
	 * Set a border on the outline of the whole table.
	 */
	public TableBuilder addOutlineBorder(BorderStyle style) {
		this.addBorder(0, 0, model.getRowCount(), model.getColumnCount(), OUTLINE, style);
		return this;
	}

	/**
	 * Set a border on the outline of the whole table, as well as around the first row.
	 */
	public TableBuilder addHeaderBorder(BorderStyle style) {
		this.addBorder(0, 0, 1, model.getColumnCount(), OUTLINE, style);
		return addOutlineBorder(style);
	}

	/**
	 * Set a border around each and every cell of the table.
	 */
	public TableBuilder addFullBorder(BorderStyle style) {
		this.addBorder(0, 0, model.getRowCount(), model.getColumnCount(), FULL, style);
		return this;
	}

	/**
	 * Set a border on the outline of the whole table, around the first row and draw vertical lines
	 * around each column.
	 */
	public TableBuilder addHeaderAndVerticalsBorders(BorderStyle style) {
		this.addBorder(0, 0, 1, model.getColumnCount(), OUTLINE, style);
		this.addBorder(0, 0, model.getRowCount(), model.getColumnCount(), OUTLINE | INNER_VERTICAL, style);
		return this;
	}

	/**
	 * Set a border on the inner verticals and horizontals of the table, but not on the outline.
	 */
	public TableBuilder addInnerBorder(BorderStyle style) {
		this.addBorder(0, 0, model.getRowCount(), model.getColumnCount(), INNER, style);
		return this;
	}

	private <K, V> LinkedHashMap<K, V> reverse(Map<K, V> original) {
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>(original.size());
		List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(original.entrySet());
		for (int i = entries.size() - 1; i >= 0; i--) {
			Map.Entry<K, V> entry = entries.get(i);
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public class CellMatcherStub {

		private final CellMatcher cellMatcher;

		private CellMatcherStub(CellMatcher cellMatcher) {
			this.cellMatcher = cellMatcher;
		}

		public CellMatcherStub addFormatter(Formatter formatter) {
			formatters.put(this.cellMatcher, formatter);
			return this;
		}

		public CellMatcherStub addSizer(SizeConstraints sizer) {
			sizeConstraints.put(this.cellMatcher, sizer);
			return this;
		}

		public CellMatcherStub addWrapper(TextWrapper textWrapper) {
			wrappers.put(this.cellMatcher, textWrapper);
			return this;
		}

		public CellMatcherStub addAligner(Aligner aligner) {
			aligners.put(this.cellMatcher, aligner);
			return this;
		}

		public CellMatcherStub on(CellMatcher other) {
			return TableBuilder.this.on(other);
		}

		public TableBuilder and() {
			return TableBuilder.this;
		}

		public Table build() {
			return TableBuilder.this.build();
		}
	}

	public class BorderStub {

		private final BorderStyle style;

		private final int match;

		private BorderStub(BorderStyle style, int match) {
			this.style = style;
			this.match = match;
		}

		public TopLeft fromRowColumn(int row, int column) {
			return new TopLeft(row, column);
		}

		public TopLeft fromTopLeft() {
			return new TopLeft(0, 0);
		}

		public class TopLeft {
			private final int row;

			private final int column;

			private TopLeft(int row, int column) {
				this.row = row;
				this.column = column;
			}

			public TableBuilder toRowColumn(int row, int column) {
				TableBuilder.this.addBorder(TopLeft.this.row, TopLeft.this.column, row, column, BorderStub.this.match, BorderStub.this.style);
				return TableBuilder.this;
			}

			public TableBuilder toBottomRight() {
				return toRowColumn(model.getRowCount(), model.getColumnCount());
			}
		}
	}
}
