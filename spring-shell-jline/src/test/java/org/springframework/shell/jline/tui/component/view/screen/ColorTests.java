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
package org.springframework.shell.jline.tui.component.view.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ColorTests {

	@Test
	void basicColorsShouldHaveCorrectValues() {
		assertEquals(0xffffff, Color.WHITE);
		assertEquals(0x000000, Color.BLACK);
		assertEquals(0xFF0000, Color.RED);
		assertEquals(0x00FF00, Color.GREEN);
		assertEquals(0x0000FF, Color.BLUE);
		assertEquals(0xFFFF00, Color.YELLOW);
		assertEquals(0x00FFFF, Color.CYAN);
		assertEquals(0xFF00FF, Color.MAGENTA);
	}

	@Test
	void greyScaleShouldBeOrdered() {
		// GREY0 is black, GREY100 is white
		assertEquals(0x000000, Color.GREY0);
		assertEquals(0xffffff, Color.GREY100);
	}

	@Test
	void numberedVariantsShouldDifferFromBase() {
		// Numbered variants (1-4) have slightly different hex values
		assertNotEquals(Color.RED, Color.RED2);
		assertNotEquals(Color.RED, Color.RED3);
		assertNotEquals(Color.RED, Color.RED4);
	}

	@Test
	void colorConstantsShouldBePositive() {
		// All color hex values should be non-negative
		assert Color.RED > 0;
		assert Color.GREEN > 0;
		assert Color.BLUE > 0;
		assert Color.WHITE > 0;
	}

}
