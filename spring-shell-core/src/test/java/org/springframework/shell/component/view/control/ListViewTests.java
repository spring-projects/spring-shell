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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.shell.component.view.control.ListView.ItemStyle;
import org.springframework.shell.component.view.control.ListView.ListViewSelectedItemChangedEvent;
import org.springframework.shell.component.view.control.cell.AbstractListCell;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.KeyHandler.KeyHandlerResult;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.shell.component.view.screen.Color;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeRegistry;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeSettings;

import static org.assertj.core.api.Assertions.assertThat;

class ListViewTests extends AbstractViewTests {

	private final static ParameterizedTypeReference<ListViewSelectedItemChangedEvent<String>> LISTVIEW_STRING_TYPEREF
		= new ParameterizedTypeReference<ListViewSelectedItemChangedEvent<String>>() {};

	private static final String SELECTED_FIELD = "selected";
	private static final String START_FIELD = "start";
	private static final String POSITION_FIELD = "pos";
	private static final String SCROLL_METHOD = "scrollIndex";
	ListView<String> view;

	ThemeResolver themeResolver;

	@BeforeEach
	public void setupListView() {
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
	class Events {

		@Test
		void arrowKeysMoveActive() {
			view = new ListView<>();
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));

			Flux<ListViewSelectedItemChangedEvent<String>> actions = eventLoop
					.viewEvents(LISTVIEW_STRING_TYPEREF);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			KeyEvent eventDown = KeyEvent.of(Key.CursorDown);
			KeyHandlerResult result = view.getKeyHandler().handle(KeyHandler.argsOf(eventDown));
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(eventDown);
				assertThat(r.consumed()).isTrue();
			});
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
			verifier.verify(Duration.ofSeconds(1));
		}

		@Test
		void mouseWheelMoveActive() {
			view = new ListView<>();
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));

			Flux<ListViewSelectedItemChangedEvent<String>> actions = eventLoop
					.viewEvents(LISTVIEW_STRING_TYPEREF);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			MouseEvent eventDown = mouseWheelDown(0, 0);
			MouseHandlerResult result = view.getMouseHandler().handle(MouseHandler.argsOf(eventDown));
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(eventDown);
				assertThat(r.capture()).isEqualTo(view);
			});
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
			verifier.verify(Duration.ofSeconds(1));
		}

		@Test
		void mouseClickMoveActive() {
			view = new ListView<>();
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));

			Flux<ListViewSelectedItemChangedEvent<String>> actions = eventLoop
					.viewEvents(LISTVIEW_STRING_TYPEREF);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			MouseEvent event01 = mouseClick(0, 1);
			view.getMouseHandler().handle(MouseHandler.argsOf(event01));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
			verifier.verify(Duration.ofSeconds(1));
		}

	}

	@Nested
	class Navigation {

		@Test
		void initialActiveIndexZeroWhenItemsSet() {
			view = new ListView<>();
			configure(view);
			view.setRect(0, 0, 80, 24);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.setItems(Arrays.asList("item1", "item2"));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
		}

		@Test
		void arrowMovesActiveFromFirstToLast() {
			view = new ListView<>();
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));

			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.CursorUp)));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
		}

		@Test
		void arrowMovesActiveFromLastToFirst() {
			view = new ListView<>();
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));

			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.CursorDown)));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);

			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.CursorDown)));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
		}

	}

	@Nested
	class Visual {

		@Test
		void hasBorder() {
			view = new ListView<>();
			view.setShowBorder(true);
			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		}

		@Test
		void selectedHighlightNoTheme() {
			view = new ListView<>();
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);
			view.setItems(Arrays.asList("item1", "item2", "item3"));
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasStyle(1, 1, 1);
			assertThat(forScreen(screen7x10)).hasForegroundColor(1, 1, -1);
			assertThat(forScreen(screen7x10)).hasBackgroundColor(1, 1, -1);
		}

		@Test
		void selectedHighlightThemeSet() {
			view = new ListView<>();
			view.setThemeResolver(themeResolver);
			view.setThemeName("default");
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);
			view.setItems(Arrays.asList("item1", "item2", "item3"));
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasStyle(1, 1, 5);
			assertThat(forScreen(screen7x10)).hasForegroundColor(1, 1, Color.RED);
			assertThat(forScreen(screen7x10)).hasBackgroundColor(1, 1, Color.BLUE);
		}

		@Test
		void showingAllCells() {
			view = new ListView<>();
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);
			view.setItems(Arrays.asList("item1", "item2", "item3"));
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
		}

		@Test
		void showingCellsWhichFitVertically() {
			view = new ListView<>();
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);
			view.setItems(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6", "item7"));
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			assertThat(forScreen(screen7x10)).hasNoHorizontalText("item6", 1, 6, 5);
		}

		@Test
		void scrollUpThrough() {
			view = new ListView<>();
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);
			view.setItems(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6", "item7"));

			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, -1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(2);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(4);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item7", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, -1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(2);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(3);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item7", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, -1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(2);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(2);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item7", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, -1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(2);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item7", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, -1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(2);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item7", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, -1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(1);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, -1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();
		}

		@Test
		void scrollDownThrough() {
			view = new ListView<>();
			view.setShowBorder(true);
			view.setRect(0, 0, 10, 7);

			view.setItems(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6", "item7"));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, 1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, 1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(2);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, 1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(3);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, 1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(4);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, 1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(1);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(4);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, 1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(2);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(4);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item6", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item7", 1, 5, 5);
			clearScreens();

			callVoidIntMethod(view, SCROLL_METHOD, 1);
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(0);
			view.draw(screen7x10);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item1", 1, 1, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item2", 1, 2, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item3", 1, 3, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item4", 1, 4, 5);
			assertThat(forScreen(screen7x10)).hasHorizontalText("item5", 1, 5, 5);
			clearScreens();
		}

	}

	@Nested
	class Radio {

		@Test
		void showRadio() {
			view = new ListView<>(ItemStyle.RADIO);
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.Space)));
			assertThat(getIntSetField(view, SELECTED_FIELD)).containsExactly(0);
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.CursorDown)));
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.Space)));
			assertThat(getIntSetField(view, SELECTED_FIELD)).containsExactly(1);
		}

	}

	@Nested
	class Checked {

		@Test
		void showChecked() {
			view = new ListView<>(ItemStyle.CHECKED);
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.Space)));
			assertThat(getIntSetField(view, SELECTED_FIELD)).containsExactly(0);
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.CursorDown)));
			view.getKeyHandler().handle(KeyHandler.argsOf(KeyEvent.of(Key.Space)));
			assertThat(getIntSetField(view, SELECTED_FIELD)).containsExactly(0, 1);
		}

	}

	@Nested
	class Factory {

		@Test
		void customCellFactory() {
			view = new ListView<>();
			view.setShowBorder(true);
			view.setCellFactory((list, item) -> new TestListCell(item));
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1"));
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasHorizontalText("pre-item1-post", 0, 1, 16);
		}

		static class TestListCell extends AbstractListCell<String> {

			TestListCell(String item) {
				super(item);
			}

			@Override
			public void draw(Screen screen) {
				Rectangle rect = getRect();
				Writer writer = screen.writerBuilder().build();
				writer.text(String.format("pre-%s-post", getItem()), rect.x(), rect.y());
				writer.background(rect, getBackgroundColor());
			}
		}

	}

	@Nested
	class ViewCommands {

		@BeforeEach
		void setup() {
			view = new ListView<>();
			configure(view);
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1", "item2"));
		}

		@Test
		void supports() {
			assertThat(view.getViewCommands()).contains(ViewCommand.LINE_DOWN, ViewCommand.LINE_UP);
		}

		@Test
		void lineDown() {
			StepVerifier verifier = StepVerifier.create(eventLoop.events())
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();
			view.runViewCommand(ViewCommand.LINE_DOWN);
			verifier.verify(Duration.ofSeconds(1));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
		}

		@Test
		void lineUp() {
			StepVerifier verifier = StepVerifier.create(eventLoop.events())
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();
			view.runViewCommand(ViewCommand.LINE_UP);
			verifier.verify(Duration.ofSeconds(1));
			assertThat(getIntField(view, START_FIELD)).isEqualTo(0);
			assertThat(getIntField(view, POSITION_FIELD)).isEqualTo(1);
		}

	}

}
