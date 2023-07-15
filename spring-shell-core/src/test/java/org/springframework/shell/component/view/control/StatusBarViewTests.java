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

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.shell.component.view.control.StatusBarView.StatusBarViewOpenSelectedItemEvent;
import org.springframework.shell.component.view.control.StatusBarView.StatusItem;
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
	class Styling {

		@Test
		void hasBorder() {
			StatusBarView view = new StatusBarView();
			view.setShowBorder(true);
			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
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
