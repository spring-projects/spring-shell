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

import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyEvent.KeyMask;

import static org.assertj.core.api.Assertions.assertThat;

class KeyEventTests {

	@Test
	void hasCtrl() {
		assertThat(KeyEvent.of(Key.A).hasCtrl()).isFalse();
		assertThat(KeyEvent.of(Key.A | KeyMask.CtrlMask).hasCtrl()).isTrue();
	}

	@Test
	void plainKey() {
		assertThat(KeyEvent.of(Key.A).getPlainKey()).isEqualTo(Key.A);
		assertThat(KeyEvent.of(Key.A | KeyMask.CtrlMask).getPlainKey()).isEqualTo(Key.A);
	}

	@Test
	void isKey() {
		assertThat(KeyEvent.of(Key.A).isKey(Key.A)).isTrue();
		assertThat(KeyEvent.of(Key.A | KeyMask.CtrlMask).isKey(Key.A)).isTrue();
		assertThat(KeyEvent.of(Key.CursorDown).isKey(Key.CursorDown)).isTrue();
		assertThat(KeyEvent.of(Key.CursorLeft).isKey(Key.CursorRight)).isFalse();

	}

	@Test
	void isKey2() {
		assertThat(KeyEvent.of(Key.A).isKey()).isTrue();
		assertThat(KeyEvent.of(Key.Backspace).isKey()).isFalse();


	}

}
