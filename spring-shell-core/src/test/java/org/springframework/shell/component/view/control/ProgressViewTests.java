/*
 * Copyright 2024 the original author or authors.
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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.shell.component.view.control.ProgressView.ProgressViewEndEvent;
import org.springframework.shell.component.view.control.ProgressView.ProgressViewItem;
import org.springframework.shell.component.view.control.ProgressView.ProgressViewStartEvent;
import org.springframework.shell.component.view.control.ProgressView.ProgressViewStateChangeEvent;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgressViewTests extends AbstractViewTests {

	@Nested
	class Construction {

		ProgressView view;

		@Test
		void constructDefault() {
			view = new ProgressView();
			assertThat(getViewItems(view)).hasSize(3);
			assertThat(view.getState().tickValue()).isEqualTo(0);
		}

		@Test
		void constructBounds() {
			view = new ProgressView(10, 30);
			assertThat(getViewItems(view)).hasSize(3);
			assertThat(view.getState().tickValue()).isEqualTo(10);
			assertThat(view.getState().tickStart()).isEqualTo(10);
			assertThat(view.getState().tickEnd()).isEqualTo(30);
		}

		@Test
		void constructJustText() {
			view = new ProgressView(10, 30, ProgressViewItem.ofText());
			assertThat(getViewItems(view)).hasSize(1);
		}

		@Test
		void constructJustTextJustItems() {
			view = new ProgressView(ProgressViewItem.ofText());
			assertThat(getViewItems(view)).hasSize(1);
		}

	}

	@Nested
	class Visual {

		ProgressView view;

		@BeforeEach
		void setup() {
			view = new ProgressView();
			view.setDescription("name");
			view.setRect(0, 0, 80, 1);
			configure(view);
		}

		@Test
		void defaultItems() {
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("name", 11, 0, 4);
			assertThat(forScreen(screen1x80)).hasHorizontalText("-", 39, 0, 1);
			assertThat(forScreen(screen1x80)).hasHorizontalText("0%", 65, 0, 2);
		}

		@Test
		void advance() {
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("0%", 65, 0, 2);
			view.tickAdvance(5);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("5%", 65, 0, 2);
			view.tickAdvance(5);
			view.draw(screen1x80);
			assertThat(forScreen(screen1x80)).hasHorizontalText("10%", 65, 0, 3);
		}

	}

	@Nested
	class State {

		ProgressView view;

		@BeforeEach
		void setup() {
			view = new ProgressView();
			view.setRect(0, 0, 20, 1);
			configure(view);
		}

		@Test
		void dontAdvanceBounds() {
			assertThat(view.getState().tickValue()).isEqualTo(0);
			view.tickAdvance(-1);
			assertThat(view.getState().tickValue()).isEqualTo(0);
			view.tickAdvance(100);
			assertThat(view.getState().tickValue()).isEqualTo(100);
			view.tickAdvance(1);
			assertThat(view.getState().tickValue()).isEqualTo(100);
		}

	}

	@Nested
	class Events {

		ProgressView view;

		@BeforeEach
		void setup() {
			view = new ProgressView();
			view.setRect(0, 0, 20, 1);
			configure(view);
		}

		@Test
		void plainStartStop() {
			Flux<ProgressViewStartEvent> startEvents = eventLoop.viewEvents(ProgressViewStartEvent.class);
			Flux<ProgressViewEndEvent> endEvents = eventLoop.viewEvents(ProgressViewEndEvent.class);

			StepVerifier startVerifier = StepVerifier.create(startEvents)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();
			StepVerifier endVerifier = StepVerifier.create(endEvents)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			view.start();
			view.stop();
			startVerifier.verify(Duration.ofSeconds(1));
			endVerifier.verify(Duration.ofSeconds(1));
		}

		@Test
		void stateChangeWithTickValue() {
			Flux<ProgressViewStateChangeEvent> changeEvents = eventLoop.viewEvents(ProgressViewStateChangeEvent.class);
			StepVerifier verifier = StepVerifier.create(changeEvents)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			view.setTickValue(1);
			verifier.verify(Duration.ofSeconds(1));
		}

	}

	@Nested
	class Styling {

		@Test
		void hasBorder() {
			ProgressView view = new ProgressView();
			configure(view);
			view.setShowBorder(true);
			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
		}

	}

	private static List<ProgressViewItem> getViewItems(ProgressView view) {

		@SuppressWarnings("unchecked")
		List<ProgressViewItem> items = (List<ProgressViewItem>) ReflectionTestUtils.getField(view, "items");
		return items;
	}
}
