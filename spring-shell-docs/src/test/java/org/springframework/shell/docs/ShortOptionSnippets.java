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

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.annotation.Option;

public class ShortOptionSnippets {

	// @Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	static class Annotation {

		// tag::option-type-string-annotation[]
		@org.springframework.shell.core.command.annotation.Command(command = "example")
		String stringWithShortOption(@Option(longNames = "arg", shortNames = 'a', required = true) String arg) {
			return String.format("Hi '%s'", arg);
		}
		// end::option-type-string-annotation[]

		// tag::option-type-multiple-booleans-annotation[]
		@org.springframework.shell.core.command.annotation.Command(command = "example")
		public String multipleBooleans(@Option(shortNames = 'a') boolean a, @Option(shortNames = 'b') boolean b,
				@Option(shortNames = 'c') boolean c) {
			return String.format("Hi a='%s' b='%s' c='%s'", a, b, c);
		}
		// end::option-type-multiple-booleans-annotation[]

	}

	static class Registration {

		// tag::option-type-string-programmatic[]
		Command stringWithShortOption() {
			return Command.builder().command("example").withTarget(targetSpec -> targetSpec.function(ctx -> {
				String arg = ctx.hasMappedOption("arg") ? ctx.getOptionValue("arg") : null;
				return String.format("Hi arg='%s'", arg);
			})).withOption(optionSpec -> optionSpec.longNames("arg").shortNames('a').required()).build();
		}
		// end::option-type-string-programmatic[]

		// tag::option-type-multiple-booleans-programmatic[]
		Command multipleBooleans() {
			return Command.builder().command("example").withTarget(targetSpec -> targetSpec.function(ctx -> {
				Boolean a = ctx.hasMappedOption("a") ? ctx.getOptionValue("a") : null;
				Boolean b = ctx.hasMappedOption("b") ? ctx.getOptionValue("b") : null;
				Boolean c = ctx.hasMappedOption("c") ? ctx.getOptionValue("c") : null;
				return String.format("Hi a='%s' b='%s' c='%s'", a, b, c);
			}))
				.withOption(optionSpec -> optionSpec.shortNames('a').type(boolean.class).defaultValue("false"))
				.withOption(optionSpec -> optionSpec.shortNames('b').type(boolean.class).defaultValue("false"))
				.withOption(optionSpec -> optionSpec.shortNames('c').type(boolean.class).defaultValue("false"))
				.build();
		}
		// end::option-type-multiple-booleans-programmatic[]

	}

}
