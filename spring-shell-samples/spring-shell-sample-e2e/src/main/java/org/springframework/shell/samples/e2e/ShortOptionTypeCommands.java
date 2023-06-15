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
package org.springframework.shell.samples.e2e;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

public class ShortOptionTypeCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "short-option-type-string", group = GROUP)
		public String shortOptionTypeStringLegacyAnnotation(
				@ShellOption(value = { "--arg", "-a" }) String arg)		{
			return String.format("Hi '%s'", arg);
		}

		@ShellMethod(key = LEGACY_ANNO + "short-option-type-single-boolean", group = GROUP)
		public String shortOptionTypeSingleBooleanLegacyAnnotation(
				@ShellOption(value = "-a") boolean a)
		{
			return String.format("Hi '%s'", a);
		}

		@ShellMethod(key = LEGACY_ANNO + "short-option-type-multi-boolean", group = GROUP)
		public String shortOptionTypeMultiBooleanLegacyAnnotation(
				@ShellOption(value = "-a") boolean a,
				@ShellOption(value = "-b") boolean b,
				@ShellOption(value = "-c") boolean c)
		{
			return String.format("Hi a='%s' b='%s' c='%s'", a, b, c);
		}
	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "short-option-type-string")
		public String shortOptionTypeStringAnnotation(
				@Option(longNames = "arg", shortNames = 'a', required = true) String arg) {
			return String.format("Hi '%s'", arg);
		}

		@Command(command = "short-option-type-single-boolean")
		public String shortOptionTypeSingleBooleanAnnotation(
				@Option(shortNames = 'a') boolean a) {
			return String.format("Hi '%s'", a);
		}

		@Command(command = "short-option-type-multi-boolean")
		public String shortOptionTypeMultiBooleanAnnotation(
				@Option(shortNames = 'a') boolean a,
				@Option(shortNames = 'b') boolean b,
				@Option(shortNames = 'c') boolean c) {
			return String.format("Hi a='%s' b='%s' c='%s'", a, b, c);
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration shortOptionTypeStringRegistration() {
			return getBuilder()
				.command(REG, "short-option-type-string")
				.group(GROUP)
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

		@Bean
		public CommandRegistration shortOptionTypeSingleBooleanRegistration() {
			return getBuilder()
				.command(REG, "short-option-type-single-boolean")
				.group(GROUP)
				.withTarget()
					.function(ctx -> {
						Boolean a = ctx.hasMappedOption("a") ? ctx.getOptionValue("a") : null;
						return String.format("Hi a='%s'", a);
					})
					.and()
				.withOption()
					.shortNames('a')
					.type(boolean.class)
					.defaultValue("false")
					.and()
				.build();
		}

		@Bean
		public CommandRegistration shortOptionTypeMultiBooleanRegistration() {
			return getBuilder()
				.command(REG, "short-option-type-multi-boolean")
				.group(GROUP)
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
	}
}
