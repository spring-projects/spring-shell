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

import org.springframework.shell.component.view.event.MouseEvent.Button;
import org.springframework.shell.component.view.event.MouseEvent.Modifier;
import org.springframework.shell.component.view.event.MouseEvent.Type;

import static org.assertj.core.api.Assertions.assertThat;

class MouseEventTests {

	@Test
	void hasType() {
		assertThat(MouseEvent.of(0, 0, 0).hasType()).isFalse();

		assertThat(MouseEvent.of(0, 0, Modifier.Shift).hasType()).isFalse();
		assertThat(MouseEvent.of(0, 0, Modifier.Alt).hasType()).isFalse();
		assertThat(MouseEvent.of(0, 0, Modifier.Control).hasType()).isFalse();

		assertThat(MouseEvent.of(0, 0, Type.Released).hasType()).isTrue();
		assertThat(MouseEvent.of(0, 0, Type.Pressed).hasType()).isTrue();
		assertThat(MouseEvent.of(0, 0, Type.Wheel).hasType()).isTrue();
		assertThat(MouseEvent.of(0, 0, Type.Moved).hasType()).isTrue();
		assertThat(MouseEvent.of(0, 0, Type.Dragged).hasType()).isTrue();

		assertThat(MouseEvent.of(0, 0, Button.Button1).hasType()).isFalse();
		assertThat(MouseEvent.of(0, 0, Button.Button2).hasType()).isFalse();
		assertThat(MouseEvent.of(0, 0, Button.Button3).hasType()).isFalse();

		assertThat(MouseEvent.of(0, 0, Button.Button3 | Modifier.Control).hasType()).isFalse();
		assertThat(MouseEvent.of(0, 0, Modifier.Shift | Type.Dragged).hasType()).isTrue();
	}

	@Test
	void hasButton() {
		assertThat(MouseEvent.of(0, 0, 0).hasButton()).isFalse();

		assertThat(MouseEvent.of(0, 0, Modifier.Shift).hasButton()).isFalse();
		assertThat(MouseEvent.of(0, 0, Modifier.Alt).hasButton()).isFalse();
		assertThat(MouseEvent.of(0, 0, Modifier.Control).hasButton()).isFalse();

		assertThat(MouseEvent.of(0, 0, Type.Released).hasButton()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Pressed).hasButton()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Wheel).hasButton()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Moved).hasButton()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Dragged).hasButton()).isFalse();

		assertThat(MouseEvent.of(0, 0, Button.Button1).hasButton()).isTrue();
		assertThat(MouseEvent.of(0, 0, Button.Button2).hasButton()).isTrue();
		assertThat(MouseEvent.of(0, 0, Button.Button3).hasButton()).isTrue();

		assertThat(MouseEvent.of(0, 0, Button.Button3 | Modifier.Control).hasButton()).isTrue();
		assertThat(MouseEvent.of(0, 0, Modifier.Shift | Type.Dragged).hasButton()).isFalse();
	}

	@Test
	void hasModifier() {
		assertThat(MouseEvent.of(0, 0, 0).hasModifier()).isFalse();

		assertThat(MouseEvent.of(0, 0, Modifier.Shift).hasModifier()).isTrue();
		assertThat(MouseEvent.of(0, 0, Modifier.Alt).hasModifier()).isTrue();
		assertThat(MouseEvent.of(0, 0, Modifier.Control).hasModifier()).isTrue();

		assertThat(MouseEvent.of(0, 0, Type.Released).hasModifier()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Pressed).hasModifier()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Wheel).hasModifier()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Moved).hasModifier()).isFalse();
		assertThat(MouseEvent.of(0, 0, Type.Dragged).hasModifier()).isFalse();

		assertThat(MouseEvent.of(0, 0, Button.Button1).hasModifier()).isFalse();
		assertThat(MouseEvent.of(0, 0, Button.Button2).hasModifier()).isFalse();
		assertThat(MouseEvent.of(0, 0, Button.Button3).hasModifier()).isFalse();

		assertThat(MouseEvent.of(0, 0, Button.Button3 | Type.Dragged).hasModifier()).isFalse();
		assertThat(MouseEvent.of(0, 0, Modifier.Shift | Type.Dragged).hasModifier()).isTrue();
	}

	@Test
	void testBaseIdeas() {
		assertThat(MouseEvent.of(0, 0, Modifier.Shift).has(Modifier.Shift)).isTrue();
		assertThat(MouseEvent.of(0, 0, Modifier.Shift).has(Modifier.Alt)).isFalse();
	}

}
