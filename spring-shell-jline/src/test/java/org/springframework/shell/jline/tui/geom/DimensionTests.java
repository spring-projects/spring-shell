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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DimensionTests {

	@Test
	void shouldStoreWidthAndHeight() {
		// when
		Dimension dimension = new Dimension(80, 24);

		// then
		assertEquals(80, dimension.width());
		assertEquals(24, dimension.height());
	}

	@Test
	void equalDimensionsShouldBeEqual() {
		// given
		Dimension d1 = new Dimension(100, 50);
		Dimension d2 = new Dimension(100, 50);

		// then
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
	}

	@Test
	void differentDimensionsShouldNotBeEqual() {
		// given
		Dimension d1 = new Dimension(100, 50);
		Dimension d2 = new Dimension(200, 50);

		// then
		assertNotEquals(d1, d2);
	}

	@Test
	void shouldAllowZeroDimensions() {
		// when
		Dimension dimension = new Dimension(0, 0);

		// then
		assertEquals(0, dimension.width());
		assertEquals(0, dimension.height());
	}

}
