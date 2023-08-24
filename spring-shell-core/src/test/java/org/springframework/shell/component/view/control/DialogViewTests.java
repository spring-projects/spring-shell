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

import org.springframework.shell.component.view.control.ButtonView.ButtonViewSelectEvent;
import org.springframework.shell.component.view.control.DialogView.DialogViewCloseEvent;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;

import static org.assertj.core.api.Assertions.assertThat;

class DialogViewTests extends AbstractViewTests {

	private DialogView view;

	@Nested
	class Construction {

		@Test
		void constructView() {
			view = new DialogView();

			ButtonView button = new ButtonView("text");
			view = new DialogView(new BoxView(), Arrays.asList(button));
		}

	}

	@Nested
	class Events {

		@BeforeEach
		void setup() {
			ButtonView button = new ButtonView("text");
			configure(button);
			view = new DialogView(null, button);
			configure(view);
			view.setRect(0, 0, 10, 10);
		}

		@Test
		void handlesMouseClick() {
			MouseEvent click = mouseClick(1, 6);

			Flux<ButtonViewSelectEvent> actions1 = eventLoop
					.viewEvents(ButtonViewSelectEvent.class);
			Flux<DialogViewCloseEvent> actions2 = eventLoop
					.viewEvents(DialogViewCloseEvent.class);
			StepVerifier verifier1 = StepVerifier.create(actions1)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();
			StepVerifier verifier2 = StepVerifier.create(actions2)
				.expectNextCount(1)
				.thenCancel()
				.verifyLater();

			MouseHandlerResult result = handleMouseClick(view, click);

			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.consumed()).isTrue();
			});
			verifier1.verify(Duration.ofSeconds(1));
			verifier2.verify(Duration.ofSeconds(1));
		}

	}

	@Nested
	class Visual {

	}

}
