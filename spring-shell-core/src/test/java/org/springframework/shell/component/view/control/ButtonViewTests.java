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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.shell.component.view.control.ButtonView.ButtonViewSelectEvent;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyHandler.KeyHandlerResult;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;

import static org.assertj.core.api.Assertions.assertThat;

class ButtonViewTests extends AbstractViewTests {

	static final String TEXT_FIELD = "text";
	static final String ACTION_FIELD = "action";
	private ButtonView view;

	@Nested
	class Construction {

		@Test
		void defaultView() {
			view = new ButtonView();
			assertThat(getStringField(view, TEXT_FIELD)).isNull();
			assertThat(getRunnableField(view, ACTION_FIELD)).isNull();
		}

		@Test
		void text() {
			view = new ButtonView("text");
			assertThat(getStringField(view, TEXT_FIELD)).isEqualTo("text");
			assertThat(getRunnableField(view, ACTION_FIELD)).isNull();
		}

		@Test
		void textAndAction() {
			view = new ButtonView("text", () -> {});
			assertThat(getStringField(view, TEXT_FIELD)).isEqualTo("text");
			assertThat(getRunnableField(view, ACTION_FIELD)).isNotNull();
		}

		@Test
		void canSetTextAndAction() {
			view = new ButtonView();
			view.setText("text");
			view.setAction(() -> {});
			assertThat(getStringField(view, TEXT_FIELD)).isEqualTo("text");
			assertThat(getRunnableField(view, ACTION_FIELD)).isNotNull();
		}

	}

	@Nested
	class Events {

		@BeforeEach
		void setup() {
			view = new ButtonView();
			view.setRect(0, 0, 10, 10);
			configure(view);
		}

		@Test
		void handlesMouseClick() {
			MouseEvent click = mouseClick(1, 1);

			Flux<ButtonViewSelectEvent> actions = eventLoop
					.viewEvents(ButtonViewSelectEvent.class);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			MouseHandlerResult result = handleMouseClick(view, click);

			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.consumed()).isTrue();
			});
			verifier.verify(Duration.ofSeconds(1));
		}

		@Test
		void handlesKeyEnter() {
			Flux<ButtonViewSelectEvent> actions = eventLoop
					.viewEvents(ButtonViewSelectEvent.class);
			StepVerifier verifier = StepVerifier.create(actions)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			KeyHandlerResult result = handleKey(view, KeyEvent.Key.Enter);

			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.consumed()).isTrue();
			});
			verifier.verify(Duration.ofSeconds(1));
		}

	}

	@Nested
	class Visual {

		@BeforeEach
		void setup() {
			view = new ButtonView("text");
		}

		@Test
		void hasButtonText() {
			view.setRect(0, 0, 10, 10);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasBorder(0, 0, 10, 10);
			assertThat(forScreen(screen10x10)).hasHorizontalText("text", 3, 5, 4);
		}

		@Test
		void hasButton() {
			view.setRect(2, 2, 6, 3);
			view.draw(screen10x10);
			assertThat(forScreen(screen10x10)).hasBorder(2, 2, 6, 3);
			assertThat(forScreen(screen10x10)).hasHorizontalText("text", 3, 3, 4);
		}

	}

}
