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

import org.assertj.core.api.AssertProvider;
import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.screen.Color;
import org.springframework.shell.component.view.screen.DefaultScreen;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Rectangle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ScreenAssertTests {

	@Test
	void testCursorPosition() {
		Screen screen = new DefaultScreen(5, 5);
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasCursorVisible())
			.withMessageContaining("Expecting a Screen to have a visible cursor");
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasCursorInPosition(1, 1))
			.withMessageContaining("Expecting a Screen to have position <1,1> but was <0,0>");
	}

	@Test
	void hasForegroundColorShouldPass() {
		Screen screen = new DefaultScreen(5, 5);
		screen.writerBuilder().color(Color.RED).build().text("test", 0, 0);
		assertThat(forScreen(screen)).hasForegroundColor(0, 0, Color.RED);
	}

	@Test
	void hasForegroundColorShouldFail() {
		Screen screen = new DefaultScreen(5, 5);
		screen.writerBuilder().color(Color.RED).build().text("test", 0, 0);
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasForegroundColor(0, 0, Color.BLUE))
			.withMessageContaining("Expecting a Screen to have foreground color <255> position <0,0> but was <16711680>");
	}

	@Test
	void hasStyleShouldPass() {
		Screen screen = new DefaultScreen(5, 5);
		screen.writerBuilder().style(ScreenItem.STYLE_BOLD).build().text("test", 0, 0);
		assertThat(forScreen(screen)).hasStyle(0, 0, ScreenItem.STYLE_BOLD);
	}

	@Test
	void hasStyleShouldFail() {
		Screen screen = new DefaultScreen(5, 5);
		screen.writerBuilder().build().text("test", 0, 0);
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasStyle(0, 0, ScreenItem.STYLE_BOLD))
			.withMessageContaining("Expecting a Screen to have style <1> position <0,0> but was <-1>");
	}

	@Test
	void hasBackgroundColorShouldPass() {
		Screen screen = new DefaultScreen(5, 5);
		Writer writer = screen.writerBuilder().build();
		writer.background(new Rectangle(0, 0, 5, 5), Color.BLUE);
		writer.text("test", 0, 0);
		assertThat(forScreen(screen)).hasBackgroundColor(0, 0, Color.BLUE);
	}

	@Test
	void hasBackgroundColorShouldFail() {
		Screen screen = new DefaultScreen(5, 5);
		Writer writer = screen.writerBuilder().build();
		writer.text("test", 0, 0);
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasBackgroundColor(0, 0, Color.BLUE))
			.withMessageContaining("Expecting a Screen to have background color <255> position <0,0> but was <-1>");
	}

	@Test
	void shouldThrowWithInvalidBounds() {
		Screen screen = new DefaultScreen(5, 5);
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasBorder(0, 0, 0, 0));
	}

	@Test
	void shouldThrowWithInvalidBorder() {
		Screen screen = new DefaultScreen(5, 5);
		screen.writerBuilder().build().border(0, 0, 5, 5);
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasBorder(0, 0, 5, 4));
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasBorder(0, 0, 5, 4));
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasBorder(0, 0, 4, 4));
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forScreen(screen)).hasBorder(1, 1, 3, 3));
	}

	@Test
	void shouldNotThrowWithValidBorder() {
		Screen screen = new DefaultScreen(5, 10);
		screen.writerBuilder().build().border(0, 0, 10, 5);
		assertThat(forScreen(screen)).hasBorder(0, 0, 10, 5);

		screen = new DefaultScreen(10, 5);
		screen.writerBuilder().build().border(0, 0, 5, 10);
		assertThat(forScreen(screen)).hasBorder(0, 0, 5, 10);

		screen = new DefaultScreen(10, 5);
		screen.writerBuilder().build().border(1, 1, 3, 8);
		assertThat(forScreen(screen)).hasBorder(1, 1, 3, 8);
	}

	@Test
	void shouldNotThrowWithValidNonBorder() {
		Screen screen = new DefaultScreen(5, 10);
		screen.writerBuilder().build().border(0, 0, 10, 5);
		assertThat(forScreen(screen)).hasNoBorder(1, 1, 8, 3);
	}

	@Test
	void hasHorizontalText() {
		Screen screen = new DefaultScreen(5, 10);
		screen.writerBuilder().build().text("test", 0, 0);
		assertThat(forScreen(screen)).hasHorizontalText("test", 0, 0, 4);
	}

	@Test
	void hasNoHorizontalText() {
		Screen screen = new DefaultScreen(5, 10);
		screen.writerBuilder().build().text("xxxx", 0, 0);
		assertThat(forScreen(screen)).hasNoHorizontalText("test", 0, 0, 4);
	}

	// @Test
	// void xxx() {
	// 	Screen screen = new Screen(5, 5);
	// 	screen.printBorder(0, 0, 5, 5);
	// 	assertThat(forScreen(screen)).hasBorder(0, 0, 5, 4);
	// }

	private AssertProvider<ScreenAssert> forScreen(Screen screen) {
		return () -> new ScreenAssert(screen);
	}
}
