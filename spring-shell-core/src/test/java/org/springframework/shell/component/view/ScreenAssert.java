/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.component.view;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.AbstractAssert;
import org.jline.utils.AttributedString;

import org.springframework.shell.component.view.screen.DisplayLines;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.shell.geom.Position;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Asserts for {@link Screen}. This is a work-in-progress implementation and
 * relevant parts are copied into spring-shell-test.
 *
 * @author Janne Valkealahti
 */
public class ScreenAssert extends AbstractAssert<ScreenAssert, Screen> {

	public ScreenAssert(Screen actual) {
		super(actual, ScreenAssert.class);
	}

	/**
	 * Verifies that the actual {@link Screen} has a cursor visible.
	 *
	 * @return this assertion object
	 */
	public ScreenAssert hasCursorVisible() {
		isNotNull();
		if (!actual.isShowCursor()) {
			failWithMessage("Expecting a Screen to have a visible cursor", actual);
		}
		return this;
	}

	/**
	 * Verifies that the actual {@link Screen} has a cursor in a given position.
	 *
	 * @param x a x position in a screen
	 * @param y a y position in a screen
	 * @return this assertion object
	 */
	public ScreenAssert hasCursorInPosition(int x, int y) {
		isNotNull();
		Position cursorPosition = actual.getCursorPosition();
		if (cursorPosition.x() != x || cursorPosition.y() != y) {
			failWithMessage("Expecting a Screen to have position <%s,%s> but was <%s,%s>", x, y, cursorPosition.x(),
					cursorPosition.y());
		}
		return this;
	}

	/**
	 * Verifies that the actual {@link Screen} has a foreground color in a position.
	 *
	 * @param x a x position in a screen
	 * @param y a y position in a screen
	 * @param color the color
	 * @return this assertion object
	 */
	public ScreenAssert hasForegroundColor(int x, int y, int color) {
		isNotNull();
		ScreenItem[][] items = actual.getItems();
		ScreenItem i = items[y][x];
		int expectedColor = i.getForeground();
		if (expectedColor != color) {
			failWithMessage("Expecting a Screen to have foreground color <%s> position <%s,%s> but was <%s>", color, x, y,
					expectedColor);
		}
		return this;
	}

	/**
	 * Verifies that the actual {@link Screen} has a foreground style in a position.
	 *
	 * @param x a x position in a screen
	 * @param y a y position in a screen
	 * @param color the style
	 * @return this assertion object
	 */
	public ScreenAssert hasStyle(int x, int y, int style) {
		isNotNull();
		ScreenItem[][] items = actual.getItems();
		ScreenItem i = items[y][x];
		int expectedStyle = i.getStyle();
		if (expectedStyle != style) {
			failWithMessage("Expecting a Screen to have style <%s> position <%s,%s> but was <%s>", style, x, y,
			expectedStyle);
		}
		return this;
	}

	/**
	 * Verifies that the actual {@link Screen} has a background color in a position.
	 *
	 * @param x a x position in a screen
	 * @param y a y position in a screen
	 * @param color the color
	 * @return this assertion object
	 */
	public ScreenAssert hasBackgroundColor(int x, int y, int color) {
		isNotNull();
		ScreenItem[][] items = actual.getItems();
		ScreenItem i = items[y][x];
		int expectedColor = i.getBackground();
		if (expectedColor != color) {
			failWithMessage("Expecting a Screen to have background color <%s> position <%s,%s> but was <%s>", color, x, y,
					expectedColor);
		}
		return this;
	}

	/**
	 * Verifies that a given bounded box is legal for a screen and that characters
	 * along border look like border characters.
	 *
	 * @param x a x position in a screen
	 * @param y a y position in a screen
	 * @param width  a width in a screen
	 * @param height a height in a screen
	 * @return this assertion object
	 */
	public ScreenAssert hasBorder(int x, int y, int width, int height) {
		return hasBorderType(x, y, width, height, true);
	}

	/**
	 * Verifies that a given bounded box is legal for a screen and that characters
	 * along border doesn't look like border characters.
	 *
	 * @param x a x position in a screen
	 * @param y a y position in a screen
	 * @param width  a width in a screen
	 * @param height a height in a screen
	 * @return this assertion object
	 */
	public ScreenAssert hasNoBorder(int x, int y, int width, int height) {
		return hasBorderType(x, y, width, height, false);
	}

	/**
	 * Verifies that a given text can be found from a screen coordinates following
	 * horizontal width.
	 *
	 * @param text a text to verify
	 * @param x a x position of a text
	 * @param y a y position of a text
	 * @param width a width of a text to check
	 * @return this assertion object
	 */
	public ScreenAssert hasHorizontalText(String text, int x, int y, int width) {
		isNotNull();
		ScreenItem[][] content = actual.getItems();
		checkBounds(content, x, y, width, 1);
		ScreenItem[] items = getHorizontalBorder(content, y, x, width);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getContent() != null) {
				buf.append(items[i].getContent());
			}
		}
		String actualText = buf.toString();
		assertThat(actualText).isEqualTo(text);
		return this;
	}

	/**
	 * Verifies that a given text can not be found from a screen coordinates following
	 * horizontal width.
	 *
	 * @param text a text to verify
	 * @param x a x position of a text
	 * @param y a y position of a text
	 * @param width a width of a text to check
	 * @return this assertion object
	 */
	public ScreenAssert hasNoHorizontalText(String text, int x, int y, int width) {
		isNotNull();
		ScreenItem[][] content = actual.getItems();
		checkBounds(content, x, y, width, 1);
		ScreenItem[] items = getHorizontalBorder(content, x, y, width);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				if (items[i].getContent() != null) {
					buf.append(items[i].getContent());
				}
			}
		}
		String actualText = buf.toString();
		assertThat(actualText).isNotEqualTo(text);
		return this;
	}

	private String screenError(int x, int y, int width, int height) {
		StringBuffer buf = new StringBuffer();
		if (actual instanceof DisplayLines dl1) {
			List<AttributedString> screenLines = dl1.getScreenLines();
			buf.append(String.format("%nExpecting screen:%n"));
			for (AttributedString line : screenLines) {
				buf.append(String.format("%n  %s", AttributedString.stripAnsi(line.toString())));
			}
			Screen clip = actual.clip(x, y, width, height);
			if (clip instanceof DisplayLines dl2) {
				List<AttributedString> screenLines2 = dl2.getScreenLines();
				buf.append(String.format("%nhave border in bounded box x=%s y=%s width=%s height=%s, was:%n", x, y, width, height));
				for (AttributedString line : screenLines2) {
					buf.append(String.format("%n  %s", AttributedString.stripAnsi(line.toString())));
				}
			}
		}
		return buf.toString();
	}

	private ScreenAssert hasBorderType(int x, int y, int width, int height, boolean border) {
		isNotNull();
		ScreenItem[][] content = actual.getItems();
		checkBounds(content, x, y, width, height);
		ScreenItem[][] borders = getBorders(content, x, y, width, height);
		ScreenItem[] topBorder = borders[0];
		ScreenItem[] rightBorder = borders[1];
		ScreenItem[] bottomBorder = borders[2];
		ScreenItem[] leftBorder = borders[3];
		if (topBorder.length != width) {
			failWithMessage("Top Border size doesn't match");
		}
		String failMessage = screenError(x, y, width, height);
		assertThat(topBorder).withFailMessage(failMessage).allSatisfy(b -> {
			if (border) {
				assertThat(b).isNotNull();
				assertThat(b.getBorder()).isGreaterThan(0);
			}
			else {
				if (b != null) {
					assertThat(b.getBorder()).isEqualTo(0);
				}
			}
		});
		assertThat(rightBorder).withFailMessage(failMessage).allSatisfy(b -> {
			if (border) {
				assertThat(b).isNotNull();
				assertThat(b.getBorder()).isGreaterThan(0);
			}
			else {
				if (b != null) {
					assertThat(b.getBorder()).isEqualTo(0);
				}
			}
		});
		assertThat(bottomBorder).withFailMessage(failMessage).allSatisfy(b -> {
			if (border) {
				assertThat(b).isNotNull();
				assertThat(b.getBorder()).isGreaterThan(0);
			}
			else {
				if (b != null) {
					assertThat(b.getBorder()).isEqualTo(0);
				}
			}
		});
		assertThat(leftBorder).withFailMessage(failMessage).allSatisfy(b -> {
			if (border) {
				assertThat(b).isNotNull();
				// assertThat(b.getType()).isEqualTo(Screen.Type.BORDER);
				assertThat(b.getBorder()).isGreaterThan(0);
			}
			else {
				if (b != null) {
					// assertThat(b.getType()).isNotEqualTo(Screen.Type.BORDER);
					assertThat(b.getBorder()).isEqualTo(0);
				}
			}
		});
		return this;
	}

	private ScreenItem[][] getBorders(ScreenItem[][] content, int x, int y, int width, int height) {
		ScreenItem[] topBorder = getHorizontalBorder(content, y, x, width);
		ScreenItem[] rightBorder = getVerticalBorder(content, x + width - 1, y, height);
		ScreenItem[] bottomBorder = getHorizontalBorder(content, y + height - 1, x, width);
		ScreenItem[] leftBorder = getVerticalBorder(content, x, y, height);
		return new ScreenItem[][] { topBorder, rightBorder, bottomBorder, leftBorder };
	}

	private ScreenItem[] getHorizontalBorder(ScreenItem[][] content, int row, int start, int width) {
		return Arrays.copyOfRange(content[row], start, start + width);
	}

	private ScreenItem[] getVerticalBorder(ScreenItem[][] content, int column, int start, int height) {
		ScreenItem[] array = new ScreenItem[height];
		for (int i = 0; i < array.length; i++) {
			array[i] = content[start][column];
		}
		return array;
	}

	private void checkBounds(ScreenItem[][] content, int x, int y, int width, int height) {
		if (x < 0 || y < 0 || width < 1 || height < 1) {
			failWithMessage("Can't assert with negative bounded rectangle, was x=%s y=%s width=%s height=%s", x, y,
					width, height);
		}
		if (x >= content[0].length) {
			failWithMessage("Can't assert position x %s as width is %s", x, content[0].length);
		}
	}
}
