/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.component.view.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.geom.Rectangle;
import org.springframework.util.Assert;

/**
 * {@code GridView} is a layout container with no initial {@link View views}.
 *
 * Loosely based on ideas from other grid layouts having features like rows and
 * columns, column and row spanning, dynamic layouts based on container size
 * using "CSS media queries" type of structure.
 *
 * @author Janne Valkealahti
 */
public class GridView extends BoxView {

	private final static Logger log = LoggerFactory.getLogger(GridView.class);
	private List<GridItem> gridItems = new ArrayList<>();
	private int[] columnSize;
	private int[] rowSize;
	private int minWidth;
	private int minHeight;
	private int gapRows;
	private int gapColumns;
	private int rowOffset;
	private int columnOffset;
	private boolean showBorders;

	/**
	 * Defines how the columns of the grid are distributed. Each value defines the
	 * size of one column, starting with the leftmost column. Values greater 0
	 * represent absolute column widths (gaps not included). Values less or equal 0
	 * represent proportional column widths or fractions of the remaining free
	 * space, where 0 is treated the same as -1. That is, a column with a value
	 * of -3 will have three times the width of a column with a value of -1 (or 0).
	 * The minimum width set with {@link #setMinSize(int, int)} is always observed.
	 *
	 * <p>Views may extend beyond the columns defined explicitly with this function. A
	 * value of 0 is assumed for any undefined column. In fact, if you never call
	 * this function, all columns occupied by Views will have the same width. On the
	 * other hand, unoccupied columns defined with this function will always take
	 * their place.
	 *
	 * <p>Assuming a total width of the grid of 100 cells and a minimum width of 0, the
	 * following call will result in columns with widths of 30, 10, 15, 15, and 30
	 * cells:
	 * <p>
	 *
	 * grid.setColumnSize(30, 10, -1, -1, -2)
	 *
	 * <p>If a {@link View} were then placed in the 6th and 7th column, the resulting
	 * widths would be: 30, 10, 10, 10, 20, 10, and 10 cells.
	 *
	 * If you then called setMinSize() as follows:
	 * <p>
	 *
	 * grid.setMinSize(15, 20)
	 *
	 * <p>The resulting widths would be: 30, 15, 15, 15, 20, 15, and 15 cells, a total
	 * of 125 cells, 25 cells wider than the available grid width.
	 *
	 * @param columns the column sizes
	 * @return a grid view for chaining
	 * @see #setRowSize(int...)
	 */
	public GridView setColumnSize(int... columns) {
		this.columnSize = columns;
		return this;
	}

	/**
	 * For documentation see {@link #setColumnSize(int...)} as it's equivalent for rows.
	 *
	 * @param rows the row sizes
	 * @return a grid view for chaining
	 * @see #setColumnSize(int...)
	 */
	public GridView setRowSize(int... rows) {
		this.rowSize = rows;
		return this;
	}

	/**
	 * Sets an absolute minimum width for rows and an absolute minimum height for
	 * columns. Negative values cannot be used.
	 *
	 * @param minWidth the rows minimum width
	 * @param minHeight the columns minimum height
	 * @return a grid view for chaining
	 */
	public GridView setMinSize(int minWidth, int minHeight) {
		Assert.state(minWidth > -1 || minHeight > -1, "Minimum sizes for rows or colums cannot be negative");
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		return this;
	}

	/**
	 * Adds a {@link View} and its position to the grid. The top-left corner of the
	 * {@link View} will be located in the top-left corner of the grid cell at
	 * the given row and column and will span "rowSpan" rows and "colSpan" columns.
	 * For example, for a primitive to occupy rows 2, 3, and 4 and columns 5 and 6:
	 *
	 * <p>
	 * grid.addItem(view, 2, 5, 3, 2, 0, 0)
	 *
	 * If {@code rowSpan} or colSpan is 0, the {@link View} will not be drawn.
	 *
	 * <p>
	 * You can add the same {@link View} multiple times with different grid
	 * positions. The {@code minGridWidth} and {@code minGridHeight} values will
	 * then determine which of those positions will be used. These minimum values
	 * refer to the overall size of the grid. If multiple items for the same
	 * primitive apply, the one that has at least one highest minimum value will
	 * be used, or the {@link View} added last if those values are the same. Example:
	 *
	 * <p>
	 * AddItem(view, 0, 0, 0, 0, 0, 0). // Hide in small grids.
	 * AddItem(view, 0, 0, 1, 2, 100, 0). // One-column layout for medium grids.
	 * AddItem(view, 1, 1, 3, 2, 300, 0) // Multi-column layout for large grids.
	 *
	 * <p>
	 * To use the same grid layout for all sizes, simply set {@code minGridWidth}
	 * and {@code minGridHeight} to 0.
	 *
	 * <p>
	 * If the item's focus is set to true, it will receive focus when the grid
	 * receives focus. If there are multiple items with a true focus flag, the
	 * last visible one that was added will receive focus.
	 *
	 * @param view the view to add
	 * @param row the row
	 * @param column the column
	 * @param rowSpan the row span
	 * @param colSpan the column span
	 * @param minGridHeight the mininum height
	 * @param minGridWidth the minimun width
	 * @return a grid view for chaining
	 */
	public GridView addItem(View view, int row, int column, int rowSpan, int colSpan, int minGridHeight,
			int minGridWidth) {
		GridItem gridItem = new GridItem(view, row, column, colSpan, rowSpan, minGridHeight,
				minGridWidth, false);
		gridItems.add(gridItem);
		return this;
	}

	/**
	 * Remove all items.
	 */
	public void clearItems() {
		this.gridItems.clear();
	}

	/**
	 * Defines if borders is shown.
	 *
	 * @param showBorders the flag showing borders
	 */
	public void setShowBorders(boolean showBorders) {
		this.showBorders = showBorders;
	}

	/**
	 * Returns if borders is shown.
	 *
	 * @return true if borders is shown
	 */
	public boolean isShowBorders() {
		return showBorders;
	}

	@Override
	public MouseHandler getMouseHandler() {
		log.trace("getMouseHandler()");
		return args -> {
			View focus = null;
			for (GridItem i : gridItems) {
				MouseHandlerResult r = i.view.getMouseHandler().handle(args);
				if (r.focus() != null) {
					focus = r.focus();
					break;
				}
			}
			return MouseHandler.resultOf(args.event(), true, focus, null);
		};
	}

	private void nextView() {
		View toFocus = null;
		boolean found = false;
		for (GridItem i : gridItems) {
			if (!i.visible) {
				continue;
			}
			if (toFocus == null) {
				toFocus = i.view;
			}
			if (found) {
				toFocus = i.view;
				break;
			}
			if (i.view.hasFocus()) {
				found = true;
			}
		}
		if (toFocus != null) {
			getViewService().setFocus(toFocus);
		}
	}

	@Override
	protected void initInternal() {
		registerViewCommand(ViewCommand.NEXT_VIEW, () -> nextView());

		registerKeyBinding(Key.Tab, ViewCommand.NEXT_VIEW);
	}

	@Override
	public KeyHandler getKeyHandler() {
		log.trace("getKeyHandler()");
		KeyHandler handler = null;
		for (GridItem i : gridItems) {
			if (i.view.hasFocus()) {
				handler = i.view.getKeyHandler();
				break;
			}
		}
		if (handler != null) {
			return handler.thenIfNotConsumed(super.getKeyHandler());
		}
		return super.getKeyHandler();
	}

	@Override
	public KeyHandler getHotKeyHandler() {
		log.trace("getHotKeyHandler()");
		KeyHandler handler = null;
		for (GridItem i : gridItems) {
			if (handler == null) {
				handler = i.view.getHotKeyHandler();
			}
			else {
				handler = handler.thenIfNotConsumed(i.view.getHotKeyHandler());
			}
			// handler = i.view.getHotKeyHandler();
			// if (i.view.hasFocus()) {
			// 	handler = i.view.getHotKeyHandler();
			// 	break;
			// }
		}
		if (handler != null) {
			return handler.thenIfNotConsumed(super.getHotKeyHandler());
		}
		return super.getHotKeyHandler();
	}

	@Override
	public boolean hasFocus() {
		for (GridItem i : gridItems) {
			if (i.view.hasFocus()) {
				return true;
			}
		}
		return super.hasFocus();
	}

	@Override
	protected void drawInternal(Screen screen) {
		super.drawInternal(screen);
		Rectangle rect = getInnerRect();
		int x = rect.x();
		int y = rect.y();
		int width = rect.width();
		int height = rect.height();

		Map<View, GridItem> items = new HashMap<>();
		for (GridItem item : gridItems) {
			item.visible = false;
			if (item.width <= 0 || item.height <= 0 || width < item.minGridWidth || height < item.minGridHeight) {
				continue;
			}
			GridItem previousItem = items.get(item.view);
			if (previousItem != null && item.minGridWidth < previousItem.minGridWidth
					&& item.minGridHeight < previousItem.minGridHeight) {
				continue;
			}
			items.put(item.view, item);
		}

		// How many rows and columns do we have?
		int rows = rowSize.length;
		int columns = columnSize.length;
		for (GridItem item : items.values()) {
			int rowEnd = item.row + item.height;
			if (rowEnd > rows) {
				rows = rowEnd;
			}
			int columnEnd = item.column + item.width;
			if (columnEnd > columns) {
				columns = columnEnd;
			}
		}
		if (rows == 0 || columns == 0) {
			return;
		}

		// Where are they located?
		int[] rowPos = new int[rows];
		int[] rowHeight = new int[rows];
		int[] columnPos = new int[columns];
		int[] columnWidth = new int[columns];

		// How much space do we distribute?
		int remainingWidth = width;
		int remainingHeight = height;
		int proportionalWidth = 0;
		int proportionalHeight = 0;


		for (int index = 0; index < rowSize.length; index++) {
			int row = rowSize[index];
			if (row > 0) {
				if (row < this.minHeight) {
					row = this.minHeight;
				}
				remainingHeight -= row;
				rowHeight[index] = row;
			}
			else if (row == 0) {
				proportionalHeight++;
			}
			else {
				proportionalHeight += -row;
			}
		}

		for (int index = 0; index < columnSize.length; index++) {
			int column = columnSize[index];
			if (column > 0) {
				if (column < this.minWidth) {
					column = this.minWidth;
				}
				remainingWidth -= column;
				columnWidth[index] = column;
			}
			else if (column == 0) {
				proportionalWidth++;
			}
			else {
				proportionalWidth += -column;
			}
		}

		if (isShowBorders()) {
			remainingHeight -= rows + 1;
			remainingWidth -= columns + 1;
		}
		else {
			remainingHeight -= (rows - 1) * this.gapRows;
			remainingWidth -= (columns - 1) * this.gapColumns;
		}
		if (rows > this.rowSize.length) {
			proportionalHeight += rows - this.rowSize.length;
		}
		if (columns > this.columnSize.length) {
			proportionalWidth += columns - this.columnSize.length;
		}

		// Distribute proportional rows/columns.
		for (int index = 0; index < rows; index++) {
			int row = 0;
			if (index < this.rowSize.length) {
				row = this.rowSize[index];
			}
			if (row > 0) {
				continue;
			}
			else if (row == 0) {
				row = 1;
			}
			else {
				row = -row;
			}
			int rowAbs = row * remainingHeight / proportionalHeight;
			remainingHeight -= rowAbs;
			proportionalHeight -= row;
			if (rowAbs < this.minHeight) {
				rowAbs = this.minHeight;
			}
			rowHeight[index] = rowAbs;
		}

		for (int index = 0; index < columns; index++) {
			int column = 0;
			if (index < this.columnSize.length) {
				column = this.columnSize[index];
			}
			if (column > 0) {
				continue;
			}
			else if (column == 0) {
				column = 1;
			}
			else {
				column = -column;
			}
			int columnAbs = column * remainingWidth / proportionalWidth;
			remainingWidth -= columnAbs;
			proportionalWidth -= column;
			if (columnAbs < this.minWidth) {
				columnAbs = this.minWidth;
			}
			columnWidth[index] = columnAbs;
		}

		// Calculate row/column positions.
		int columnX = 0, rowY = 0;
		if (isShowBorders()) {
			columnX++;
			rowY++;
		}
		for (int index = 0; index < rowHeight.length; index++) {
			int row = rowHeight[index];
			rowPos[index] = rowY;
			int gap = this.gapRows;
			if (isShowBorders()) {
				gap = 1;
			}
			rowY += row + gap;
		}
		for (int index = 0; index < columnWidth.length; index++) {
			int column = columnWidth[index];
			columnPos[index] = columnX;
			int gap = this.gapColumns;
			if (isShowBorders()) {
				gap = 1;
			}
			columnX += column + gap;
		}

		// Calculate primitive positions.
		GridItem focus = null;
		for (Entry<View, GridItem> entry : items.entrySet()) {
			View primitive = entry.getKey();
			GridItem item = entry.getValue();
			int px = columnPos[item.column];
			int py = rowPos[item.row];
			int pw = 0, ph = 0;
			for (int index = 0; index < item.height; index++) {
				ph += rowHeight[item.row + index];
			}
			for (int index = 0; index < item.width; index++) {
				pw += columnWidth[item.column + index];
			}
			if (isShowBorders()) {
				pw += item.width - 1;
				ph += item.height - 1;
			}
			else {
				pw += (item.width - 1) * this.gapColumns;
				ph += (item.height - 1) * this.gapRows;
			}
			item.x = px;
			item.y = py;
			item.w = pw;
			item.h = ph;
			item.visible = true;
			if (primitive.hasFocus()) {
				focus = item;
			}
		}

		// Calculate screen offsets.
		int offsetX = 0;
		int offsetY = 0;
		int add = 1;
		if (!isShowBorders()) {
			add = this.gapRows;
		}
		for (int index = 0; index < rowHeight.length; index++) {
			int height2 = rowHeight[index];
			if (index >= this.rowOffset) {
				break;
			}
			offsetY += height2 + add;
		}
		if (!isShowBorders()) {
			add = this.gapColumns;
		}
		for (int index = 0; index < columnWidth.length; index++) {
			int width2 = columnWidth[index];
			if (index >= this.columnOffset) {
				break;
			}
			offsetX += width2 + add;
		}

		// The focused item must be within the visible area.
		if (focus != null) {
			if (focus.y + focus.h - offsetY >= height) {
				offsetY = focus.y - height + focus.h;
			}
			if (focus.y - offsetY < 0) {
				offsetY = focus.y;
			}
			if (focus.x + focus.w - offsetX >= width) {
				offsetX = focus.x - width + focus.w;
			}
			if (focus.x - offsetX < 0) {
				offsetX = focus.x;
			}
		}

		// Adjust row/column offsets based on this value.
		int from = 0;
		int to = 0;
		for (int index = 0; index < rowPos.length; index ++) {
			int pos = rowPos[index];
			if (pos - offsetY < 0) {
				from = index + 1;
			}
			if (pos - offsetY < height) {
				to = index;
			}
		}
		if (this.rowOffset < from) {
			this.rowOffset = from;
		}
		if (this.rowOffset > to) {
			this.rowOffset = to;
		}

		from = 0;
		to = 0;
		for (int index = 0; index < columnPos.length; index ++) {
			int pos = columnPos[index];
			if (pos - offsetX < 0) {
				from = index + 1;
			}
			if (pos - offsetX < width) {
				to = index;
			}
		}
		if (this.columnOffset < from) {
			this.columnOffset = from;
		}
		if (this.columnOffset > to) {
			this.columnOffset = to;
		}

		// Draw primitives and borders.
		for (Entry<View, GridItem> entry : items.entrySet()) {
			View view = entry.getKey();
			GridItem item = entry.getValue();

			// Final primitive position.
			if (!item.visible) {
				continue;
			}

			item.x -= offsetX;
			item.y -= offsetY;
			if (item.x >= width || item.x + item.w <= 0 || item.y >= height || item.y + item.h <= 0) {
				item.visible = false;
				continue;
			}

			if (item.x + item.w > width) {
				item.w = width - item.x;
			}
			if (item.y + item.h > height) {
				item.h = height - item.y;
			}
			if (item.x < 0) {
				item.w += item.x;
				item.x = 0;
			}
			if (item.y < 0) {
				item.h += item.y;
				item.y = 0;
			}
			if (item.w <= 0 || item.h <= 0) {
				item.visible = false;
				continue;
			}

			item.x += x;
			item.y += y;
			view.setRect(item.x, item.y, item.w, item.h);

			view.draw(screen);

			// Draw border around primitive.
			if (isShowBorders()) {
				screen.writerBuilder().build().border(item.x - 1, item.y - 1, item.w + 2, item.h + 2);
			}
		}
	}

	private static class GridItem {
		View view;
		int row;
		int column;
		int width;
		int height;
		int minGridHeight;
		int minGridWidth;
		boolean visible;
		// The last position of the item relative to the top-left
		// corner of the grid. Undefined if visible is false.
		int x, y, w, h;

		GridItem(View view, int row, int column, int width, int height, int minGridHeight, int minGridWidth,
				boolean visible) {
			this.view = view;
			this.row = row;
			this.column = column;
			this.width = width;
			this.height = height;
			this.minGridHeight = minGridHeight;
			this.minGridWidth = minGridWidth;
			this.visible = visible;
		}
	}
}
