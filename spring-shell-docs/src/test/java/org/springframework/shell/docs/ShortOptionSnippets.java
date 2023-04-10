/*
 * Copyright 2023 the original author or authors.
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

import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

public class ShortOptionSnippets {

	static class LegacyAnnotation {

		// tag::option-type-string-legacyannotation[]
		@ShellMethod(key = "example")
		String stringWithShortOption(
				@ShellOption(value = { "--arg", "-a" }) String arg)		{
			return String.format("Hi '%s'", arg);
		}
		// end::option-type-string-legacyannotation[]

		// tag::option-type-multiple-booleans-legacyannotation[]
		@ShellMethod(key = "example")
		public String multipleBooleans(
				@ShellOption(value = "-a") boolean a,
				@ShellOption(value = "-b") boolean b,
				@ShellOption(value = "-c") boolean c)
		{
			return String.format("Hi a='%s' b='%s' c='%s'", a, b, c);
		}
		// end::option-type-multiple-booleans-legacyannotation[]
	}

	// @Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	static class Annotation {

		// tag::option-type-string-annotation[]
		@Command(command = "example")
		String stringWithShortOption(
				@Option(longNames = "arg", shortNames = 'a', required = true) String arg) {
			return String.format("Hi '%s'", arg);
		}
		// end::option-type-string-annotation[]

		// tag::option-type-multiple-booleans-annotation[]
		@Command(command = "example")
		public String multipleBooleans(
				@Option(shortNames = 'a') boolean a,
				@Option(shortNames = 'b') boolean b,
				@Option(shortNames = 'c') boolean c) {
			return String.format("Hi a='%s' b='%s' c='%s'", a, b, c);
		}
		// end::option-type-multiple-booleans-annotation[]
	}

	static class Registration {

		// tag::option-type-string-programmatic[]
		CommandRegistration stringWithShortOption() {
			return CommandRegistration.builder()
				.command("example")
				.withTarget()
					.function(ctx -> {
						String arg = ctx.hasMappedOption("arg") ? ctx.getOptionValue("arg") : null;
						return String.format("Hi arg='%s'", arg);
					})
					.and()
				.withOption()
					.longNames("arg")
					.shortNames('a')
					.required()
					.and()
				.build();
		}
		// end::option-type-string-programmatic[]

		// tag::option-type-multiple-booleans-programmatic[]
		CommandRegistration multipleBooleans() {
			return CommandRegistration.builder()
				.command("example")
				.withTarget()
					.function(ctx -> {
						Boolean a = ctx.hasMappedOption("a") ? ctx.getOptionValue("a") : null;
						Boolean b = ctx.hasMappedOption("b") ? ctx.getOptionValue("b") : null;
						Boolean c = ctx.hasMappedOption("c") ? ctx.getOptionValue("c") : null;
						return String.format("Hi a='%s' b='%s' c='%s'", a, b, c);
					})
					.and()
				.withOption()
					.shortNames('a')
					.type(boolean.class)
					.defaultValue("false")
					.and()
				.withOption()
					.shortNames('b')
					.type(boolean.class)
					.defaultValue("false")
					.and()
				.withOption()
					.shortNames('c')
					.type(boolean.class)
					.defaultValue("false")
					.and()
				.build();
		}
		// end::option-type-multiple-booleans-programmatic[]
	}

}
