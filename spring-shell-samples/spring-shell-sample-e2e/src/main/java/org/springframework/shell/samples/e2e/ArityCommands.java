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

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.OptionArity;
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
public class ArityCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "arity-boolean-default-true", group = GROUP)
		public String testArityBooleanDefaultTrueLegacyAnnotation(
			@ShellOption(value = "--overwrite", arity = 1, defaultValue = "true") Boolean overwrite
		) {
			return "Hello " + overwrite;
		}

		@ShellMethod(key = LEGACY_ANNO + "arity-string-array", group = GROUP)
		public String testArityStringArrayLegacyAnnotation(
			@ShellOption(value = "--arg1", arity = 3) String[] arg1
		) {
			return "Hello " + Arrays.asList(arg1);
		}

		@ShellMethod(key = LEGACY_ANNO + "arity-float-array", group = GROUP)
		public String testArityFloatArrayLegacyAnnotation(
			@ShellOption(value = "--arg1", arity = 3) float[] arg1
		) {
			return "Hello " + stringOfFloats(arg1);
		}

		@ShellMethod(key = LEGACY_ANNO + "string-arityeone-required", group = GROUP)
		public String testStringArityeoneRequiredLegacyAnnotation(
			@ShellOption(value = "--arg1", arity = 1) String arg1
		) {
			return "Hello " + arg1;
		}

	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "arity-boolean-default-true")
		public String testArityBooleanDefaultTrueAnnotation(
				@Option(longNames = "overwrite", defaultValue = "true", arity = OptionArity.ZERO_OR_ONE)
				Boolean overwrite
		) {
				return "Hello " + overwrite;
		}

		@Command(command = "arity-string-array")
		public String testArityStringArrayAnnotation(
				@Option(longNames = "arg1", defaultValue = "true", arityMax = 3)
				String[] arg1
		) {
				return "Hello " + Arrays.asList(arg1);
		}

		@Command(command = "arity-float-array")
		public String testArityFloatArrayAnnotation(
				@Option(longNames = "arg1", defaultValue = "true", arity = OptionArity.ZERO_OR_MORE)
				float[] arg1
		) {
				return "Hello " + stringOfFloats(arg1);
		}

		@Command(command = "string-arityeone-required")
		public String testStringArityeoneRequiredAnnotation(
				@Option(longNames = {"arg1"}, arity = OptionArity.EXACTLY_ONE, required = true)
				String arg1
		) {
				return "Hello " + arg1;
		}

	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration testArityBooleanDefaultTrueRegistration() {
			return getBuilder()
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

		@Bean
		public CommandRegistration testArityStringArrayRegistration() {
			return getBuilder()
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

		@Bean
		public CommandRegistration testArityFloatArrayRegistration() {
			return getBuilder()
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
						return "Hello " + stringOfFloats(arg1);
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration testStringArityeoneRequiredRegistration() {
			return getBuilder()
				.command(REG, "string-arityeone-required")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(String.class)
					.required()
					.arity(OptionArity.EXACTLY_ONE)
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
		public CommandRegistration testArityErrorsRegistration() {
			return getBuilder()
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
	}
}
