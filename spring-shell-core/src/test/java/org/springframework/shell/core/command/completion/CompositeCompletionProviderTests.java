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

class CompositeCompletionProviderTests {

	@Test
	void shouldCombineProposalsFromMultipleProviders() {
		// given
		CompletionProvider provider1 = ctx -> List.of(new CompletionProposal("alpha"), new CompletionProposal("beta"));
		CompletionProvider provider2 = ctx -> List.of(new CompletionProposal("gamma"));

		CompositeCompletionProvider composite = new CompositeCompletionProvider(provider1, provider2);
		CompletionContext context = new CompletionContext(List.of(), 0, 0, null, null);

		// when
		List<CompletionProposal> proposals = composite.apply(context);

		// then
		assertEquals(3, proposals.size());
		assertEquals("alpha", proposals.get(0).value());
		assertEquals("beta", proposals.get(1).value());
		assertEquals("gamma", proposals.get(2).value());
	}

	@Test
	void shouldReturnEmptyWhenNoProviders() {
		// given
		CompositeCompletionProvider composite = new CompositeCompletionProvider();
		CompletionContext context = new CompletionContext(List.of(), 0, 0, null, null);

		// when
		List<CompletionProposal> proposals = composite.apply(context);

		// then
		assertTrue(proposals.isEmpty());
	}

	@Test
	void shouldHandleSingleProvider() {
		// given
		CompletionProvider provider = ctx -> List.of(new CompletionProposal("only"));
		CompositeCompletionProvider composite = new CompositeCompletionProvider(provider);
		CompletionContext context = new CompletionContext(List.of(), 0, 0, null, null);

		// when
		List<CompletionProposal> proposals = composite.apply(context);

		// then
		assertEquals(1, proposals.size());
		assertEquals("only", proposals.get(0).value());
	}

}
