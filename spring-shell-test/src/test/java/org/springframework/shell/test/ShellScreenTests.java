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
package org.springframework.shell.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShellScreenTests {

	@Test
	void ofShouldCreateScreenWithLines() {
		// given
		List<String> lines = Arrays.asList("line1", "line2", "line3");

		// when
		ShellScreen screen = ShellScreen.of(lines);

		// then
		assertEquals(3, screen.lines().size());
		assertEquals("line1", screen.lines().get(0));
		assertEquals("line2", screen.lines().get(1));
		assertEquals("line3", screen.lines().get(2));
	}

	@Test
	void ofShouldCreateScreenWithEmptyLines() {
		// when
		ShellScreen screen = ShellScreen.of(Collections.emptyList());

		// then
		assertTrue(screen.lines().isEmpty());
	}

	@Test
	void ofShouldCreateScreenWithSingleLine() {
		// when
		ShellScreen screen = ShellScreen.of(List.of("single line"));

		// then
		assertEquals(1, screen.lines().size());
		assertEquals("single line", screen.lines().get(0));
	}

	@Test
	void equalScreensShouldBeEqual() {
		// given
		ShellScreen screen1 = ShellScreen.of(List.of("a", "b"));
		ShellScreen screen2 = ShellScreen.of(List.of("a", "b"));

		// then
		assertEquals(screen1, screen2);
		assertEquals(screen1.hashCode(), screen2.hashCode());
	}

	@Test
	void differentScreensShouldNotBeEqual() {
		// given
		ShellScreen screen1 = ShellScreen.of(List.of("a", "b"));
		ShellScreen screen2 = ShellScreen.of(List.of("c", "d"));

		// then
		assertNotEquals(screen1, screen2);
	}

	@Test
	void constructorAndFactoryMethodShouldProduceEqualResults() {
		// given
		List<String> lines = List.of("test");

		// when
		ShellScreen fromConstructor = new ShellScreen(lines);
		ShellScreen fromFactory = ShellScreen.of(lines);

		// then
		assertEquals(fromConstructor, fromFactory);
	}

}
