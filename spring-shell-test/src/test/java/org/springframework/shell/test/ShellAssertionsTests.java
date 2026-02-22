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
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShellAssertionsTests {

	@Test
	void assertThatShouldReturnNonNullAssert() {
		// given
		ShellScreen screen = ShellScreen.of(List.of("line1"));

		// when
		ShellScreenAssert result = ShellAssertions.assertThat(screen);

		// then
		assertNotNull(result);
	}

	@Test
	void assertThatShouldReturnUsableAssert() {
		// given
		ShellScreen screen = ShellScreen.of(Arrays.asList("hello", "world"));

		// when / then - should not throw
		ShellAssertions.assertThat(screen).containsText("hello");
		ShellAssertions.assertThat(screen).containsText("world");
	}

	@Test
	void assertThatShouldSupportChaining() {
		// given
		ShellScreen screen = ShellScreen.of(Arrays.asList("hello world", "foo bar"));

		// when / then - chaining should work
		ShellAssertions.assertThat(screen).containsText("hello").containsText("foo");
	}

	@Test
	void assertThatShouldFailForMissingText() {
		// given
		ShellScreen screen = ShellScreen.of(List.of("hello"));

		// when / then
		assertThrows(AssertionError.class, () -> ShellAssertions.assertThat(screen).containsText("missing"));
	}

}
