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

import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellOption;

class OptionTypesSnippets {

	class Dump1 {
		// tag::option-type-boolean-anno[]
		String example(
			@ShellOption() boolean arg1,
			@ShellOption(defaultValue = "true") boolean arg2,
			@ShellOption(defaultValue = "false") boolean arg3,
			@ShellOption() Boolean arg4,
			@ShellOption(defaultValue = "true") Boolean arg5,
			@ShellOption(defaultValue = "false") Boolean arg6
		) {
			return String.format("arg1=%s arg2=%s arg3=%s arg4=%s arg5=%s arg6=%s",
					arg1, arg2, arg3, arg4, arg5, arg6);
		}
		// end::option-type-boolean-anno[]
		void dump() {
		// tag::option-type-boolean-reg[]
		CommandRegistration.builder()
			.command("example")
			.withOption()
				.longNames("arg1").type(boolean.class).and()
			.withOption()
				.longNames("arg2").type(boolean.class).defaultValue("true").and()
			.withOption()
				.longNames("arg3").type(boolean.class).defaultValue("false").and()
			.withOption()
				.longNames("arg4").type(Boolean.class).and()
			.withOption()
				.longNames("arg5").type(Boolean.class).defaultValue("true").and()
			.withOption()
				.longNames("arg6").type(Boolean.class).defaultValue("false").and()
			.withTarget()
				.function(ctx -> {
					boolean arg1 = ctx.hasMappedOption("arg1")
							? ctx.getOptionValue("arg1")
							: false;
					boolean arg2 = ctx.getOptionValue("arg2");
					boolean arg3 = ctx.getOptionValue("arg3");
					Boolean arg4 = ctx.getOptionValue("arg4");
					Boolean arg5 = ctx.getOptionValue("arg5");
					Boolean arg6 = ctx.getOptionValue("arg6");
					return String.format("Hello arg1=%s arg2=%s arg3=%s arg4=%s arg5=%s arg6=%s",
							arg1, arg2, arg3, arg4, arg5, arg6);
				})
				.and()
			.build();
		// end::option-type-boolean-reg[]
		}
	}

	class Dump2 {
		// tag::option-type-integer-anno[]
		String example(@ShellOption(value = "arg1") int arg1) {
			return "Hello " + arg1;
		}
		// end::option-type-integer-anno[]
		void dump() {
		// tag::option-type-integer-reg[]
		CommandRegistration.builder()
			.command("example")
			.withOption()
				.longNames("arg1")
				.type(int.class)
				.required()
				.and()
			.withTarget()
				.function(ctx -> {
					boolean arg1 = ctx.getOptionValue("arg1");
					return "Hello " + arg1;
				})
				.and()
			.build();
		// end::option-type-integer-reg[]
		}
	}

	class Dump3 {
		// tag::option-type-string-anno[]
		String example(@ShellOption(value = "arg1") String arg1) {
			return "Hello " + arg1;
		}
		// end::option-type-string-anno[]
		void dump() {
		// tag::option-type-string-reg[]
		CommandRegistration.builder()
			.command("example")
			.withOption()
				.longNames("arg1")
				.type(String.class)
				.required()
				.and()
			.withTarget()
				.function(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					return "Hello " + arg1;
				})
				.and()
			.build();
		// end::option-type-string-reg[]
		}
	}

	class Dump4 {

		// tag::option-type-enum-class[]
		enum OptionTypeEnum {
			ONE,TWO,THREE
		}
		// end::option-type-enum-class[]

		// tag::option-type-enum-anno[]
		String example(@ShellOption(value = "arg1") OptionTypeEnum arg1) {
			return "Hello " + arg1;
		}
		// end::option-type-enum-anno[]
		void dump() {
		// tag::option-type-enum-reg[]
		CommandRegistration.builder()
			.command("example")
			.withOption()
				.longNames("arg1")
				.type(OptionTypeEnum.class)
				.required()
				.and()
			.withTarget()
				.function(ctx -> {
					OptionTypeEnum arg1 = ctx.getOptionValue("arg1");
					return "Hello " + arg1;
				})
				.and()
			.build();
		// end::option-type-enum-reg[]
		}
	}

	class Dump5 {
		// tag::option-type-string-array-anno[]
		String example(@ShellOption(value = "arg1") String[] arg1) {
			return "Hello " + arg1;
		}
		// end::option-type-string-array-anno[]
		void dump() {
		// tag::option-type-string-array-reg[]
		CommandRegistration.builder()
			.command("example")
			.withOption()
				.longNames("arg1")
				.type(String[].class)
				.required()
				.and()
			.withTarget()
				.function(ctx -> {
					String[] arg1 = ctx.getOptionValue("arg1");
					return "Hello " + arg1;
				})
				.and()
			.build();
		// end::option-type-string-array-reg[]
		}
	}
}
