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

import org.springframework.shell.component.view.event.KeyEvent;

import static org.assertj.core.api.Assertions.assertThat;

class InputViewTests extends AbstractViewTests {

	@Test
	void shouldShowInput() {
		InputView view = new InputView();
		view.setShowBorder(true);
		view.setRect(0, 0, 80, 24);

		dispatchEvent(view, KeyEvent.of('1'));
		view.draw(screen24x80);

		assertThat(forScreen(screen24x80)).hasHorizontalText("1", 1, 1, 1);
		assertThat(forScreen(screen24x80)).hasCursorInPosition(2, 1);
	}

	@Test
	void shouldShowUnicode() {
		InputView view = new InputView();
		view.setShowBorder(true);
		view.setRect(0, 0, 80, 24);

		dispatchEvent(view, KeyEvent.of('★'));
		view.draw(screen24x80);

		assertThat(forScreen(screen24x80)).hasHorizontalText("★", 1, 1, 1);
		assertThat(forScreen(screen24x80)).hasCursorInPosition(2, 1);
	}
}
