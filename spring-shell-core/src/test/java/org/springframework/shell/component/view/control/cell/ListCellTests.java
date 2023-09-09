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
package org.springframework.shell.component.view.control.cell;

import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.control.AbstractViewTests;
import org.springframework.shell.component.view.control.ListView.ItemStyle;
import org.springframework.shell.component.view.screen.Color;
import org.springframework.shell.component.view.screen.ScreenItem;

import static org.assertj.core.api.Assertions.assertThat;

class ListCellTests extends AbstractViewTests {

	@Test
	void simpleTextWritesNoCheck() {
		ListCell<String> cell = ListCell.of("item", ItemStyle.NOCHECK);
		cell.setRect(0, 0, 10, 1);
		cell.draw(screen10x10);
		assertThat(forScreen(screen10x10)).hasHorizontalText("item", 0, 0, 4);
	}

	@Test
	void simpleTextWritesChecked() {
		ListCell<String> cell = ListCell.of("item", ItemStyle.CHECKED);
		cell.setRect(0, 0, 10, 1);
		cell.draw(screen10x10);
		assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] item", 0, 0, 8);
		cell.setSelected(true);
		cell.draw(screen10x10);
		assertThat(forScreen(screen10x10)).hasHorizontalText("[x] item", 0, 0, 8);
	}

	@Test
	void hasBackgroundColor() {
		ListCell<String> cell = ListCell.of("item", ItemStyle.RADIO);
		cell.setRect(0, 0, 10, 1);
		cell.draw(screen10x10);
		assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] item", 0, 0, 8);
		cell.setSelected(true);
		cell.draw(screen10x10);
		assertThat(forScreen(screen10x10)).hasHorizontalText("[x] item", 0, 0, 8);
	}

	@Test
	void hasForegroundColor() {
		ListCell<String> cell = ListCell.of("item", ItemStyle.NOCHECK);
		cell.setForegroundColor(Color.BLUE);
		cell.setRect(0, 0, 10, 1);
		cell.draw(screen10x10);
		assertThat(forScreen(screen10x10)).hasHorizontalText("item", 0, 0, 4).hasForegroundColor(0, 0, Color.BLUE);
	}

	@Test
	void hasStyle() {
		ListCell<String> cell = ListCell.of("item", ItemStyle.NOCHECK);
		cell.setStyle(ScreenItem.STYLE_BOLD);
		cell.setRect(0, 0, 10, 1);
		cell.draw(screen10x10);
		assertThat(forScreen(screen10x10)).hasHorizontalText("item", 0, 0, 4).hasStyle(0, 0, ScreenItem.STYLE_BOLD);
	}

}
