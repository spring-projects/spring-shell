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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Commands used for e2e test.
 *
 * @author Janne Valkealahti
 */
@ShellComponent
public class ArityCommands extends BaseE2ECommands {

	@ShellMethod(key = LEGACY_ANNO + "arity-boolean-default-true", group = GROUP)
	public String testArityBooleanDefaultTrueLegacyAnnotation(
		@ShellOption(value = "--overwrite", arity = 1, defaultValue = "true") Boolean overwrite
	) {
		return "Hello " + overwrite;
	}

	@Bean
	public CommandRegistration testArityBooleanDefaultTrueRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
			.command(REG, "arity-boolean-default-true")
			.group(GROUP)
			.withOption()
				.longNames("overwrite")
				.type(Boolean.class)
				.defaultValue("true")
				.arity(OptionArity.ZERO_OR_ONE)
				.and()
			.withTarget()
				.function(ctx -> {
					Boolean overwrite = ctx.getOptionValue("overwrite");
					return "Hello " + overwrite;
				})
				.and()
			.build();
	}

	@ShellMethod(key = LEGACY_ANNO + "arity-string-array", group = GROUP)
	public String testArityStringArrayLegacyAnnotation(
		@ShellOption(value = "--arg1", arity = 3) String[] arg1
	) {
		return "Hello " + Arrays.asList(arg1);
	}

	@Bean
	public CommandRegistration testArityStringArrayRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
			.command(REG, "arity-string-array")
			.group(GROUP)
			.withOption()
				.longNames("arg1")
				.required()
				.type(String[].class)
				.arity(0, 3)
				.position(0)
				.and()
			.withTarget()
				.function(ctx -> {
					String[] arg1 = ctx.getOptionValue("arg1");
					return "Hello " + Arrays.asList(arg1);
				})
				.and()
			.build();
	}

	@ShellMethod(key = LEGACY_ANNO + "arity-float-array", group = GROUP)
	public String testArityFloatArrayLegacyAnnotation(
		@ShellOption(value = "--arg1", arity = 3) float[] arg1
	) {
		return "Hello " + floatsToString(arg1);
	}

	@Bean
	public CommandRegistration testArityFloatArrayRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
			.command(REG, "arity-float-array")
			.group(GROUP)
			.withOption()
				.longNames("arg1")
				.type(float[].class)
				.arity(0, 3)
				.and()
			.withTarget()
				.function(ctx -> {
					float[] arg1 = ctx.getOptionValue("arg1");
					return "Hello " + floatsToString(arg1);
				})
				.and()
			.build();
	}

	@Bean
	public CommandRegistration testArityErrorsRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
			.command(REG, "arity-errors")
			.group(GROUP)
			.withOption()
				.longNames("arg1")
				.type(String[].class)
				.required()
				.arity(1, 2)
				.and()
			.withTarget()
				.function(ctx -> {
					String[] arg1 = ctx.getOptionValue("arg1");
					return "Hello " + Arrays.asList(arg1);
				})
				.and()
			.build();
	}

	private static String floatsToString(float[] arg1) {
		return IntStream.range(0, arg1.length)
			.mapToDouble(i -> arg1[i])
			.boxed()
			.map(d -> d.toString())
			.collect(Collectors.joining(","));
	}
}
