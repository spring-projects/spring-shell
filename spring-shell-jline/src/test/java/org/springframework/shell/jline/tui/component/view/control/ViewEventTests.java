/*
 * Copyright 2025-present the original author or authors.
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
package org.springframework.shell.jline.tui.component.view.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ViewEventTests {

	@Test
	void viewDoneEventShouldHaveViewAndEmptyArgs() {
		// given
		View mockView = mock(View.class);

		// when
		ViewDoneEvent event = ViewDoneEvent.of(mockView);

		// then
		assertEquals(mockView, event.view());
		assertEquals(ViewEventArgs.EMPTY, event.args());
	}

	@Test
	void viewEventArgsShouldHaveEmptyConstant() {
		// then
		assertNotNull(ViewEventArgs.EMPTY);
	}

	@Test
	void genericViewDoneEventShouldBeRecord() {
		// given
		View mockView = mock(View.class);

		// when
		ViewDoneEvent.GenericViewDoneEvent event = new ViewDoneEvent.GenericViewDoneEvent(mockView,
				ViewEventArgs.EMPTY);

		// then
		assertEquals(mockView, event.view());
		assertEquals(ViewEventArgs.EMPTY, event.args());
	}

}
