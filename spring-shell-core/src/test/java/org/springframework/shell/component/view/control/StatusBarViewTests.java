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
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.shell.component.view.control.StatusBarView.StatusBarViewOpenSelectedItemEvent;
import org.springframework.shell.component.view.control.StatusBarView.StatusItem;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class StatusBarViewTests extends AbstractViewTests {

	@Nested
	class Construction {

		@Test
		void constructView() {
			StatusBarView view;

			view = new StatusBarView();
			assertThat(view.getItems()).hasSize(0);

			view = new StatusBarView(new StatusItem[] {
				new StatusItem("item1")
			});
			assertThat(view.getItems()).hasSize(1);

			view = new StatusBarView(Arrays.asList(new StatusItem("item1")));
			assertThat(view.getItems()).hasSize(1);

			view = new StatusBarView(Arrays.asList(StatusItem.of("item1")));
			assertThat(view.getItems()).hasSize(1);

			view = new StatusBarView(Arrays.asList(StatusItem.of("item1", null, null, false, 1)));
			assertThat(view.getItems()).hasSize(1);

		}

		@Test
		void hotkeys() {
			StatusItem item;

			item = StatusItem.of("title");
			assertThat(item.getHotKey()).isNull();
			item.setHotKey(Key.f);
			assertThat(item.getHotKey()).isEqualTo(Key.f);

			item = StatusItem.of("title").setHotKey(Key.f);
			assertThat(item.getHotKey()).isEqualTo(Key.f);
		}
	}

	@Nested
	class Internal {

		StatusBarView view;
		StatusItem item;

		@Test
		void itemPosition() {
			view = new StatusBarView(new StatusItem[] {
				new StatusItem("item1"),
				new StatusItem("item2")
			});
			view.setRect(0, 0, 10, 1);

			item = (StatusItem) ReflectionTestUtils.invokeMethod(view, "itemAt", 0, 0);
			assertThat(item).isNotNull();
			assertThat(item.getTitle()).isEqualTo("item1");

			item = (StatusItem) ReflectionTestUtils.invokeMethod(view, "itemAt", 7, 0);
			assertThat(item).isNotNull();
			assertThat(item.getTitle()).isEqualTo("item2");
		}

	}

	@Nested
	class Sorting {

		StatusItem p_0_1;
		StatusItem p_0_2;
		StatusItem p_0_3;
		StatusItem p_1_1;
		StatusItem p_2_1;
		StatusItem n_0_1;
		StatusItem n_0_2;
		StatusItem n_0_3;
		StatusBarView view;

		@BeforeEach
		void setup() {
			p_0_1 = new StatusItem("p_0_1");
			p_0_2 = new StatusItem("p_0_2");
			p_0_3 = new StatusItem("p_0_3");

			p_1_1 = new StatusItem("p_1_1");
			p_1_1.setPriority(1);
			p_2_1 = new StatusItem("p_2_1");
			p_2_1.setPriority(2);

			n_0_1 = new StatusItem("n_0_1");
			n_0_1.setPrimary(false);
			n_0_2 = new StatusItem("n_0_2");
			n_0_2.setPrimary(false);
			n_0_3 = new StatusItem("n_0_3");
			n_0_3.setPrimary(false);
		}

		@Test
		void defaultsOrderNotChanged() {
			view = new StatusBarView(Arrays.asList(p_0_1, p_0_2, p_0_3));
			assertThat(extractTitles()).containsExactly("p_0_1", "p_0_2", "p_0_3");
		}

		@Test
		void primaryBeforeNonprimary() {
			view = new StatusBarView(Arrays.asList(p_0_1, n_0_1));
			assertThat(extractTitles()).containsExactly("p_0_1", "n_0_1");
			view = new StatusBarView(Arrays.asList(n_0_1, p_0_1));
			assertThat(extractTitles()).containsExactly("p_0_1", "n_0_1");
		}

		@Test
		void priorityTakesOrder() {
			view = new StatusBarView(Arrays.asList(p_0_1, p_1_1, p_2_1));
			assertThat(extractTitles()).containsExactly("p_0_1", "p_1_1", "p_2_1");
			view = new StatusBarView(Arrays.asList(p_2_1, p_0_1, p_1_1));
			assertThat(extractTitles()).containsExactly("p_0_1", "p_1_1", "p_2_1");
		}

		private List<String> extractTitles() {
			return view.getItems().stream().map(StatusItem::getTitle).collect(Collectors.toList());
		}

	}

	@Nested
	class Styling {

		@Test
		void hasBorder() {
			StatusBarView view = new StatusBarView();
			view.setShowBorder(true);
			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		}

		@Test
		void primaryItems() {
			StatusItem item1 = new StatusItem("item1");
			StatusItem item2 = new StatusItem("item2");
			StatusBarView view = new StatusBarView(Arrays.asList(item1, item2));
			view.setItemSeparator(null);
			view.setRect(0, 0, 80, 1);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item1", 0, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item2", 5, 0, 5);
		}

		@Test
		void nonprimaryItems() {
			StatusItem item1 = new StatusItem("item1");
			StatusItem item2 = new StatusItem("item2");
			item1.setPrimary(false);
			item2.setPrimary(false);
			StatusBarView view = new StatusBarView(Arrays.asList(item1, item2));
			view.setItemSeparator(null);
			view.setRect(0, 0, 80, 1);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item1", 75, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item2", 70, 0, 5);
		}

		@Test
		void primaryAndNonprimaryItems() {
			StatusItem item1 = new StatusItem("item1");
			StatusItem item2 = new StatusItem("item2");
			item2.setPrimary(false);
			StatusBarView view = new StatusBarView(Arrays.asList(item1, item2));
			view.setItemSeparator(null);
			view.setRect(0, 0, 80, 1);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item1", 0, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item2", 75, 0, 5);
		}

		@Test
		void canChangeText() {
			StatusItem item1 = new StatusItem("item1");
			StatusBarView view = new StatusBarView(Arrays.asList(item1));
			view.setItemSeparator(null);
			view.setRect(0, 0, 80, 1);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item1", 0, 0, 5);
			item1.setTitle("fake");
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("fake", 0, 0, 4);
		}

		@Test
		void itemSeparator() {
			StatusItem item1 = new StatusItem("item1");
			StatusItem item2 = new StatusItem("item2");
			StatusItem item3 = new StatusItem("item3");
			item3.setPrimary(false);
			StatusItem item4 = new StatusItem("item4");
			item4.setPrimary(false);
			StatusBarView view = new StatusBarView(Arrays.asList(item1, item2, item3, item4));
			view.setRect(0, 0, 80, 1);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item1 | ", 0, 0, 8);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item2", 8, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText(" | item3", 72, 0, 8);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item4", 67, 0, 5);
			view.setItemSeparator(null);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item1", 0, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item2", 5, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item3", 75, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item4", 70, 0, 5);
		}

		@Test
		void skipItemsWhenOverflow() {
			StatusItem item1 = new StatusItem("item11111111111111111111111111");
			StatusItem item2 = new StatusItem("item22222222222222222222222222");
			StatusItem item3 = new StatusItem("item33333333333333333333333333");
			StatusBarView view = new StatusBarView(Arrays.asList(item1, item2, item3));
			view.setItemSeparator(null);
			view.setRect(0, 0, 80, 1);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item11111111111111111111111111", 0, 0, 30);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item22222222222222222222222222", 30, 0, 30);
			assertThat(forScreen(screen1x80)).hasHorizontalText("", 60, 0, 20);
		}

		@Test
		void nullTitleDontDraw() {
			StatusItem item1 = new StatusItem("item1");
			StatusItem item2 = new StatusItem(null);
			StatusBarView view = new StatusBarView(Arrays.asList(item1, item2));
			view.setItemSeparator(null);
			view.setRect(0, 0, 80, 1);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("item1", 0, 0, 5);
			assertThat(forScreen(screen1x80)).hasHorizontalText("", 5, 0, 5);
		}

	}

	@Nested
	class Events {

		StatusBarView view;

		@BeforeEach
		void setup() {
			view = new StatusBarView(new StatusItem[] {
				new StatusItem("item1")
			});
			configure(view);
			view.setRect(0, 0, 20, 1);
		}

		@Test
		void handlesMouseClickInItem() {
			MouseEvent click = mouseClick(1, 0);

			Flux<StatusBarViewOpenSelectedItemEvent> actions = eventLoop
					.viewEvents(StatusBarViewOpenSelectedItemEvent.class);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			MouseHandlerResult result = handleMouseClick(view, click);

			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isEqualTo(click);
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isNull();
				assertThat(r.capture()).isNull();
			});
			verifier.verify(Duration.ofSeconds(1));
		}


	}

}
