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

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Commands used for e2e test.
 *
 * @author Janne Valkealahti
 */
@ShellComponent
public class OptionTypeCommands extends BaseE2ECommands {

	@Bean
	public CommandRegistration testOptionTypeRegistration() {
		return CommandRegistration.builder()
			.command(REG, "option-type")
			.group(GROUP)
			.withOption()
				.longNames("arg1")
				.and()
			.withOption()
				.longNames("arg2")
				.type(String.class)
				.and()
			.withOption()
				.longNames("arg3")
				.type(int.class)
				.and()
			.withOption()
				.longNames("arg4")
				.label("MYLABEL")
				.and()
			.withTarget()
				.consumer(ctx -> {
					PrintWriter writer = ctx.getTerminal().writer();
					if (ctx.hasMappedOption("arg3")) {
						int v = ctx.getOptionValue("arg3");
						writer.append("arg3=" + Integer.toString(v) + "\n");
					}
					writer.flush();
				})
				.and()
			.build();
	}

	//
	// String
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-string", group = GROUP)
	public String optionTypeStringAnnotation(
		@ShellOption(help = "Desc arg1") String arg1
	) {
		return "Hello " + arg1;
	}

	@Bean
	public CommandRegistration optionTypeStringRegistration() {
		return CommandRegistration.builder()
			.command(REG, "option-type-string")
			.group(GROUP)
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
	}

	//
	// Boolean
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-boolean", group = GROUP)
	public String optionTypeBooleanAnnotation(
		@ShellOption() boolean arg1,
		@ShellOption(defaultValue = "true") boolean arg2,
		@ShellOption(defaultValue = "false") boolean arg3,
		@ShellOption() Boolean arg4,
		@ShellOption(defaultValue = "true") Boolean arg5,
		@ShellOption(defaultValue = "false") Boolean arg6
	) {
		return String.format("Hello arg1=%s arg2=%s arg3=%s arg4=%s arg5=%s arg6=%s", arg1, arg2, arg3, arg4, arg5,
				arg6);
	}

	@Bean
	public CommandRegistration optionTypeBooleanRegistration() {
		return CommandRegistration.builder()
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
			.withTarget()
				.function(ctx -> {
					boolean arg1 = ctx.hasMappedOption("arg1") ? ctx.getOptionValue("arg1") : false;
					boolean arg2 = ctx.getOptionValue("arg2");
					boolean arg3 = ctx.getOptionValue("arg3");
					Boolean arg4 = ctx.getOptionValue("arg4");
					Boolean arg5 = ctx.getOptionValue("arg5");
					Boolean arg6 = ctx.getOptionValue("arg6");
					return String.format("Hello arg1=%s arg2=%s arg3=%s arg4=%s arg5=%s arg6=%s", arg1, arg2, arg3,
							arg4, arg5, arg6);
				})
				.and()
			.build();
	}

	//
	// Integer
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-integer", group = GROUP)
	public String optionTypeIntegerAnnotation(
		@ShellOption int arg1,
		@ShellOption Integer arg2
	) {
		return String.format("Hello '%s' '%s'", arg1, arg2);
	}

	@Bean
	public CommandRegistration optionTypeIntegerRegistration() {
		return CommandRegistration.builder()
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

	//
	// Enum
	//

	public static enum OptionTypeEnum {
		ONE,TWO,THREE
	}

	@ShellMethod(key = LEGACY_ANNO + "option-type-enum", group = GROUP)
	public String optionTypeEnumAnnotation(
		@ShellOption(help = "Desc arg1") OptionTypeEnum arg1
	) {
		return "Hello " + arg1;
	}

	@Bean
	public CommandRegistration optionTypeEnumRegistration() {
		return CommandRegistration.builder()
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

	//
	// String[]
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-string-array", group = GROUP)
	public String optionTypeStringArrayAnnotation(
		@ShellOption(help = "Desc arg1") String[] arg1
	) {
		return "Hello " + stringOfStrings(arg1);
	}

	@Bean
	public CommandRegistration optionTypeStringArrayRegistration() {
		return CommandRegistration.builder()
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
	//
	// int[]
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-int-array", group = GROUP)
	public String optionTypeIntArrayAnnotation(
		@ShellOption(help = "Desc arg1") int[] arg1
	) {
		return "Hello " + stringOfInts(arg1);
	}

	@Bean
	public CommandRegistration optionTypeIntArrayRegistration() {
		return CommandRegistration.builder()
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

	//
	// List<String>
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-string-list", group = GROUP)
	public String optionTypeStringListAnnotation(
		@ShellOption(help = "Desc arg1") List<String> arg1
	) {
		return "Hello " + arg1;
	}

	@Bean
	public CommandRegistration optionTypeStringListRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
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

	//
	// Set<String>
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-string-set", group = GROUP)
	public String optionTypeStringSetAnnotation(
		@ShellOption(help = "Desc arg1") Set<String> arg1
	) {
		return "Hello " + arg1;
	}

	@Bean
	public CommandRegistration optionTypeStringSetRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
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

	//
	// Collection<String>
	//

	@ShellMethod(key = LEGACY_ANNO + "option-type-string-collection", group = GROUP)
	public String optionTypeStringCollectionAnnotation(
		@ShellOption(help = "Desc arg1") Collection<String> arg1
	) {
		return "Hello " + arg1;
	}

	@Bean
	public CommandRegistration optionTypeStringCollectionRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
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

	//
	// Void
	//

	@Bean
	public CommandRegistration optionTypeVoidRegistration() {
		return CommandRegistration.builder()
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
