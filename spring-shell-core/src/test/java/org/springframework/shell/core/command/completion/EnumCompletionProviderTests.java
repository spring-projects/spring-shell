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

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumCompletionProviderTests {

	enum Color {

		RED, GREEN, BLUE

	}

	@Test
	void shouldProvideAllEnumValues() {
		// given
		EnumCompletionProvider provider = new EnumCompletionProvider(Color.class);
		CompletionContext context = new CompletionContext(List.of(), 0, 0, null, null);

		// when
		List<CompletionProposal> proposals = provider.apply(context);

		// then
		assertEquals(3, proposals.size());
		assertEquals("RED", proposals.get(0).value());
		assertEquals("GREEN", proposals.get(1).value());
		assertEquals("BLUE", proposals.get(2).value());
	}

	@Test
	void shouldProvideEnumValuesWithPrefix() {
		// given
		EnumCompletionProvider provider = new EnumCompletionProvider(Color.class, "color");
		CompletionContext context = new CompletionContext(List.of(), 0, 0, null, null);

		// when
		List<CompletionProposal> proposals = provider.apply(context);

		// then
		assertEquals(3, proposals.size());
		assertEquals("color=RED", proposals.get(0).value());
		assertEquals("color=GREEN", proposals.get(1).value());
		assertEquals("color=BLUE", proposals.get(2).value());
	}

	@Test
	void shouldThrowForNonEnumType() {
		// given
		EnumCompletionProvider provider = new EnumCompletionProvider(String.class);
		CompletionContext context = new CompletionContext(List.of(), 0, 0, null, null);

		// when / then - getEnumConstants() returns null for non-enum types
		org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> provider.apply(context));
	}

}
