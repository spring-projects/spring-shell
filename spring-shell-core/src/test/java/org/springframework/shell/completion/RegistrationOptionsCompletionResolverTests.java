/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.completion;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandRegistration;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationOptionsCompletionResolverTests {

	private final RegistrationOptionsCompletionResolver resolver = new RegistrationOptionsCompletionResolver();

	@Test
	void completesAllOptions() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.and()
			.build();
		CompletionContext ctx = new CompletionContext(Arrays.asList("hello", "world", ""), 2, "".length(), registration, null);
		List<CompletionProposal> proposals = resolver.apply(ctx);
		assertThat(proposals).isNotNull();
		assertThat(proposals).hasSize(2);
	}

	@Test
	void completesNonExistingOptions() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.and()
			.build();
		CompletionContext ctx = new CompletionContext(Arrays.asList("hello", "world", "--arg1", ""), 2, "".length(), registration, null);
		List<CompletionProposal> proposals = resolver.apply(ctx);
		assertThat(proposals).isNotNull();
		assertThat(proposals).hasSize(1);
	}

}
