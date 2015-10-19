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

import static org.springframework.shell.table.BorderSpecification.NONE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.shell.TerminalSizeAware;

/**
 * This is the central API for table rendering. A Table object is constructed with a given
 * TableModel, which holds raw table contents. Its rendering logic is then altered by applying
 * various customizations, in a fashion very similar to what is used <i>e.g.</i> in a spreadsheet
 * program:<ol>
 * <li>{@link #format(CellMatcher, Formatter) formatters} know how to derive character data out of raw data. For
 * example, numbers are
 * formatted according to a Locale, or Maps are emitted as a series of {@literal key=value} lines</li>
 * <li>{@link #size(CellMatcher, SizeConstraints) size constraints} are then applied, which decide how
 * much column real estate to allocate to cells</li>
 * <li>{@link #wrap(CellMatcher, TextWrapper) text wrapping policies} are applied once the column sizes
 * are known</li>
 * <li>finally, {@link #align(CellMatcher, Aligner) alignment} strategies actually render
 * text as a series of space-padded strings that draw nicely on screen.</li>
 * </ol>
 * All those customizations are applied selectively on the Table cells thanks to a {@link CellMatcher}: One can
 * decide to right pad column number 3, or to format in a certain way all instances of {@literal java.util.Map}.
 *
 * <p>Of course, all of those customizations often work hand in hand, and not all combinations make sense:
 * one needs to anticipate the fact that text will be split using the ' ' (space) character to properly
 * calculate column sizes.</p>
 * @author Eric Bottard
 */
public class Table implements TerminalSizeAware {

	private final int rows;

	private final int columns;

	private TableModel model;

	private Map<CellMatcher, Formatter> formatters = new LinkedHashMap<CellMatcher, Formatter>();

	private Map<CellMatcher, SizeConstraints> sizeConstraints = new LinkedHashMap<CellMatcher, SizeConstraints>();

	private Map<CellMatcher, TextWrapper> wrappers = new LinkedHashMap<CellMatcher, TextWrapper>();

	private Map<CellMatcher, Aligner> aligners = new LinkedHashMap<CellMatcher, Aligner>();

	private List<BorderSpecification> borderSpecifications = new ArrayList<BorderSpecification>();

	/**
	 * Construct a new Table with the given model and customizers.
	 * The passed in LinkedHashMap should be in reverse-insertion order (<i>i.e.</i> the first CellMatcher
	 * found in iteration order will "win").
	 *
	 * @see TableBuilder#build()
	 */
	/*package*/ Table(TableModel model,
	      LinkedHashMap<CellMatcher, Formatter> formatters,
	      LinkedHashMap<CellMatcher, SizeConstraints> sizeConstraints,
	      LinkedHashMap<CellMatcher, TextWrapper> wrappers,
	      LinkedHashMap<CellMatcher, Aligner> aligners,
	      List<BorderSpecification> borderSpecifications) {
		this.model = model;
		this.formatters = formatters;
		this.sizeConstraints = sizeConstraints;
		this.wrappers = wrappers;
		this.aligners = aligners;
		this.borderSpecifications = borderSpecifications;
		rows = model.getRowCount();
		columns = model.getColumnCount();

	}

	public TableModel getModel() {
		return model;
	}

	public String render(int totalAvailableWidth) {
		StringBuilder result = new StringBuilder();

		int[] cellHeights = new int[rows];
		int[] cellWidths;
		int[] minCellWidths = new int[columns];
		int[] maxCellWidths = new int[columns];

		String[][][] subLines = new String[rows][columns][];

		Borders borders = new Borders();
		int widthAvailableForContents = totalAvailableWidth - borders.getNumberOfVerticalBorders();

		// First, compute desired column widths
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				Object value = model.getValue(row, column);
				String[] lines = getFormatter(row, column).format(value);
				subLines[row][column] = lines;

				SizeConstraints.Extent extent = getSizeConstraints(row, column).width(lines, widthAvailableForContents, columns);

				minCellWidths[column] = Math.max(minCellWidths[column], extent.min);
				maxCellWidths[column] = Math.max(maxCellWidths[column], extent.max);

			}
		}


		cellWidths = computeColumnWidths(widthAvailableForContents, minCellWidths, maxCellWidths);
		// Now that widths are known, apply wrapping & render
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				subLines[row][column] = getWrapper(row, column).wrap(subLines[row][column], cellWidths[column]);
				cellHeights[row] = Math.max(cellHeights[row], subLines[row][column].length);
			}
			for (int column = 0; column < columns; column++) {
				for (Map.Entry<CellMatcher, Aligner> kv : aligners.entrySet()) {
					if (kv.getKey().matches(row, column, model)) {
						subLines[row][column] = kv.getValue().align(subLines[row][column], cellWidths[column], cellHeights[row]);
					}
				}
			}
		}


		for (int row = 0; row < rows; row++) {

			// TOP CELL BORDER
			int before = result.length();
			for (int column = 0; column < columns; column++) {
				borders.paintCorner(row, column, result);
				borders.paintHorizontal(row, column, cellWidths[column], result);
			}
			borders.paintCorner(row, columns, result);
			if (result.length() > before) {
				result.append('\n');
			}

			for (int subRow = 0; subRow < cellHeights[row]; subRow++) {
				for (int column = 0; column < columns; column++) {
					// LEFT CELL BORDER
					borders.paintVertical(row, column, result);
					String[] lines = subLines[row][column];
					result.append(lines[subRow]);
				}
				// TABLE RIGHT BORDER
				borders.paintVertical(row, columns, result);
				result.append("\n");
			}
		}

		// TABLE BOTTOM BORDER
		int before = result.length();
		for (int column = 0; column < columns; column++) {
			borders.paintCorner(rows, column, result);
			borders.paintHorizontal(rows, column, cellWidths[column], result);
		}

		// TABLE BOTTOM RIGHT CORNER
		borders.paintCorner(rows, columns, result);
		if (result.length() > before) {
			result.append('\n');
		}
		return result.toString();
	}

	private int[] computeColumnWidths(int availableWidth, int[] minCellWidths, int[] maxCellWidths) {

		int[] cellWidths;
		int minTableWidth = 0, maxTableWidth = 0;
		for (int column = 0; column < columns; column++) {
			minTableWidth += minCellWidths[column];
			maxTableWidth += maxCellWidths[column];
		}

		// Can use max desired width
		if (maxTableWidth <= availableWidth) {
			cellWidths = maxCellWidths;
		} // will overflow
		else if (minTableWidth >= availableWidth) {
			cellWidths = minCellWidths;
		} // Redistribute nicely
		else {
			int W = availableWidth - minTableWidth;
			int D = maxTableWidth - minTableWidth;
			cellWidths = new int[columns];
			for (int column = 0; column < columns; column++) {
				cellWidths[column] = minCellWidths[column] + W * (maxCellWidths[column] - minCellWidths[column]) / D;
			}
		}
		return cellWidths;
	}

	private TextWrapper getWrapper(int row, int column) {
		for (Map.Entry<CellMatcher, TextWrapper> kv : wrappers.entrySet()) {
			if (kv.getKey().matches(row, column, model)) {
				return kv.getValue();
			}
		}
		throw new AssertionError("Can't be reached thanks to the whole-table default");
	}

	private SizeConstraints getSizeConstraints(int row, int column) {
		for (Map.Entry<CellMatcher, SizeConstraints> kv : sizeConstraints.entrySet()) {
			if (kv.getKey().matches(row, column, model)) {
				return kv.getValue();
			}
		}
		throw new AssertionError("Can't be reached thanks to the whole-table default");
	}

	private Formatter getFormatter(int row, int column) {
		for (Map.Entry<CellMatcher, Formatter> kv : formatters.entrySet()) {
			if (kv.getKey().matches(row, column, model)) {
				return  kv.getValue();
			}
		}
		throw new AssertionError("Can't be reached thanks to the whole-table default");
	}

	/**
	 * An instance of this class knows where to paint border glyphs.
	 *
	 * <p>In all instance arrays, 'row' and 'column' are actually indices in-between
	 * table rows and columns. Hence, sizes are larger by one.</p>
	 * @author Eric Bottard
	 */
	private class Borders {

		/**
		 * Glyph to paint a vertical line at row,col.
		 */
		private char[][] verticals;

		/**
		 * Glyph to paint a horizontal line at row,col.
		 */
		private char[][] horizontals;

		/**
		 * The type of corner, if any, to paint at row,col.
		 */
		private char[][] corners;

		/**
		 * True if at least one vertical bar exists in that col.
		 */
		private boolean[] vFillers;

		/**
		 * True if at least one horizontal bar exists in that row.
		 */
		private boolean[] hFillers;

		public Borders() {
			verticals = new char[rows][columns + 1];
			horizontals = new char[rows + 1][columns];
			corners = new char[rows + 1][columns + 1];
			vFillers = new boolean[columns + 1];
			hFillers = new boolean[rows + 1];
			init();
		}

		private void init() {

			for (int row = 0; row <= rows; row++) {
				for (int column = 0; column <= columns; column++) {
					for (BorderSpecification bs : borderSpecifications) {
						if (row < rows) {
							char verticalThere = bs.verticals(row, column);
							if (verticalThere != BorderStyle.NONE) {
								this.verticals[row][column] = verticalThere;
								vFillers[column] |= true;
							}
						}
						if (column < columns) {
							char horizontalThere = bs.horizontals(row, column);
							if (horizontalThere != BorderStyle.NONE) {
								this.horizontals[row][column] = horizontalThere;
								hFillers[row] |= true;
							}
						}
					}
				}
			}

			// Compute corners when horizontals & verticals intersect
			for (int row = 0; row <= rows; row++) {
				for (int column = 0; column <= columns; column++) {
					char left = (column - 1 >= 0) ? horizontals[row][column - 1] : NONE;
					char right = (column < columns) ? horizontals[row][column] : NONE;
					char above = (row - 1 >= 0) ? verticals[row - 1][column] : NONE;
					char below = (row < rows) ? verticals[row][column] : NONE;
					corners[row][column] = BorderStyle.intersection(above, below, left, right);
				}
			}
		}

		private void paintCorner(int row, int column, StringBuilder stringBuilder) {
			if (corners[row][column] != NONE) {
				stringBuilder.append(corners[row][column]);
			} // If there is a border in same row|column, paint filler
			else if (vFillers[column] && hFillers[row]) {
				stringBuilder.append(' ');
			}
		}

		private void paintVertical(int row, int column, StringBuilder stringBuilder) {
			if (verticals[row][column] != NONE) {
				stringBuilder.append(verticals[row][column]);
			}
			else if (vFillers[column]) {
				stringBuilder.append(' ');
			}
		}

		private void paintHorizontal(int row, int column, int width, StringBuilder stringBuilder) {
			if (horizontals[row][column] != NONE) {
				for (int i = 0; i < width; i++) {
					stringBuilder.append(horizontals[row][column]);
				}
			}
			else if (hFillers[row]) {
				for (int i = 0; i < width; i++) {
					stringBuilder.append(' ');
				}
			}
		}

		/**
		 * Return the number of vertical borders, and hence the space consumed by those.
		 */
		public int getNumberOfVerticalBorders() {
			int result = 0;
			for (boolean b : vFillers) {
				if (b) {
					result++;
				}
			}
			return result;
		}
	}

}
