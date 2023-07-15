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
package org.springframework.shell.component.view.control;

import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.screen.DefaultScreen;

import static org.assertj.core.api.Assertions.assertThat;

class GridViewTests extends AbstractViewTests {

	@Test
	void hasBordersWith1x1() {
		BoxView box1 = new BoxView();

		GridView grid = new GridView();
		grid.setShowBorders(true);
		grid.setRowSize(0);
		grid.setColumnSize(0);
		grid.setShowBorder(false);
		grid.addItem(box1, 0, 0, 1, 1, 0, 0);

		grid.setRect(0, 0, 80, 24);
		grid.draw(screen24x80);

		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
	}

	@Test
	void hasBordersWith1x2() {
		BoxView box1 = new BoxView();
		BoxView box2 = new BoxView();

		GridView grid = new GridView();
		grid.setShowBorders(true);
		grid.setRowSize(0);
		grid.setColumnSize(0, 0);
		grid.setShowBorder(false);
		grid.addItem(box1, 0, 0, 1, 1, 0, 0);
		grid.addItem(box2, 0, 1, 1, 1, 0, 0);

		grid.setRect(0, 0, 80, 24);
		grid.draw(screen24x80);

		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 39, 24);
		assertThat(forScreen(screen24x80)).hasBorder(39, 0, 41, 24);
	}

	@Test
	void hasBordersWith2x1() {
		BoxView box1 = new BoxView();
		BoxView box2 = new BoxView();

		GridView grid = new GridView();
		grid.setShowBorders(true);
		grid.setRowSize(0, 0);
		grid.setColumnSize(0);
		grid.setShowBorder(false);
		grid.addItem(box1, 0, 0, 1, 1, 0, 0);
		grid.addItem(box2, 1, 0, 1, 1, 0, 0);

		grid.setRect(0, 0, 80, 24);
		grid.draw(screen24x80);

		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 12);
		assertThat(forScreen(screen24x80)).hasBorder(0, 11, 80, 13);
	}

	@Test
	void hasBordersWith2x2() {
		BoxView box1 = new BoxView();
		BoxView box2 = new BoxView();
		BoxView box3 = new BoxView();
		BoxView box4 = new BoxView();

		GridView grid = new GridView();
		grid.setShowBorders(true);
		grid.setRowSize(0, 0);
		grid.setColumnSize(0, 0);
		grid.setShowBorder(false);
		grid.addItem(box1, 0, 0, 1, 1, 0, 0);
		grid.addItem(box2, 0, 1, 1, 1, 0, 0);
		grid.addItem(box3, 1, 0, 1, 1, 0, 0);
		grid.addItem(box4, 1, 1, 1, 1, 0, 0);

		grid.setRect(0, 0, 80, 24);
		grid.draw(screen24x80);

		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 39, 12);
		assertThat(forScreen(screen24x80)).hasBorder(39, 0, 41, 12);
		assertThat(forScreen(screen24x80)).hasBorder(0, 11, 80, 13);
		assertThat(forScreen(screen24x80)).hasBorder(39, 11, 41, 13);
	}

	@Test
	void hasBordersWithHidden() {
		screen24x80 = new DefaultScreen(20, 10);

		BoxView menu = new BoxView();
		BoxView main = new BoxView();
		BoxView sideBar = new BoxView();
		BoxView header = new BoxView();
		BoxView footer = new BoxView();

		GridView grid = new GridView();
		grid.setRowSize(3, 0, 3);
		grid.setColumnSize(30, 0, 30);
		grid.setShowBorders(true);

		grid.addItem(header, 0, 0, 1, 3, 0, 0);
		grid.addItem(footer, 2, 0, 1, 3, 0, 0);

		grid.addItem(menu, 0, 0, 0, 0, 0, 0);
		grid.addItem(main, 1, 0, 1, 3, 0, 0);
		grid.addItem(sideBar, 0, 0, 0, 0, 0, 0);

		grid.addItem(menu, 1, 0, 1, 1, 0, 100);
		grid.addItem(main, 1, 1, 1, 1, 0, 100);
		grid.addItem(sideBar, 1, 2, 1, 1, 0, 100);

		grid.setRect(0, 0, 10, 20);
		grid.draw(screen24x80);

		// overflows, don't have full border anywhere
		// assertThat(forScreen(screen)).hasBorder(0, 0, 80, 24);
	}

	@Test
	void gridBoxHasTitle() {
		BoxView box1 = new BoxView();

		GridView grid = new GridView();
		grid.setShowBorder(true);
		grid.setTitle("title");
		grid.setShowBorders(true);
		grid.setRowSize(0);
		grid.setColumnSize(0);
		grid.addItem(box1, 0, 0, 1, 1, 0, 0);

		grid.setRect(0, 0, 80, 24);
		grid.draw(screen24x80);

		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		assertThat(forScreen(screen24x80)).hasBorder(1, 1, 78, 22);
		assertThat(forScreen(screen24x80)).hasHorizontalText("title", 1, 0, 5);
	}

}
