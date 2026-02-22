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
package org.springframework.shell.jline.tui.geom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HorizontalAlignTests {

	@Test
	void shouldHaveThreeValues() {
		assertEquals(3, HorizontalAlign.values().length);
	}

	@Test
	void shouldContainExpectedValues() {
		assertEquals(HorizontalAlign.LEFT, HorizontalAlign.valueOf("LEFT"));
		assertEquals(HorizontalAlign.CENTER, HorizontalAlign.valueOf("CENTER"));
		assertEquals(HorizontalAlign.RIGHT, HorizontalAlign.valueOf("RIGHT"));
	}

}
