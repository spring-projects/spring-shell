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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.control.MenuBarView.MenuBarItem;
import org.springframework.shell.component.view.control.MenuView.MenuItem;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler.KeyHandlerResult;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class MenuBarViewTests extends AbstractViewTests {

	private static final String SELECTED_FIELD = "activeItemIndex";
	private static final String MENUVIEW_FIELD = "currentMenuView";

	@Nested
	class Construction {

		@Test
		void constructView() {
			MenuBarView view;

			view = MenuBarView.of(MenuBarItem.of("title"));
			assertThat(view.getItems()).hasSize(1);
		}

		@Test
		void hotkeys() {
			MenuBarItem item;

			item = MenuBarItem.of("title");
			assertThat(item.getHotKey()).isNull();
			item.setHotKey(Key.f);
			assertThat(item.getHotKey()).isEqualTo(Key.f);

			item = MenuBarItem.of("title").setHotKey(Key.f);
			assertThat(item.getHotKey()).isEqualTo(Key.f);
		}
	}

	@Nested
	class Events {

		MenuBarView view;

		@BeforeEach
		void setup() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			MenuBarItem menuBarItem = new MenuBarView.MenuBarItem("menu1", new MenuView.MenuItem[] { menuItem });
			menuBarItem.setHotKey(Key.q);
			view = new MenuBarView(new MenuBarView.MenuBarItem[] { menuBarItem });
			view.setRect(0, 0, 10, 10);
			configure(view);
		}

		@Test
		void mouseClickGetsFocus() {
			MouseEvent click = mouseClick(0, 0);
			MouseHandlerResult result = view.getMouseHandler().handle(MouseHandler.argsOf(click));
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});
		}

		@Test
		void hotKeyGetsFocus() {
			KeyHandlerResult result = handleHotKey(view, KeyEvent.Key.q);

			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isNotNull();
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
			});
		}

	}

	@Nested
	class Styling {

		@Test
		void hasBorder() {
			MenuBarView view = new MenuBarView(new MenuBarView.MenuBarItem[0]);
			view.setShowBorder(true);
			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		}

	}

	// @Nested
	// class Selection {
	// 		MenuBarView view;

	// }

	@Nested
	class Menus {

		@Test
		void mouseClicksSameOpensAndClosesMenu() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			MenuBarItem menuBarItem = new MenuBarView.MenuBarItem("menu1", new MenuView.MenuItem[] { menuItem });
			MenuBarView view = new MenuBarView(new MenuBarView.MenuBarItem[] { menuBarItem });
			configure(view);
			view.setRect(0, 0, 10, 10);

			MouseEvent click1 = mouseClick(0, 0);
			MouseHandlerResult result1 = view.getMouseHandler().handle(MouseHandler.argsOf(click1));
			assertThat(result1).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click1);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});

			Integer selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(0);

			MenuView menuView1 = (MenuView) ReflectionTestUtils.getField(view, MENUVIEW_FIELD);
			assertThat(menuView1).isNotNull();

			MouseEvent click2 = mouseClick(0, 0);
			MouseHandlerResult result2 = view.getMouseHandler().handle(MouseHandler.argsOf(click2));
			assertThat(result2).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click2);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});

			MenuView menuView2 = (MenuView) ReflectionTestUtils.getField(view, MENUVIEW_FIELD);
			assertThat(menuView2).isNull();
		}

		@Test
		void mouseClicksOpensDifferentMenus() {
			MenuItem menuItem1 = new MenuView.MenuItem("sub1");
			MenuItem menuItem2 = new MenuView.MenuItem("sub2");
			MenuBarItem menuBarItem1 = new MenuBarView.MenuBarItem("menu1", new MenuView.MenuItem[] { menuItem1 });
			MenuBarItem menuBarItem2 = new MenuBarView.MenuBarItem("menu2", new MenuView.MenuItem[] { menuItem2 });
			MenuBarView view = new MenuBarView(new MenuBarView.MenuBarItem[] { menuBarItem1, menuBarItem2 });
			configure(view);
			view.setRect(0, 0, 10, 10);

			MouseEvent click1 = mouseClick(0, 0);
			MouseHandlerResult result1 = view.getMouseHandler().handle(MouseHandler.argsOf(click1));
			assertThat(result1).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click1);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});

			Integer selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(0);

			MenuView menuView1 = (MenuView) ReflectionTestUtils.getField(view, MENUVIEW_FIELD);
			assertThat(menuView1).isNotNull();
			assertThat(menuView1.getItems().get(0).getTitle()).isEqualTo("sub1");

			MouseEvent click2 = mouseClick(7, 0);
			MouseHandlerResult result2 = view.getMouseHandler().handle(MouseHandler.argsOf(click2));
			assertThat(result2).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click2);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});

			MenuView menuView2 = (MenuView) ReflectionTestUtils.getField(view, MENUVIEW_FIELD);
			assertThat(menuView2).isNotNull();
			assertThat(menuView2.getItems().get(0).getTitle()).isEqualTo("sub2");
		}

		@Test
		void arrowKeysMoveBetweenDifferentMenus() {
			MenuItem menuItem1 = new MenuView.MenuItem("sub1");
			MenuItem menuItem2 = new MenuView.MenuItem("sub2");
			MenuBarItem menuBarItem1 = new MenuBarView.MenuBarItem("menu1", new MenuView.MenuItem[] { menuItem1 });
			MenuBarItem menuBarItem2 = new MenuBarView.MenuBarItem("menu2", new MenuView.MenuItem[] { menuItem2 });
			MenuBarView view = new MenuBarView(new MenuBarView.MenuBarItem[] { menuBarItem1, menuBarItem2 });
			configure(view);
			view.setRect(0, 0, 10, 10);

			// Can't yet active menu with a key, so start with a click
			MouseEvent click1 = mouseClick(0, 0);
			MouseHandlerResult result1 = view.getMouseHandler().handle(MouseHandler.argsOf(click1));
			assertThat(result1).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click1);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});

			Integer selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(0);

			MenuView menuView1 = (MenuView) ReflectionTestUtils.getField(view, MENUVIEW_FIELD);
			assertThat(menuView1).isNotNull();
			assertThat(menuView1.getItems().get(0).getTitle()).isEqualTo("sub1");

			handleKey(view, Key.CursorRight);

			MenuView menuView2 = (MenuView) ReflectionTestUtils.getField(view, MENUVIEW_FIELD);
			assertThat(menuView2).isNotNull();
			assertThat(menuView2.getItems().get(0).getTitle()).isEqualTo("sub2");
		}

		@Test
		void menuHasPositionRelativeToHeader() {
			MenuBarView view = new MenuBarView(new MenuBarItem[] {
				new MenuBarItem("menu1", new MenuItem[] {
					new MenuItem("sub11")
				}),
				new MenuBarItem("menu2", new MenuItem[] {
					new MenuItem("sub21")
				})
			});
			configure(view);
			view.setRect(0, 0, 20, 1);

			MouseEvent click = mouseClick(7, 0);
			MouseHandlerResult result = view.getMouseHandler().handle(MouseHandler.argsOf(click));
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});

			MenuView menuView = (MenuView) ReflectionTestUtils.getField(view, MENUVIEW_FIELD);
			assertThat(menuView).isNotNull().satisfies(m -> {
				assertThat(m.getRect()).satisfies(r -> {
					assertThat(r.x()).isEqualTo(7);
				});
			});

		}

	}

}
