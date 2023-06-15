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
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.command.annotation.OptionValues;
import org.springframework.shell.completion.CompletionProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProvider;
import org.springframework.stereotype.Component;

public class InteractiveCompletionCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "interactive-completion-1", group = GROUP)
		public String testInteractiveCompletion1(
			@ShellOption(valueProvider = Test1ValuesProvider.class) String arg1,
			@ShellOption(valueProvider = Test2ValuesProvider.class) String arg2
		) {
			return "Hello " + arg1;
		}

		@Bean
		Test1ValuesProvider test1ValuesProvider() {
			return new Test1ValuesProvider();
		}

		@Bean
		Test2ValuesProvider test2ValuesProvider() {
			return new Test2ValuesProvider();
		}
	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "interactive-completion-1")
		public String testRequiredValueAnnotation(
				@Option(longNames = "arg1", required = true) @OptionValues(provider = "test1CompletionProvider") String arg1,
				@Option(longNames = "arg2", required = true) @OptionValues(provider = "test2CompletionProvider") String arg2
		) {
				return "Hello " + arg1;
		}

		@Bean
		CompletionProvider test1CompletionProvider() {
			return ctx -> {
				Test1ValuesProvider test1ValuesProvider = new Test1ValuesProvider();
				return test1ValuesProvider.complete(ctx);
			};
		}

		@Bean
		CompletionProvider test2CompletionProvider() {
			return ctx -> {
				Test2ValuesProvider test2ValuesProvider = new Test2ValuesProvider();
				return test2ValuesProvider.complete(ctx);
			};
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		CommandRegistration testInteractiveCompletion1Registration() {
			Test1ValuesProvider test1ValuesProvider = new Test1ValuesProvider();
			Test2ValuesProvider test2ValuesProvider = new Test2ValuesProvider();
			return getBuilder()
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
