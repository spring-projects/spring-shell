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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.screen.DefaultScreen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class GridViewTests extends AbstractViewTests {


	@Nested
	class ItemPositions {

		@Test
		void noBorderKeepsFullArea() {
			BoxView box1 = new BoxView();
			BoxView sbox1 = spy(box1);
			GridView grid = new GridView();

			grid.setShowBorders(false);
			grid.setRowSize(0);
			grid.setColumnSize(0);
			grid.setShowBorder(false);
			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 80, 24);
		}

		@Test
		void borderMakesItemSmaller() {
			BoxView box1 = new BoxView();
			BoxView sbox1 = spy(box1);
			GridView grid = new GridView();

			grid.setShowBorders(true);
			grid.setRowSize(0);
			grid.setColumnSize(0);
			grid.setShowBorder(false);
			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1).setRect(1, 1, 78, 22);
		}

		@Test
		void positionsWith1x1FixedSizeGoTopLeft() {
			BoxView box1 = new BoxView();
			BoxView sbox1 = spy(box1);
			GridView grid = new GridView();

			grid.setShowBorders(false);
			grid.setRowSize(1);
			grid.setColumnSize(1);
			grid.setShowBorder(false);
			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 1, 1);
		}

		@Test
		void positionsWith1x2() {
			BoxView box1 = new BoxView();
			BoxView box2 = new BoxView();
			BoxView sbox1 = spy(box1);
			BoxView sbox2 = spy(box2);
			GridView grid = new GridView();

			grid.setShowBorders(false);
			grid.setRowSize(0, 0);
			grid.setColumnSize(0);
			grid.setShowBorder(false);
			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);
			grid.addItem(sbox2, 1, 0, 1, 1, 0, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 80, 12);
			verify(sbox2).setRect(0, 12, 80, 12);
		}

		@Test
		void positionsWith3x3() {
			BoxView box1 = new BoxView();
			BoxView box2 = new BoxView();
			BoxView box3 = new BoxView();
			BoxView box4 = new BoxView();
			BoxView box5 = new BoxView();
			BoxView box6 = new BoxView();
			BoxView box7 = new BoxView();
			BoxView box8 = new BoxView();
			BoxView box9 = new BoxView();
			BoxView sbox1 = spy(box1);
			BoxView sbox2 = spy(box2);
			BoxView sbox3 = spy(box3);
			BoxView sbox4 = spy(box4);
			BoxView sbox5 = spy(box5);
			BoxView sbox6 = spy(box6);
			BoxView sbox7 = spy(box7);
			BoxView sbox8 = spy(box8);
			BoxView sbox9 = spy(box9);
			GridView grid = new GridView();

			grid.setShowBorders(false);
			grid.setRowSize(0, 0, 0);
			grid.setColumnSize(0, 0, 0);
			grid.setShowBorder(false);

			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);
			grid.addItem(sbox2, 0, 1, 1, 1, 0, 0);
			grid.addItem(sbox3, 0, 2, 1, 1, 0, 0);

			grid.addItem(sbox4, 1, 0, 1, 1, 0, 0);
			grid.addItem(sbox5, 1, 1, 1, 1, 0, 0);
			grid.addItem(sbox6, 1, 2, 1, 1, 0, 0);

			grid.addItem(sbox7, 2, 0, 1, 1, 0, 0);
			grid.addItem(sbox8, 2, 1, 1, 1, 0, 0);
			grid.addItem(sbox9, 2, 2, 1, 1, 0, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 26, 8);
			verify(sbox2).setRect(26, 0, 27, 8);
			verify(sbox3).setRect(53, 0, 27, 8);
			verify(sbox4).setRect(0, 8, 26, 8);
			verify(sbox5).setRect(26, 8, 27, 8);
			verify(sbox6).setRect(53, 8, 27, 8);
			verify(sbox7).setRect(0, 16, 26, 8);
			verify(sbox8).setRect(26, 16, 27, 8);
			verify(sbox9).setRect(53, 16, 27, 8);
		}

		@Test
		void minimumWidthWith3x3_1() {
			BoxView box1 = new BoxView();
			BoxView box2 = new BoxView();
			BoxView box3 = new BoxView();
			BoxView box4 = new BoxView();
			BoxView box5 = new BoxView();
			BoxView sbox1 = spy(box1);
			BoxView sbox2 = spy(box2);
			BoxView sbox3 = spy(box3);
			BoxView sbox4 = spy(box4);
			BoxView sbox5 = spy(box5);
			GridView grid = new GridView();

			grid.setShowBorders(false);
			grid.setRowSize(0);
			grid.setColumnSize(30, 10, -1, -1, -2);
			grid.setShowBorder(false);

			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);
			grid.addItem(sbox2, 0, 1, 1, 1, 0, 0);
			grid.addItem(sbox3, 0, 2, 1, 1, 0, 0);
			grid.addItem(sbox4, 0, 3, 1, 1, 0, 0);
			grid.addItem(sbox5, 0, 4, 1, 1, 0, 0);

			grid.setRect(0, 0, 100, 100);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 30, 100);
			verify(sbox2).setRect(30, 0, 10, 100);
			verify(sbox3).setRect(40, 0, 15, 100);
			verify(sbox4).setRect(55, 0, 15, 100);
			verify(sbox5).setRect(70, 0, 30, 100);
		}

		@Test
		void minimumWidthWith3x3_2() {
			BoxView box1 = new BoxView();
			BoxView box2 = new BoxView();
			BoxView box3 = new BoxView();
			BoxView box4 = new BoxView();
			BoxView box5 = new BoxView();
			BoxView box6 = new BoxView();
			BoxView box7 = new BoxView();
			BoxView sbox1 = spy(box1);
			BoxView sbox2 = spy(box2);
			BoxView sbox3 = spy(box3);
			BoxView sbox4 = spy(box4);
			BoxView sbox5 = spy(box5);
			BoxView sbox6 = spy(box6);
			BoxView sbox7 = spy(box7);
			GridView grid = new GridView();

			grid.setShowBorders(false);
			grid.setRowSize(0);
			grid.setColumnSize(30, 10, -1, -1, -2);
			grid.setShowBorder(false);

			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);
			grid.addItem(sbox2, 0, 1, 1, 1, 0, 0);
			grid.addItem(sbox3, 0, 2, 1, 1, 0, 0);
			grid.addItem(sbox4, 0, 3, 1, 1, 0, 0);
			grid.addItem(sbox5, 0, 4, 1, 1, 0, 0);
			grid.addItem(sbox6, 0, 5, 1, 1, 0, 0);
			grid.addItem(sbox7, 0, 6, 1, 1, 0, 0);

			grid.setRect(0, 0, 100, 100);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 30, 100);
			verify(sbox2).setRect(30, 0, 10, 100);
			verify(sbox3).setRect(40, 0, 10, 100);
			verify(sbox4).setRect(50, 0, 10, 100);
			verify(sbox5).setRect(60, 0, 20, 100);
			verify(sbox6).setRect(80, 0, 10, 100);
			verify(sbox7).setRect(90, 0, 10, 100);
		}

		@Test
		void minimumWidthWith3x3_3() {
			BoxView box1 = new BoxView();
			BoxView box2 = new BoxView();
			BoxView box3 = new BoxView();
			BoxView box4 = new BoxView();
			BoxView box5 = new BoxView();
			BoxView box6 = new BoxView();
			BoxView box7 = new BoxView();
			BoxView sbox1 = spy(box1);
			BoxView sbox2 = spy(box2);
			BoxView sbox3 = spy(box3);
			BoxView sbox4 = spy(box4);
			BoxView sbox5 = spy(box5);
			BoxView sbox6 = spy(box6);
			BoxView sbox7 = spy(box7);
			GridView grid = new GridView();

			grid.setShowBorders(false);
			grid.setRowSize(0);
			grid.setColumnSize(30, 10, -1, -1, -2);
			grid.setShowBorder(false);

			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);
			grid.addItem(sbox2, 0, 1, 1, 1, 0, 0);
			grid.addItem(sbox3, 0, 2, 1, 1, 0, 0);
			grid.addItem(sbox4, 0, 3, 1, 1, 0, 0);
			grid.addItem(sbox5, 0, 4, 1, 1, 0, 0);
			grid.addItem(sbox6, 0, 5, 1, 1, 0, 0);
			grid.addItem(sbox7, 0, 6, 1, 1, 0, 0);

			grid.setRect(0, 0, 100, 100);
			grid.setMinSize(15, 20);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 30, 100);
			verify(sbox2).setRect(30, 0, 15, 100);
			verify(sbox3).setRect(45, 0, 15, 100);
			verify(sbox4).setRect(60, 0, 15, 100);
			verify(sbox5).setRect(75, 0, 18, 100);
			verify(sbox6).setRect(93, 0, 7, 100);
			// last one outside should not get called
			verify(sbox7, never()).setRect(0, 0, 0, 0);
		}

	}

	@Nested
	class DynamicLayoutMinWidthHeight {

		@Test
		void widthHides_1_base() {
			BoxView sbox1 = spy(new BoxView());
			BoxView sbox2 = spy(new BoxView());
			BoxView sbox3 = spy(new BoxView());

			GridView grid = new GridView();
			grid.setRowSize(0);
			grid.setColumnSize(0, 0, 0);

			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);
			grid.addItem(sbox2, 0, 1, 1, 1, 0, 0);
			grid.addItem(sbox3, 0, 2, 1, 1, 0, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 26, 24);
			verify(sbox2).setRect(26, 0, 27, 24);
			verify(sbox3).setRect(53, 0, 27, 24);
		}

		@Test
		void widthHides_1_shouldHide() {
			BoxView sbox1 = spy(new BoxView());
			BoxView sbox2 = spy(new BoxView());
			BoxView sbox3 = spy(new BoxView());

			GridView grid = new GridView();
			grid.setRowSize(0);
			grid.setColumnSize(0, 0, 0);

			// this should get used
			grid.addItem(sbox1, 0, 0, 0, 1, 0, 0);
			grid.addItem(sbox2, 0, 0, 1, 3, 0, 0);
			grid.addItem(sbox3, 0, 2, 0, 1, 0, 0);

			// this should not get used
			grid.addItem(sbox1, 0, 0, 1, 1, 0, 100);
			grid.addItem(sbox2, 0, 1, 1, 1, 0, 100);
			grid.addItem(sbox3, 0, 2, 1, 1, 0, 100);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
			verify(sbox2).setRect(0, 0, 80, 24);
			verify(sbox3, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
		}

		@Test
		void widthHides_2_base() {
			BoxView sbox1 = spy(new BoxView());
			BoxView sbox2 = spy(new BoxView());
			BoxView sbox3 = spy(new BoxView());

			GridView grid = new GridView();
			grid.setRowSize(0, 0, 0);
			grid.setColumnSize(0);

			grid.addItem(sbox1, 0, 0, 1, 1, 0, 0);
			grid.addItem(sbox2, 1, 0, 1, 1, 0, 0);
			grid.addItem(sbox3, 2, 0, 1, 1, 0, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1).setRect(0, 0, 80, 8);
			verify(sbox2).setRect(0, 8, 80, 8);
			verify(sbox3).setRect(0, 16, 80, 8);
		}

		@Test
		void widthHides_2_shouldHide() {
			BoxView sbox1 = spy(new BoxView());
			BoxView sbox2 = spy(new BoxView());
			BoxView sbox3 = spy(new BoxView());

			GridView grid = new GridView();
			grid.setRowSize(0, 0, 0);
			grid.setColumnSize(0);

			grid.addItem(sbox1, 0, 0, 0, 1, 0, 0);
			grid.addItem(sbox2, 0, 0, 3, 1, 0, 0);
			grid.addItem(sbox3, 2, 0, 0, 1, 0, 0);

			grid.addItem(sbox1, 0, 0, 1, 1, 100, 0);
			grid.addItem(sbox2, 1, 0, 1, 1, 100, 0);
			grid.addItem(sbox3, 2, 0, 1, 1, 100, 0);

			grid.setRect(0, 0, 80, 24);
			grid.draw(screen24x80);

			verify(sbox1, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
			verify(sbox2).setRect(0, 0, 80, 24);
			verify(sbox3, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
		}

	}

	@Nested
	class Borders {

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

	@Nested
	class NestedGrids {

		@Test
		void simpleNestedGrid() {
			BoxView box = spy(new BoxView());

			GridView grid2 = spy(new GridView());
			grid2.setColumnSize(0);
			grid2.setRowSize(0);
			grid2.addItem(box, 0, 0, 1, 1, 0, 0);

			GridView grid1 = spy(new GridView());
			grid1.setColumnSize(0);
			grid1.setRowSize(0);
			grid1.addItem(grid2, 0, 0, 1, 1, 0, 0);

			grid1.setRect(0, 0, 80, 24);
			grid1.draw(screen24x80);

			verify(grid1).setRect(0, 0, 80, 24);
			verify(grid2).setRect(0, 0, 80, 24);
			verify(box).setRect(0, 0, 80, 24);
		}

		@Test
		void simpleNestedGridWithBorders() {
			BoxView box = spy(new BoxView());
			box.setShowBorder(true);

			GridView grid2 = spy(new GridView());
			grid2.setColumnSize(0);
			grid2.setRowSize(0);
			grid2.setShowBorder(true);
			grid2.addItem(box, 0, 0, 1, 1, 0, 0);

			GridView grid1 = spy(new GridView());
			grid1.setColumnSize(0);
			grid1.setRowSize(0);
			grid1.setShowBorder(true);
			grid1.addItem(grid2, 0, 0, 1, 1, 0, 0);

			grid1.setRect(0, 0, 80, 24);
			grid1.draw(screen24x80);

			verify(grid1).setRect(0, 0, 80, 24);
			verify(grid2).setRect(1, 1, 78, 22);
			verify(box).setRect(2, 2, 76, 20);
		}
	}

}
