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
package org.springframework.shell.core.command.completion;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.command.CommandOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompletionContextTests {

	@Test
	void currentWordShouldReturnWordAtIndex() {
		// given
		List<String> words = Arrays.asList("--name", "john");
		CompletionContext ctx = new CompletionContext(words, 1, 4, null, null);

		// then
		assertEquals("john", ctx.currentWord());
	}

	@Test
	void currentWordShouldReturnNullWhenIndexOutOfBounds() {
		// given
		List<String> words = Arrays.asList("--name");
		CompletionContext ctx = new CompletionContext(words, 5, 0, null, null);

		// then
		assertNull(ctx.currentWord());
	}

	@Test
	void currentWordUpToCursorShouldReturnSubstring() {
		// given
		List<String> words = Arrays.asList("--verbose");
		CompletionContext ctx = new CompletionContext(words, 0, 4, null, null);

		// then
		assertEquals("--ve", ctx.currentWordUpToCursor());
	}

	@Test
	void currentWordUpToCursorShouldReturnNullWhenNoCurrentWord() {
		// given
		List<String> words = Arrays.asList("--name");
		CompletionContext ctx = new CompletionContext(words, 5, 0, null, null);

		// then
		assertNull(ctx.currentWordUpToCursor());
	}

	@Test
	void upToCursorShouldReturnPartialInput() {
		// given
		List<String> words = Arrays.asList("--name", "john");
		CompletionContext ctx = new CompletionContext(words, 1, 2, null, null);

		// when
		String result = ctx.upToCursor();

		// then
		assertEquals("--name jo", result);
	}

	@Test
	void upToCursorShouldHandleEmptyWords() {
		// given
		List<String> words = List.of();
		CompletionContext ctx = new CompletionContext(words, 0, 0, null, null);

		// when
		String result = ctx.upToCursor();

		// then
		assertEquals("", result);
	}

	@Test
	void dropShouldRemoveFirstNWords() {
		// given
		List<String> words = Arrays.asList("sub", "--name", "john");
		CompletionContext ctx = new CompletionContext(words, 2, 3, null, null);

		// when
		CompletionContext dropped = ctx.drop(1);

		// then
		assertEquals(2, dropped.getWords().size());
		assertEquals("--name", dropped.getWords().get(0));
		assertEquals(1, dropped.getWordIndex());
	}

	@Test
	void commandOptionShouldReturnCopyWithOption() {
		// given
		CompletionContext ctx = new CompletionContext(List.of("val"), 0, 0, null, null);
		CommandOption option = CommandOption.with().longName("name").build();

		// when
		CompletionContext withOption = ctx.commandOption(option);

		// then
		assertNotNull(withOption.getCommandOption());
		assertEquals("name", withOption.getCommandOption().longName());
		assertNull(ctx.getCommandOption());
	}

	@Test
	void gettersShouldReturnCorrectValues() {
		// given
		List<String> words = Arrays.asList("a", "b");
		CompletionContext ctx = new CompletionContext(words, 1, 3, null, null);

		// then
		assertEquals(words, ctx.getWords());
		assertEquals(1, ctx.getWordIndex());
		assertEquals(3, ctx.getPosition());
		assertNull(ctx.getCommand());
		assertNull(ctx.getCommandOption());
	}

}
