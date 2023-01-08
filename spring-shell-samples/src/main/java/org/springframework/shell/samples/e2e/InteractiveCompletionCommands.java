/*
 * Copyright 2022-2023 the original author or authors.
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
package org.springframework.shell.samples.e2e;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProvider;

@ShellComponent
public class InteractiveCompletionCommands extends BaseE2ECommands {

	@ShellMethod(key = LEGACY_ANNO + "interactive-completion-1", group = GROUP)
	public String testInteractiveCompletion1(
		@ShellOption(valueProvider = Test1ValuesProvider.class) String arg1,
		@ShellOption(valueProvider = Test2ValuesProvider.class) String arg2
	) {
		return "Hello " + arg1;
	}

	@Bean
	CommandRegistration testInteractiveCompletion1Registration(CommandRegistration.BuilderSupplier builder) {
		Test1ValuesProvider test1ValuesProvider = new Test1ValuesProvider();
		Test2ValuesProvider test2ValuesProvider = new Test2ValuesProvider();
		return builder.get()
			.command(REG, "interactive-completion-1")
			.group(GROUP)
			.withOption()
				.longNames("arg1")
				.completion(ctx -> test1ValuesProvider.complete(ctx))
				.and()
			.withOption()
				.longNames("arg2")
				.completion(ctx -> test2ValuesProvider.complete(ctx))
				.and()
			.withTarget()
				.function(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					return "Hello " + arg1;
				})
				.and()
			.build();
	}

	@Bean
	Test1ValuesProvider test1ValuesProvider() {
		return new Test1ValuesProvider();
	}

	@Bean
	Test2ValuesProvider test2ValuesProvider() {
		return new Test2ValuesProvider();
	}

	static class Test1ValuesProvider implements ValueProvider {

		private final static String[] VALUES = new String[] {
			"values1Complete1",
			"values1Complete2"
		};

		@Override
		public List<CompletionProposal> complete(CompletionContext completionContext) {
			return Arrays.stream(VALUES)
				.map(CompletionProposal::new)
				.collect(Collectors.toList());
		}
	}

	static class Test2ValuesProvider implements ValueProvider {

		private final static String[] VALUES = new String[] {
			"values2Complete1",
			"values2Complete2"
		};

		@Override
		public List<CompletionProposal> complete(CompletionContext completionContext) {
			return Arrays.stream(VALUES)
				.map(CompletionProposal::new)
				.collect(Collectors.toList());
		}
	}

}
