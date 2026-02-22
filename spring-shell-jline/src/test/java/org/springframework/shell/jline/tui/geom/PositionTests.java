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

class PositionTests {

	@Test
	void shouldStoreXAndY() {
		// when
		Position position = new Position(10, 20);

		// then
		assertEquals(10, position.x());
		assertEquals(20, position.y());
	}

	@Test
	void equalPositionsShouldBeEqual() {
		// given
		Position p1 = new Position(5, 10);
		Position p2 = new Position(5, 10);

		// then
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	void differentPositionsShouldNotBeEqual() {
		// given
		Position p1 = new Position(5, 10);
		Position p2 = new Position(10, 5);

		// then
		assertNotEquals(p1, p2);
	}

	@Test
	void shouldAllowZeroPosition() {
		// when
		Position position = new Position(0, 0);

		// then
		assertEquals(0, position.x());
		assertEquals(0, position.y());
	}

	@Test
	void shouldAllowNegativeCoordinates() {
		// when
		Position position = new Position(-5, -10);

		// then
		assertEquals(-5, position.x());
		assertEquals(-10, position.y());
	}

}
