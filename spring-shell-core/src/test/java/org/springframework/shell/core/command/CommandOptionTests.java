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
package org.springframework.shell.core.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandOptionTests {

	@Test
	void isOptionEqualShouldMatchLongName() {
		// given
		CommandOption option = CommandOption.with().longName("verbose").build();

		// then
		assertTrue(option.isOptionEqual("--verbose"));
		assertFalse(option.isOptionEqual("--debug"));
	}

	@Test
	void isOptionEqualShouldMatchShortName() {
		// given
		CommandOption option = CommandOption.with().shortName('v').build();

		// then
		assertTrue(option.isOptionEqual("-v"));
		assertFalse(option.isOptionEqual("-d"));
	}

	@Test
	void isOptionEqualShouldNotMatchDefaultShortName() {
		// given - default short name is space character
		CommandOption option = CommandOption.with().longName("verbose").build();

		// then
		assertFalse(option.isOptionEqual("- "));
	}

	@Test
	void builderShouldBuildWithAllFields() {
		// when
		CommandOption option = CommandOption.with()
			.shortName('f')
			.longName("file")
			.description("Input file")
			.required(true)
			.defaultValue("data.txt")
			.value("input.txt")
			.type(String.class)
			.build();

		// then
		assertEquals('f', option.shortName());
		assertEquals("file", option.longName());
		assertEquals("Input file", option.description());
		assertTrue(option.required());
		assertEquals("data.txt", option.defaultValue());
		assertEquals("input.txt", option.value());
		assertEquals(String.class, option.type());
	}

	@Test
	void builderShouldHaveDefaults() {
		// when
		CommandOption option = CommandOption.with().build();

		// then
		assertEquals(' ', option.shortName());
		assertEquals(Object.class, option.type());
	}

}
