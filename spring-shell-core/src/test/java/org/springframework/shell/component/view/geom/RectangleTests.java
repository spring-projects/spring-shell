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
package org.springframework.shell.component.view.geom;

import org.junit.jupiter.api.Test;

import org.springframework.shell.geom.Rectangle;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleTests {

	private Rectangle rect;

	@Test
	void isEmpty() {
		rect = new Rectangle(0, 0, 0, 0);
		assertThat(rect.isEmpty()).isTrue();
		rect = new Rectangle(1, 0, 0, 0);
		assertThat(rect.isEmpty()).isTrue();
		rect = new Rectangle(0, 1, 0, 0);
		assertThat(rect.isEmpty()).isTrue();
		rect = new Rectangle(1, 1, 0, 0);
		assertThat(rect.isEmpty()).isTrue();
		rect = new Rectangle(0, 0, 1, 0);
		assertThat(rect.isEmpty()).isTrue();
		rect = new Rectangle(0, 0, 0, 1);
		assertThat(rect.isEmpty()).isTrue();
		rect = new Rectangle(1, 1, 1, 1);
		assertThat(rect.isEmpty()).isFalse();
	}
}
