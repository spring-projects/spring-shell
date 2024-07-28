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
package org.springframework.shell.component.view.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.Position;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeResolver.ResolvedValues;
import org.springframework.util.Assert;

/**
 * Default implementation of a {@link Screen}.
 *
 * @author Janne Valkealahti
 */
public class DefaultScreen implements Screen, DisplayLines {

	private final static Logger log = LoggerFactory.getLogger(DefaultScreen.class);
	private boolean showCursor;
	private Position cursorPosition = new Position(0, 0);
	private int rows = 0;
	private int columns = 0;

	public DefaultScreen() {
		this(0, 0);
	}

	public DefaultScreen(int rows, int columns) {
		resize(rows, columns);
	}

	@Override
	public WriterBuilder writerBuilder() {
		return new DefaultWriterBuilder();
	}

	@Override
	public void setShowCursor(boolean showCursor) {
		this.showCursor = showCursor;
	}

	@Override
	public boolean isShowCursor() {
		return showCursor;
	}

	@Override
	public void setCursorPosition(Position cursorPosition) {
		this.cursorPosition = cursorPosition;
	}

	@Override
	public Position getCursorPosition() {
		return cursorPosition;
	}

	@Override
	public void resize(int rows, int columns) {
		Assert.isTrue(rows > -1, "Cannot have negative rows size");
		Assert.isTrue(columns > -1, "Cannot have negative columns size");
		this.rows = rows;
		this.columns = columns;
		reset();
		log.trace("Screen reset rows={} cols={}", this.rows, this.columns);
	}

	@Override
	public ScreenItem[][] getItems() {
		return getScreenItems();
	}

	@Override
	public Screen clip(int x, int y, int width, int height) {
		return null;
	}

	private static char[] BOX_CHARS = new char[] { ' ', '╴', '╵', '┌', '╶', '─', '┐', '┬', '╷', '└', '│', '├', '┘', '┴',
			'┤', '┼' };

	@Override
	public List<AttributedString> getScreenLines() {
		List<AttributedString> newLines = new ArrayList<>();
		ScreenItem[][] items = getScreenItems();
		for (int i = 0; i < items.length; i++) {
			AttributedStringBuilder builder = new AttributedStringBuilder();
			for (int j = 0; j < items[i].length; j++) {
				DefaultScreenItem item = (DefaultScreenItem) items[i][j];
				if (item != null) {
					AttributedStyle s = new AttributedStyle(AttributedStyle.DEFAULT);
					if (item.background > -1) {
						s = s.backgroundRgb(item.getBackground());
					}
					if (item.foreground > -1) {
						s = s.foregroundRgb(item.getForeground());
					}
					if (item.style > -1) {
						if ((item.style & ScreenItem.STYLE_BOLD) == ScreenItem.STYLE_BOLD) {
							s = s.bold();
						}
						if ((item.style & ScreenItem.STYLE_FAINT) == ScreenItem.STYLE_FAINT) {
							s = s.faint();
						}
						if ((item.style & ScreenItem.STYLE_ITALIC) == ScreenItem.STYLE_ITALIC) {
							s = s.italic();
						}
						if ((item.style & ScreenItem.STYLE_UNDERLINE) == ScreenItem.STYLE_UNDERLINE) {
							s = s.underline();
						}
						if ((item.style & ScreenItem.STYLE_BLINK) == ScreenItem.STYLE_BLINK) {
							s = s.blink();
						}
						if ((item.style & ScreenItem.STYLE_INVERSE) == ScreenItem.STYLE_INVERSE) {
							s = s.inverse();
						}
						if ((item.style & ScreenItem.STYLE_CONCEAL) == ScreenItem.STYLE_CONCEAL) {
							s = s.conceal();
						}
						if ((item.style & ScreenItem.STYLE_CROSSEDOUT) == ScreenItem.STYLE_CROSSEDOUT) {
							s = s.crossedOut();
						}
					}
					if (item.getContent() != null){
						builder.append(item.getContent(), s);
					}
					else if (item.getBorder() > 0) {
						builder.append(Character.toString(BOX_CHARS[item.getBorder()]), s);
					}
					else {
						builder.append(Character.toString(' '), s);
					}
				}
				else {
					builder.append(' ');
				}
			}
			newLines.add(builder.toAttributedString());
		}
		return newLines;
	}

	/**
	 * Default private implementation of a {@link ScreenItem}.
	 */
	private static class DefaultScreenItem implements ScreenItem {

		CharSequence content;
		int foreground = -1;
		int background = -1;
		int style = -1;
		int border;

		@Override
		public CharSequence getContent() {
			return content;
		}

		@Override
		public int getBorder() {
			return border;
		}

		@Override
		public int getBackground() {
			return background;
		}

		@Override
		public int getForeground() {
			return foreground;
		}

		@Override
		public int getStyle() {
			return style;
		}

	}

	/**
	 * Default private implementation of a {@link WriterBuilder}.
	 */
	private class DefaultWriterBuilder implements WriterBuilder {

		int layer;
		int color = -1;
		int style = -1;

		@Override
		public Writer build() {
			return new DefaultWriter(layer, color, style);
		}

		@Override
		public WriterBuilder layer(int index) {
			this.layer = index;
			return this;
		}

		@Override
		public WriterBuilder color(int color) {
			this.color = color;
			return this;
		}

		@Override
		public WriterBuilder style(int style) {
			this.style = style;
			return this;
		}
	}

	private void reset() {
		layers.clear();
	}

	private class Layer {
		DefaultScreenItem[][] items = new DefaultScreenItem[rows][columns];

		DefaultScreenItem getScreenItem(int x, int y) {
			if (y < rows && x < columns) {
				if (items[y][x] == null) {
					items[y][x] = new DefaultScreenItem();
				}
				return items[y][x];
			}
			else {
				return null;
			}
		}
	}

	private Map<Integer, Layer> layers = new TreeMap<>();

	private Layer getLayer(int index) {
		Layer layer = layers.computeIfAbsent(index, l -> {
			return new Layer();
		});
		return layer;
	}

	public ScreenItem[][] getScreenItems() {
		DefaultScreenItem[][] projection = new DefaultScreenItem[rows][columns];
		layers.entrySet().stream().forEach(entry -> {
			Layer layer = entry.getValue();
			DefaultScreenItem[][] layerItems = layer.items;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if (layerItems[i][j] != null) {
						projection[i][j] = layerItems[i][j];
					}
				}
			}

		});
		return projection;
	}

	/**
	 * Default private implementation of a {@link Writer}.
	 */
	private class DefaultWriter implements Writer {

		int index;
		int color = -1;
		int style = -1;

		DefaultWriter(int index, int color, int style) {
			this.index = index;
			this.color = color;
			this.style = style;
		}

		@Override
		public void text(String text, int x, int y) {
			Layer layer = getLayer(index);
			for (int i = 0; i < text.length() && i < columns; i++) {
				char c = text.charAt(i);
				DefaultScreenItem item = layer.getScreenItem(x + i, y);
				if (item != null) {
					item.content = Character.toString(c);
					if (color > -1) {
						item.foreground = color;
					}
					if (style > -1) {
						item.style = style;
					}
				}
			}
		}

		@Override
		public void text(AttributedString text, int x, int y) {
			Layer layer = getLayer(index);
			for (int i = 0; i < text.length() && i < columns; i++) {
				DefaultScreenItem item = layer.getScreenItem(x + i, y);
				if (item != null) {
					char c = text.charAt(i);
					AttributedStyle as = text.styleAt(i);
					ResolvedValues rv = ThemeResolver.resolveValues(as);
					item.content = Character.toString(c);
					if (rv.foreground() > -1) {
						item.foreground = rv.foreground();
					}
					if (rv.style() > -1) {
						item.style = rv.style();
					}
					if (rv.background() > -1) {
						item.background = rv.background();
					}
				}
			}
		}

		@Override
		public void border(int x, int y, int width, int height) {
			log.trace("PrintBorder rows={}, columns={}, x={}, y={}, width={}, height={}", rows, columns, x, y, width,
					height);
			printBorderHorizontal(x, y, width);
			printBorderHorizontal(x, y + height - 1, width);
			printBorderVertical(x, y, height);
			printBorderVertical(x + width - 1, y, height);
		}

		@Override
		public void background(Rectangle rect, int color) {
			log.trace("Background {} {}", color, rect);
			Layer layer = getLayer(index);
			for (int i = rect.y(); i < rect.y() + rect.height(); i++) {
				for (int j = rect.x(); j < rect.x() + rect.width(); j++) {
					DefaultScreenItem item = layer.getScreenItem(j, i);
					if (item != null) {
						item.background = color;
					}
				}
			}
		}

		@Override
		public void text(String text, Rectangle rect, HorizontalAlign hAlign, VerticalAlign vAlign) {
			int x = rect.x();
			if (hAlign == HorizontalAlign.CENTER) {
				x = x + rect.width() / 2;
				x = x - text.length() / 2;
			}
			else if (hAlign == HorizontalAlign.RIGHT) {
				x = x + rect.width() - text.length();
			}
			int y = rect.y();
			if (vAlign == VerticalAlign.CENTER) {
				y = y + rect.height() / 2;
			}
			else if (vAlign == VerticalAlign.BOTTOM) {
				y = y + rect.height() - 1;
			}
			text(text, x, y);
		}

		private void printBorderHorizontal(int x, int y, int width) {
			Layer layer = getLayer(index);
			for (int i = x; i < x + width; i++) {
				if (i < 0 || i >= columns) {
					continue;
				}
				if (y >= rows) {
					continue;
				}
				DefaultScreenItem item = layer.getScreenItem(i, y);
				if (i > x) {
					item.border |= ScreenItem.BORDER_RIGHT;
				}
				if (i < x + width - 1) {
					item.border |= ScreenItem.BORDER_LEFT;
				}
			}
		}

		private void printBorderVertical(int x, int y, int height) {
			Layer layer = getLayer(index);
			for (int i = y; i < y + height; i++) {
				if (i < 0 || i >= rows) {
					continue;
				}
				if (x >= columns) {
					continue;
				}
				DefaultScreenItem item = layer.getScreenItem(x, i);
				if (i > y) {
					item.border |= ScreenItem.BORDER_BOTTOM;
				}
				if (i < y + height - 1) {
					item.border |= ScreenItem.BORDER_TOP;
				}
			}
		}


	}
}
