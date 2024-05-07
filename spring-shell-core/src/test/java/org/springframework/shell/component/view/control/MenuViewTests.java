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

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.shell.component.view.control.MenuView.MenuItem;
import org.springframework.shell.component.view.control.MenuView.MenuItemCheckStyle;
import org.springframework.shell.component.view.control.MenuView.MenuViewOpenSelectedItemEvent;
import org.springframework.shell.component.view.control.MenuView.MenuViewSelectedItemChangedEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.shell.component.view.screen.Color;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeRegistry;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeSettings;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class MenuViewTests extends AbstractViewTests {

	private static final String SELECTED_FIELD = "activeItemIndex";
	private static final String RADIO_ACTIVE_FIELD = "radioActive";
	private static final String CHECKED_ACTIVE_FIELD = "checkedActive";

	ThemeResolver themeResolver;

	@BeforeEach
	public void setupMenuView() {
		ThemeRegistry themeRegistry = new ThemeRegistry();
		themeRegistry.register(new Theme() {
			@Override
			public String getName() {
				return "default";
			}

			@Override
			public ThemeSettings getSettings() {

				return new ThemeSettings() {
					@Override
					public StyleSettings styles() {
						return new StyleSettings() {
							@Override
							public String highlight() {
								return "bold,italic,bg-rgb:#0000FF,fg-rgb:#FF0000";
							}
						};
					}
				};
			}
		});

		themeResolver = new ThemeResolver(themeRegistry, "default");
	}

	@Nested
	class Construction {

		@SuppressWarnings("unchecked")
		@Test
		void constructView() {
			MenuView view;

			view = new MenuView(new MenuItem[] {
				MenuItem.of("sub1"),
				MenuItem.of("sub2")
			});
			assertThat(view.getItems()).hasSize(2);

			view = new MenuView(new MenuItem[] {
				new MenuItem("sub1"),
				new MenuItem("sub2")
			});
			assertThat(view.getItems()).hasSize(2);

			view = new MenuView(new MenuItem[] {
				MenuItem.of("sub1", MenuItemCheckStyle.RADIO),
				MenuItem.of("sub2", MenuItemCheckStyle.RADIO)
			});
			assertThat(view.getItems()).hasSize(2);

			view = new MenuView(new MenuItem[] {
				new MenuItem("sub1", MenuItemCheckStyle.RADIO),
				new MenuItem("sub2", MenuItemCheckStyle.RADIO)
			});
			assertThat(view.getItems()).hasSize(2);

			view = new MenuView(new MenuItem[] {
				new MenuItem("sub1", MenuItemCheckStyle.RADIO, null, true),
				new MenuItem("sub2", MenuItemCheckStyle.RADIO, null, false)
			});
			assertThat(view.getItems()).hasSize(2);
			MenuItem radioActive = (MenuItem) ReflectionTestUtils.getField(view, RADIO_ACTIVE_FIELD);
			assertThat(radioActive).isNotNull();
			assertThat(radioActive.getTitle()).isEqualTo("sub1");

			view = new MenuView(new MenuItem[] {
				new MenuItem("sub1", MenuItemCheckStyle.CHECKED, null, true),
				new MenuItem("sub2", MenuItemCheckStyle.CHECKED, null, true)
			});
			assertThat(view.getItems()).hasSize(2);
			Set<MenuItem> checkedActive = (Set<MenuItem>) ReflectionTestUtils.getField(view, CHECKED_ACTIVE_FIELD);
			assertThat(checkedActive).isNotNull();
			assertThat(checkedActive).hasSize(2);
		}

		@Test
		void constructItem() {
			MenuItem item;
			Runnable runnable = () -> {};

			item = new MenuItem("title", MenuItemCheckStyle.RADIO, runnable, true);
			assertThat(item.getTitle()).isEqualTo("title");
			assertThat(item.getCheckStyle()).isEqualTo(MenuItemCheckStyle.RADIO);
			assertThat(item.getAction()).isSameAs(runnable);
			assertThat(item.isInitialCheckState()).isTrue();

			item = MenuItem.of("title", MenuItemCheckStyle.RADIO, runnable, true);
			assertThat(item.getTitle()).isEqualTo("title");
			assertThat(item.getCheckStyle()).isEqualTo(MenuItemCheckStyle.RADIO);
			assertThat(item.getAction()).isSameAs(runnable);
			assertThat(item.isInitialCheckState()).isTrue();
		}

		@Test
		void constructUsingRunnable() {
			MenuView view;

			view = new MenuView(new MenuItem[] {
				MenuItem.of("sub1", MenuItemCheckStyle.RADIO, () -> {}),
				MenuItem.of("sub2", MenuItemCheckStyle.RADIO, () -> {})
			});
			assertThat(view.getItems()).hasSize(2);
		}

	}

	@Nested
	class Styling {

		MenuView view;

		@Test
		void hasBorder() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			view = new MenuView(Arrays.asList(menuItem));
			view.setShowBorder(true);
			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		}

		@Test
		void selectedHighlightNoTheme() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			view = new MenuView(Arrays.asList(menuItem));
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasStyle(1, 1, 1);
			assertThat(forScreen(screen7x10)).hasForegroundColor(1, 1, -1);
			assertThat(forScreen(screen7x10)).hasBackgroundColor(1, 1, -1);
		}

		@Test
		void selectedHighlightThemeSet() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			view = new MenuView(Arrays.asList(menuItem));
			view.setThemeResolver(themeResolver);
			view.setThemeName("default");
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasStyle(1, 1, 5);
			assertThat(forScreen(screen7x10)).hasForegroundColor(1, 1, Color.RED);
			assertThat(forScreen(screen7x10)).hasBackgroundColor(1, 1, Color.BLUE);
		}

		@Test
		void defaultItemCheckStyleIsNoCheck() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			view = new MenuView(Arrays.asList(menuItem));
			assertThat(view.getItems()).allSatisfy(item -> {
				assertThat(item.getCheckStyle()).isEqualTo(MenuItemCheckStyle.NOCHECK);
			});
		}

	}

	@Nested
	class Selection {

		MenuView view;

		@BeforeEach
		void setup() {
			view = new MenuView(new MenuItem[] {
				MenuItem.of("sub1"),
				MenuItem.of("sub2")
			});
			configure(view);
			view.setRect(0, 0, 10, 10);
		}

		@Test
		void firstItemShouldAlwaysBeSelected() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			MenuView view = new MenuView(Arrays.asList(menuItem));
			Integer selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(0);
		}

		@Test
		void clickInItemSelects() {
			handleMouseClick(view, 0, 2);
			Integer selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(1);
		}

		@Test
		void downArrowMovesSelection() {
			Integer selected;

			handleKey(view, Key.CursorDown);
			selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(1);
		}

		@Test
		void upArrowMovesSelection() {
			Integer selected;

			handleKey(view, Key.CursorUp);
			selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(1);
		}

		@Test
		void wheelMovesSelection() {
			Integer selected;

			handleMouseWheelDown(view, 0, 1);
			selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(1);

			handleMouseWheelUp(view, 0, 1);
			selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(0);
		}

		@Test
		void selectionShouldNotMoveOutOfBounds() {
			Integer selected;

			handleKey(view, Key.CursorDown);
			selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(1);

			handleKey(view, Key.CursorDown);
			selected = (Integer) ReflectionTestUtils.getField(view, SELECTED_FIELD);
			assertThat(selected).isEqualTo(0);
		}

		void canSelectManually() {

		}
	}

	@Nested
	class Checked {

		MenuItem sub1;
		MenuItem sub2;
		MenuItem sub3;
		MenuItem sub4;
		MenuItem sub5;
		MenuItem sub6;
		MenuView view;

		@BeforeEach
		void setup() {
			sub1 = MenuItem.of("sub1", MenuItemCheckStyle.NOCHECK);
			sub2 = MenuItem.of("sub2", MenuItemCheckStyle.NOCHECK);
			sub3 = MenuItem.of("sub3", MenuItemCheckStyle.CHECKED);
			sub4 = MenuItem.of("sub4", MenuItemCheckStyle.CHECKED);
			sub5 = MenuItem.of("sub5", MenuItemCheckStyle.RADIO);
			sub6 = MenuItem.of("sub6", MenuItemCheckStyle.RADIO);
		}

		@Test
		void onlyNocheckDontAddPrefix() {
			view = new MenuView(new MenuItem[] { sub1, sub2 });
			view.setShowBorder(true);
			configure(view);
			view.setRect(0, 0, 10, 10);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasHorizontalText("sub1", 0, 1, 5);
			assertThat(forScreen(screen10x10)).hasHorizontalText("sub2", 0, 2, 5);
		}

		@Test
		void showsCheckedInRadio() {
			view = new MenuView(new MenuItem[] { sub5, sub6 });
			view.setShowBorder(true);
			configure(view);
			view.setRect(0, 0, 10, 10);

			view.toggle(sub5);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[x] sub5", 0, 1, 9);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] sub6", 0, 2, 9);

			view.toggle(sub6);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] sub5", 0, 1, 9);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[x] sub6", 0, 2, 9);
		}

		@Test
		void showsCheckedInNonRadio() {
			view = new MenuView(new MenuItem[] { sub3, sub4 });
			view.setShowBorder(true);
			configure(view);
			view.setRect(0, 0, 10, 10);

			view.toggle(sub4);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] sub3", 0, 1, 9);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[x] sub4", 0, 2, 9);

			view.toggle(sub3);
			view.toggle(sub4);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[x] sub3", 0, 1, 9);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] sub4", 0, 2, 9);
		}

		@Test
		void mixedAddPrefixforNocheck() {
			view = new MenuView(new MenuItem[] { sub1, sub3, sub4 });
			view.setShowBorder(true);
			configure(view);
			view.setRect(0, 0, 10, 10);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasHorizontalText("    sub1", 0, 1, 9);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] sub3", 0, 2, 9);
			assertThat(forScreen(screen10x10)).hasHorizontalText("[ ] sub4", 0, 3, 9);
		}

	}

	@Nested
	class Events {

		MenuView view;

		@BeforeEach
		void setup() {
			view = new MenuView(new MenuItem[] {
				MenuItem.of("sub1"),
				MenuItem.of("sub2")
			});
			configure(view);
			view.setRect(0, 0, 10, 10);
		}

		@Test
		void handlesMouseClickInItem() {
			MouseEvent click = mouseClick(0, 2);

			Flux<MenuViewSelectedItemChangedEvent> actions = eventLoop
					.viewEvents(MenuViewSelectedItemChangedEvent.class);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			MouseHandlerResult result = handleMouseClick(view, click);

			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});
			verifier.verify(Duration.ofSeconds(1));
		}

		@Test
		void keySelectSendsEvent() {
			Flux<MenuViewOpenSelectedItemEvent> actions = eventLoop
					.viewEvents(MenuViewOpenSelectedItemEvent.class);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			handleKey(view, Key.Enter);
			verifier.verify(Duration.ofSeconds(1));
		}

		@Test
		void selectionChangedSendsEvent() {
			Flux<MenuViewSelectedItemChangedEvent> actions = eventLoop
					.viewEvents(MenuViewSelectedItemChangedEvent.class);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			handleKey(view, Key.CursorDown);
			verifier.verify(Duration.ofSeconds(1));
		}

	}

	@Nested
	class Visual {
		MenuView view;

		@BeforeEach
		void setup() {
			view = new MenuView(new MenuItem[] {
				MenuItem.of("sub1"),
				MenuItem.of("sub2")
			});
			configure(view);
			view.setRect(0, 0, 10, 10);
		}

		@Test
		void hasDefaultItemSelected() {
			MenuItem menuItem = new MenuView.MenuItem("sub1");
			MenuView view = new MenuView(Arrays.asList(menuItem));
			view.setShowBorder(true);
			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasHorizontalText("sub1", 0, 1, 5);
		}
	}

}
