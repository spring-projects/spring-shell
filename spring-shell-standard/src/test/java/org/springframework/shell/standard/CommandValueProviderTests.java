/*
 * Copyright 2017-2022 the original author or authors.
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
package org.springframework.shell.standard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CommandValueProvider}.
 *
 * @author Eric Bottard
 */
public class CommandValueProviderTests {

	@Mock
	private CommandCatalog catalog;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testValues() {
		CommandValueProvider valueProvider = new CommandValueProvider(catalog);

		CompletionContext completionContext = new CompletionContext(Arrays.asList("help", "m"), 0, 0, null, null);

		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("me", null);
		registrations.put("meow", null);
		registrations.put("yourself", null);

		when(catalog.getRegistrations()).thenReturn(registrations);
		List<CompletionProposal> proposals = valueProvider.complete(completionContext);

		assertThat(proposals).extracting("value", String.class)
			.contains("me", "meow", "yourself");
	}
}
