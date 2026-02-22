/*
 * Copyright 2025-present the original author or authors.
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
package org.springframework.shell.jline.tui.component.view.screen;

import java.util.List;

import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;

import org.springframework.shell.jline.tui.geom.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultScreenTests {

	@Test
	void shouldCreateWithDimensions() {
		// given
		DefaultScreen screen = new DefaultScreen(10, 20);

		// then
		ScreenItem[][] items = screen.getItems();
		assertEquals(10, items.length);
		assertEquals(20, items[0].length);
	}

	@Test
	void shouldCreateWithZeroDimensions() {
		// given
		DefaultScreen screen = new DefaultScreen();

		// then
		ScreenItem[][] items = screen.getItems();
		assertEquals(0, items.length);
	}

	@Test
	void shouldResizeScreen() {
		// given
		DefaultScreen screen = new DefaultScreen(5, 5);

		// when
		screen.resize(10, 15);

		// then
		ScreenItem[][] items = screen.getItems();
		assertEquals(10, items.length);
		assertEquals(15, items[0].length);
	}

	@Test
	void resizeWithNegativeRowsShouldThrow() {
		// given
		DefaultScreen screen = new DefaultScreen();

		// when / then
		assertThrows(IllegalArgumentException.class, () -> screen.resize(-1, 10));
	}

	@Test
	void resizeWithNegativeColumnsShouldThrow() {
		// given
		DefaultScreen screen = new DefaultScreen();

		// when / then
		assertThrows(IllegalArgumentException.class, () -> screen.resize(10, -1));
	}

	@Test
	void shouldWriteTextToScreen() {
		// given
		DefaultScreen screen = new DefaultScreen(5, 20);
		Screen.Writer writer = screen.writerBuilder().build();

		// when
		writer.text("Hello", 0, 0);

		// then
		ScreenItem[][] items = screen.getItems();
		assertNotNull(items[0][0]);
		assertEquals("H", items[0][0].getContent().toString());
		assertEquals("e", items[0][1].getContent().toString());
		assertEquals("l", items[0][2].getContent().toString());
		assertEquals("l", items[0][3].getContent().toString());
		assertEquals("o", items[0][4].getContent().toString());
	}

	@Test
	void shouldSetAndGetCursorPosition() {
		// given
		DefaultScreen screen = new DefaultScreen(10, 10);
		Position newPosition = new Position(5, 3);

		// when
		screen.setCursorPosition(newPosition);

		// then
		assertEquals(newPosition, screen.getCursorPosition());
	}

	@Test
	void shouldSetAndGetShowCursor() {
		// given
		DefaultScreen screen = new DefaultScreen(10, 10);

		// when
		screen.setShowCursor(true);

		// then
		assertTrue(screen.isShowCursor());

		// when
		screen.setShowCursor(false);

		// then
		assertFalse(screen.isShowCursor());
	}

	@Test
	void clipShouldReturnNull() {
		// given
		DefaultScreen screen = new DefaultScreen(10, 10);

		// when / then
		assertNull(screen.clip(0, 0, 5, 5));
	}

	@Test
	void getScreenLinesShouldReturnAttributedStrings() {
		// given
		DefaultScreen screen = new DefaultScreen(3, 10);
		Screen.Writer writer = screen.writerBuilder().build();
		writer.text("Hi", 0, 0);

		// when
		List<AttributedString> lines = screen.getScreenLines();

		// then
		assertEquals(3, lines.size());
		assertTrue(lines.get(0).toString().startsWith("Hi"));
	}

	@Test
	void writerBuilderShouldSupportLayers() {
		// given
		DefaultScreen screen = new DefaultScreen(5, 10);

		// when - write on layer 0 and layer 1
		Screen.Writer writer0 = screen.writerBuilder().layer(0).build();
		Screen.Writer writer1 = screen.writerBuilder().layer(1).build();
		writer0.text("A", 0, 0);
		writer1.text("B", 0, 0);

		// then - layer 1 should override layer 0
		ScreenItem[][] items = screen.getItems();
		assertEquals("B", items[0][0].getContent().toString());
	}

	@Test
	void writerBuilderShouldSupportColor() {
		// given
		DefaultScreen screen = new DefaultScreen(5, 10);

		// when
		Screen.Writer writer = screen.writerBuilder().color(Color.RED).build();
		writer.text("X", 0, 0);

		// then
		ScreenItem[][] items = screen.getItems();
		assertEquals(Color.RED, items[0][0].getForeground());
	}

}
