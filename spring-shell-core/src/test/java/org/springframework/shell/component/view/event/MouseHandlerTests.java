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
package org.springframework.shell.component.view.event;

import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerArgs;

import static org.assertj.core.api.Assertions.assertThat;

class MouseHandlerTests {

	private static final MouseEvent EVENT = MouseEvent.of(0, 0, 0);
	private static final MouseHandlerArgs ARGS = MouseHandler.argsOf(EVENT);

	@Test
	void handlesOtherIfThisConsumes() {
		TestMouseHandler h1 = new TestMouseHandler(true);
		TestMouseHandler h2 = new TestMouseHandler(false);
		MouseHandler composed = h1.thenIfConsumed(h2);
		composed.handle(ARGS);
		assertThat(h1.calls).isEqualTo(1);
		assertThat(h2.calls).isEqualTo(1);
	}

	@Test
	void doesNotHandlesOtherIfThisDoesNotConsume() {
		TestMouseHandler h1 = new TestMouseHandler(true);
		TestMouseHandler h2 = new TestMouseHandler(false);
		MouseHandler composed = h2.thenIfConsumed(h1);
		composed.handle(ARGS);
		assertThat(h1.calls).isEqualTo(0);
		assertThat(h2.calls).isEqualTo(1);
	}

	@Test
	void handlesOtherIfThisDoesNotConsume() {
		TestMouseHandler h1 = new TestMouseHandler(true);
		TestMouseHandler h2 = new TestMouseHandler(false);
		MouseHandler composed = h2.thenIfNotConsumed(h1);
		composed.handle(ARGS);
		assertThat(h1.calls).isEqualTo(1);
		assertThat(h2.calls).isEqualTo(1);
	}

	private static class TestMouseHandler implements MouseHandler {

		boolean willConsume;
		int calls;

		TestMouseHandler(boolean willConsume) {
			this.willConsume = willConsume;
		}

		@Override
		public MouseHandlerResult handle(MouseHandlerArgs args) {
			calls++;
			return MouseHandler.resultOf(args.event(), willConsume, null, null);
		}

	}

}
