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

import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.shell.component.view.screen.Screen;

import static org.assertj.core.api.Assertions.assertThat;

class BaseViewTests extends AbstractViewTests {

	TestView view;

	@Nested
	class Mouse {

		@BeforeEach
		void setup() {
			view = new TestView();
			configure(view);
		}

		@Test
		void clickInBounds() {
			view.setRect(0, 0, 80, 24);
			MouseHandlerResult result = handleMouseClick(view, 0, 0);
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.consumed()).isFalse();
				assertThat(r.focus()).isEqualTo(view);
				assertThat(r.capture()).isEqualTo(view);
			});
		}

		@Test
		void clickOutOfBounds() {
			view.setRect(0, 0, 80, 24);
			MouseHandlerResult result = handleMouseClick(view, 100, 100);
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.consumed()).isFalse();
				assertThat(r.focus()).isNull();
				assertThat(r.capture()).isEqualTo(view);
			});
		}

	}

	private static class TestView extends AbstractView {

		@Override
		protected void drawInternal(Screen screen) {
		}

	}

}
