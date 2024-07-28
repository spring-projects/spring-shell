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

import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.control.AbstractViewTests;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.geom.VerticalAlign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScreenTests extends AbstractViewTests {

	@Test
	void zeroDataSizeDoesntBreak() {
		assertThat(screen0x0.getItems()).isEmpty();
	}

	@Test
	void cantResizeNegative() {
		assertThatThrownBy(() -> {
			screen0x0.resize(-1, 0);
		}).hasMessageContaining("negative rows");
		assertThatThrownBy(() -> {
			screen0x0.resize(0, -1);
		}).hasMessageContaining("negative columns");
	}

	@Test
	void printInBoxShows() {
		screen10x10.writerBuilder().build().border(0, 0, 10, 10);
		assertThat(forScreen(screen10x10)).hasBorder(0, 0, 10, 10);
	}

	@Test
	void printsText() {
		screen24x80.writerBuilder().build().text("text", 0, 0);
		assertThat(forScreen(screen24x80)).hasHorizontalText("text", 0, 0, 4);
	}

	@Test
	void printsAttributedText() {
		AttributedString text = new AttributedString("text");
		screen24x80.writerBuilder().build().text(text, 0, 0);
		assertThat(forScreen(screen24x80)).hasHorizontalText("text", 0, 0, 4);
	}

	@Test
	void printsTextWithForegroundColor() {
		Writer writer = screen24x80.writerBuilder().color(Color.RED).build();
		writer.text("text", 0, 0);
		assertThat(forScreen(screen24x80)).hasForegroundColor(0, 0, Color.RED);
	}

	@Test
	void printsTextWithForegroundColorOnLayerTopOverrides() {
		Writer writer0 = screen24x80.writerBuilder().layer(0).color(Color.BLACK).build();
		Writer writer1 = screen24x80.writerBuilder().layer(1).color(Color.RED).build();
		writer0.text("text", 0, 0);
		assertThat(forScreen(screen24x80)).hasForegroundColor(0, 0, Color.BLACK);
		writer1.text("text", 0, 0);
		assertThat(forScreen(screen24x80)).hasForegroundColor(0, 0, Color.RED);
	}

	@Test
	void printsTextAlign() {
		Rectangle rect = new Rectangle(1, 1, 10, 10);
		screen24x80.writerBuilder().build().text("text", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		assertThat(forScreen(screen24x80)).hasHorizontalText("text", 4, 6, 4);
	}

	@Test
	void printsTextAlignInOneRowRect() {
		Rectangle rect = new Rectangle(1, 1, 10, 1);
		screen24x80.writerBuilder().build().text("text", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		assertThat(forScreen(screen24x80)).hasHorizontalText("text", 4, 1, 4);
	}

	@Test
	void printsTextAlignInSmallRect() {
		Rectangle rect = new Rectangle(10, 14, 10, 3);
		screen24x80.writerBuilder().build().text("text", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		assertThat(forScreen(screen24x80)).hasHorizontalText("text", 13, 15, 4);
	}

	@Test
	void writeTextFromWriterLayerOverrides() {
		DefaultScreen screen = new DefaultScreen(24, 80);
		screen.writerBuilder().layer(0).build().text("text", 0, 0);
		screen.writerBuilder().layer(1).build().text("xxx", 0, 0);
		ScreenItem[][] items = screen.getScreenItems();
		assertThat(items[0][0].getContent()).isEqualTo("x");
		assertThat(items[0][1].getContent()).isEqualTo("x");
		assertThat(items[0][2].getContent()).isEqualTo("x");
		assertThat(items[0][3].getContent()).isEqualTo("t");
	}

	@Test
	void discardAndDoNotErrorTextWriteOutsideScreen() {
		assertThatCode(() -> {
			screen24x80.writerBuilder().build().text("text", 79, 0);
			screen24x80.writerBuilder().build().text("text", 80, 0);
			screen24x80.writerBuilder().build().text("text", 0, 24);
		}).doesNotThrowAnyException();
	}

	@Test
	void discardAndDoNotErrorBorderWriteOutsideScreen() {
		assertThatCode(() -> {
			screen24x80.writerBuilder().build().border(0, 0, 81, 24);
			screen24x80.writerBuilder().build().border(0, 0, 80, 25);
			screen24x80.writerBuilder().build().border(0, 0, 81, 25);
		}).doesNotThrowAnyException();
	}

}
