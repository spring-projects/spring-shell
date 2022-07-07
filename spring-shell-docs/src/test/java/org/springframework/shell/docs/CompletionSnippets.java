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
package org.springframework.shell.docs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProvider;

public class CompletionSnippets {

	// tag::builder-1[]
	void dump1() {
		CommandRegistration.builder()
			.withOption()
				.longNames("arg1")
				.completion(ctx -> {
					return Arrays.asList("val1", "val2").stream()
						.map(CompletionProposal::new)
						.collect(Collectors.toList());
				})
				.and()
			.build();
	}
	// end::builder-1[]

	// tag::resolver-1[]
	static class MyValuesCompletionResolver implements CompletionResolver {

		@Override
		public List<CompletionProposal> apply(CompletionContext t) {
			return Arrays.asList("val1", "val2").stream()
				.map(CompletionProposal::new)
				.collect(Collectors.toList());
		}
	}
	// end::resolver-1[]

	// tag::provider-1[]
	static class MyValuesProvider implements ValueProvider {

		@Override
		public List<CompletionProposal> complete(CompletionContext completionContext) {
			return Arrays.asList("val1", "val2").stream()
				.map(CompletionProposal::new)
				.collect(Collectors.toList());
		}
	}
	// end::provider-1[]

	static class Dump1 {
		// tag::anno-method[]
		@ShellMethod(value = "complete", key = "complete")
		public String complete(
			@ShellOption(valueProvider = MyValuesProvider.class) String arg1)
		{
			return "You said " + arg1;
		}
		// end::anno-method[]
	}
}
