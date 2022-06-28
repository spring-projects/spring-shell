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
package org.springframework.shell.samples.standard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProvider;

@ShellComponent
public class CompleteCommands {

	@Bean
	CommandRegistration completeCommandRegistration1() {
		return CommandRegistration.builder()
			.command("complete", "sample1")
			.description("complete sample1")
			.group("Complete Commands")
			.withOption()
				.longNames("arg1")
				.completion(ctx -> {
					CompletionProposal p1 = new CompletionProposal("arg1hi1");
					CompletionProposal p2 = new CompletionProposal("arg1hi2");
					return Arrays.asList(p1, p2);
				})
				.and()
			.withOption()
				.longNames("arg2")
				.completion(ctx -> {
					CompletionProposal p1 = new CompletionProposal("arg2hi1");
					CompletionProposal p2 = new CompletionProposal("arg2hi2");
					return Arrays.asList(p1, p2);
				})
				.and()
			.withTarget()
				.function(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					return String.format("hi, arg1 value is '%s'", arg1);
				})
				.and()
			.build();
	}

	@ShellMethod(value = "complete sample2", key = "complete sample2")
	public String completeCommandSample2(@ShellOption(valueProvider = FunnyValuesProvider.class) String arg1) {
		return "You said " + arg1;
	}

	@Bean
	FunnyValuesProvider funnyValuesProvider() {
		return new FunnyValuesProvider();
	}

	static class FunnyValuesProvider implements ValueProvider {

		private final static String[] VALUES = new String[] {
			"hello world",
			"I am quoting \"The Daily Mail\"",
			"10 \\ 3 = 3"
		};

		@Override
		public List<CompletionProposal> complete(CompletionContext completionContext) {
			return Arrays.stream(VALUES).map(CompletionProposal::new).collect(Collectors.toList());
		}
	}

	@Bean
	CommandRegistration completeCommandRegistration3() {
		return CommandRegistration.builder()
			.command("complete", "sample3")
			.description("complete sample3")
			.group("Complete Commands")
			.withOption()
				.longNames("arg1")
				.type(MyEnums.class)
				.completion(ctx -> {
					CompletionProposal p1 = new CompletionProposal(MyEnums.E1.toString());
					CompletionProposal p2 = new CompletionProposal(MyEnums.E2.toString());
					CompletionProposal p3 = new CompletionProposal(MyEnums.E3.toString());
					return Arrays.asList(p1, p2, p3);
				})
				.and()
			.withTarget()
				.function(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					return String.format("You said '%s'", arg1);
				})
				.and()
			.build();
	}

	@ShellMethod(value = "complete sample4", key = "complete sample4")
	public String completeCommandSample4(@ShellOption(valueProvider = EnumValueProvider.class) MyEnums arg1) {
		return "You said " + arg1;
	}

	static enum MyEnums {
		E1, E2, E3
	}
}
