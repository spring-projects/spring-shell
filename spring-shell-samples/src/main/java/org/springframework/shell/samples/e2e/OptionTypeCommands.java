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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

/**
 * Commands used for e2e test.
 *
 * @author Janne Valkealahti
 */
public class OptionTypeCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "option-type-string", group = GROUP)
		public String optionTypeStringAnnotation(
			@ShellOption(help = "Desc arg1") String arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-boolean", group = GROUP)
		public String optionTypeBooleanAnnotation(
			@ShellOption() boolean arg1,
			@ShellOption(defaultValue = "true") boolean arg2,
			@ShellOption(defaultValue = "false") boolean arg3,
			@ShellOption() Boolean arg4,
			@ShellOption(defaultValue = "true") Boolean arg5,
			@ShellOption(defaultValue = "false") Boolean arg6,
			boolean arg7
		) {
			return String.format("Hello arg1=%s arg2=%s arg3=%s arg4=%s arg5=%s arg6=%s arg7=%s", arg1, arg2, arg3,
					arg4, arg5, arg6, arg7);
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-integer", group = GROUP)
		public String optionTypeIntegerAnnotation(
			@ShellOption int arg1,
			@ShellOption Integer arg2
		) {
			return String.format("Hello '%s' '%s'", arg1, arg2);
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-enum", group = GROUP)
		public String optionTypeEnumAnnotation(
			@ShellOption(help = "Desc arg1") OptionTypeEnum arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-string-array", group = GROUP)
		public String optionTypeStringArrayAnnotation(
			@ShellOption(help = "Desc arg1") String[] arg1
		) {
			return "Hello " + stringOfStrings(arg1);
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-int-array", group = GROUP)
		public String optionTypeIntArrayAnnotation(
			@ShellOption(help = "Desc arg1") int[] arg1
		) {
			return "Hello " + stringOfInts(arg1);
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-string-list", group = GROUP)
		public String optionTypeStringListAnnotation(
			@ShellOption(help = "Desc arg1") List<String> arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-string-set", group = GROUP)
		public String optionTypeStringSetAnnotation(
			@ShellOption(help = "Desc arg1") Set<String> arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-type-string-collection", group = GROUP)
		public String optionTypeStringCollectionAnnotation(
			@ShellOption(help = "Desc arg1") Collection<String> arg1
		) {
			return "Hello " + arg1;
		}
	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "option-type-string")
		public String optionTypeStringAnnotation(
			@Option(longNames = "arg1")
			String arg1
		) {
			return "Hello " + arg1;
		}

		@Command(command = "option-type-boolean")
		public String optionTypeBooleanAnnotation(
			@Option(longNames = "arg1") boolean arg1,
			@Option(longNames = "arg2", defaultValue = "true") boolean arg2,
			@Option(longNames = "arg3", defaultValue = "false") boolean arg3,
			@Option(longNames = "arg4") Boolean arg4,
			@Option(longNames = "arg5", defaultValue = "true") Boolean arg5,
			@Option(longNames = "arg6", defaultValue = "false") Boolean arg6,
			boolean arg7
		) {
			return String.format("Hello arg1=%s arg2=%s arg3=%s arg4=%s arg5=%s arg6=%s arg7=%s", arg1, arg2, arg3,
					arg4, arg5, arg6, arg7);
		}

		@Command(command = "option-type-integer")
		public String optionTypeIntegerAnnotation(
			@Option(longNames = "arg1")
			int arg1,
			@Option(longNames = "arg2")
			Integer arg2
		) {
			return String.format("Hello '%s' '%s'", arg1, arg2);
		}

		@Command(command = "option-type-enum")
		public String optionTypeEnumAnnotation(
			@Option(longNames = "arg1")
			OptionTypeEnum arg1
		) {
			return "Hello " + arg1;
		}

		@Command(command = "option-type-string-array")
		public String optionTypeStringArrayAnnotation(
			@Option(longNames = "arg1")
			String[] arg1
		) {
			return "Hello " + stringOfStrings(arg1);
		}

		@Command(command = "option-type-int-array")
		public String optionTypeIntArrayAnnotation(
			@Option(longNames = "arg1")
			int[] arg1
		) {
			return "Hello " + stringOfInts(arg1);
		}

		@Command(command = "option-type-string-list")
		public String optionTypeStringListAnnotation(
			@Option(longNames = "arg1")
			List<String> arg1
		) {
			return "Hello " + arg1;
		}

		@Command(command = "option-type-string-set")
		public String optionTypeStringSetAnnotation(
			@Option(longNames = "arg1")
			Set<String> arg1
		) {
			return "Hello " + arg1;
		}

		@Command(command = "option-type-string-collection")
		public String optionTypeStringCollectionAnnotation(
			@Option(longNames = "arg1")
			Collection<String> arg1
		) {
			return "Hello " + arg1;
		}

	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration optionTypeStringRegistration() {
			return getBuilder()
				.command(REG, "option-type-string")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(String.class)
					.position(0)
					.required()
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
		public CommandRegistration optionTypeBooleanRegistration() {
			return getBuilder()
				.command(REG, "option-type-boolean")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(boolean.class)
					.and()
				.withOption()
					.longNames("arg2")
					.type(boolean.class)
					.defaultValue("true")
					.and()
				.withOption()
					.longNames("arg3")
					.type(boolean.class)
					.defaultValue("false")
					.and()
				.withOption()
					.longNames("arg4")
					.type(Boolean.class)
					.and()
				.withOption()
					.longNames("arg5")
					.type(Boolean.class)
					.defaultValue("true")
					.and()
				.withOption()
					.longNames("arg6")
					.type(Boolean.class)
					.defaultValue("false")
					.and()
				.withOption()
					.longNames("arg7")
					.type(boolean.class)
					.and()
				.withTarget()
					.function(ctx -> {
						boolean arg1 = ctx.hasMappedOption("arg1") ? ctx.getOptionValue("arg1") : false;
						boolean arg2 = ctx.getOptionValue("arg2");
						boolean arg3 = ctx.getOptionValue("arg3");
						Boolean arg4 = ctx.getOptionValue("arg4");
						Boolean arg5 = ctx.getOptionValue("arg5");
						Boolean arg6 = ctx.getOptionValue("arg6");
						boolean arg7 = ctx.hasMappedOption("arg7") ? ctx.getOptionValue("arg7") : false;
						return String.format("Hello arg1=%s arg2=%s arg3=%s arg4=%s arg5=%s arg6=%s arg7=%s", arg1,
								arg2, arg3, arg4, arg5, arg6, arg7);
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionTypeIntegerRegistration() {
			return getBuilder()
				.command(REG, "option-type-integer")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(int.class)
					.required()
					.and()
				.withOption()
					.longNames("arg2")
					.type(Integer.class)
					.required()
					.and()
				.withTarget()
					.function(ctx -> {
						int arg1 = ctx.getOptionValue("arg1");
						Integer arg2 = ctx.getOptionValue("arg2");
						return String.format("Hello '%s' '%s'", arg1, arg2);
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionTypeEnumRegistration() {
			return getBuilder()
				.command(REG, "option-type-enum")
				.group(GROUP)
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
		}

		@Bean
		public CommandRegistration optionTypeStringArrayRegistration() {
			return getBuilder()
				.command(REG, "option-type-string-array")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(String[].class)
					.required()
					.and()
				.withTarget()
					.function(ctx -> {
						String[] arg1 = ctx.getOptionValue("arg1");
						return "Hello " + stringOfStrings(arg1);
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionTypeIntArrayRegistration() {
			return getBuilder()
				.command(REG, "option-type-int-array")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(int[].class)
					.required()
					.and()
				.withTarget()
					.function(ctx -> {
						int[] arg1 = ctx.getOptionValue("arg1");
						return "Hello " + stringOfInts(arg1);
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionTypeStringListRegistration() {
			return getBuilder()
				.command(REG, "option-type-string-list")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(List.class)
					.required()
					.and()
				.withTarget()
					.function(ctx -> {
						List<String> arg1 = ctx.getOptionValue("arg1");
						return "Hello " + arg1;
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionTypeStringSetRegistration() {
			return getBuilder()
				.command(REG, "option-type-string-set")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(Set.class)
					.required()
					.and()
				.withTarget()
					.function(ctx -> {
						Set<String> arg1 = ctx.getOptionValue("arg1");
						return "Hello " + arg1;
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionTypeStringCollectionRegistration() {
			return getBuilder()
				.command(REG, "option-type-string-collection")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(Collection.class)
					.required()
					.and()
				.withTarget()
					.function(ctx -> {
						Collection<String> arg1 = ctx.getOptionValue("arg1");
						return "Hello " + arg1;
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionTypeVoidRegistration() {
			return getBuilder()
				.command(REG, "option-type-void")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(void.class)
					.and()
				.withTarget()
					.function(ctx -> {
						return "Hello ";
					})
					.and()
				.build();
		}
	}

	public static enum OptionTypeEnum {
		ONE,TWO,THREE
	}
}
