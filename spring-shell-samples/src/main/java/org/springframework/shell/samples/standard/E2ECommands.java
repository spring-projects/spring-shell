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
public class E2ECommands {

	@ShellMethod(key = "e2e anno optional-value")
	public String testOptionalValue(
		@ShellOption(defaultValue = ShellOption.NULL) String arg1
	) {
		return "Hello " + arg1;
	}

	@Bean
	public CommandRegistration testOptionalValueRegistration() {
		return CommandRegistration.builder()
			.command("e2e", "reg", "optional-value")
			.group("E2E Commands")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.function(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					return "Hello " + arg1;
				})
				.and()
			.build();
	}

	@ShellMethod(key = "e2e anno default-value")
	public String testDefaultValue(
		@ShellOption(defaultValue = "hi") String arg1
	) {
		return "Hello " + arg1;
	}

	@Bean
	public CommandRegistration testDefaultValueRegistration() {
		return CommandRegistration.builder()
			.command("e2e", "reg", "default-value")
			.group("E2E Commands")
			.withOption()
				.longNames("arg1")
				.defaultValue("hi")
				.and()
			.withTarget()
				.function(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					return "Hello " + arg1;
				})
				.and()
			.build();
	}

	@ShellMethod(key = "e2e anno boolean-arity1-default-true")
	public String testBooleanArity1DefaultTrue(
		@ShellOption(value = "--overwrite", arity = 1, defaultValue = "true") Boolean overwrite
	) {
		return "Hello " + overwrite;
	}

	@Bean
	public CommandRegistration testBooleanArity1DefaultTrueRegistration() {
		return CommandRegistration.builder()
			.command("e2e", "reg", "boolean-arity1-default-true")
			.group("E2E Commands")
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
	public CommandRegistration testExitCodeRegistration() {
		return CommandRegistration.builder()
			.command("e2e", "reg", "exit-code")
			.group("E2E Commands")
			.withOption()
				.longNames("arg1")
				.required()
				.and()
			.withTarget()
				.consumer(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					throw new MyException(arg1);
				})
				.and()
			.withExitCode()
				.map(MyException.class, 3)
				.map(t -> {
					String msg = t.getMessage();
					if (msg != null && msg.contains("ok")) {
						return 0;
					}
					else if (msg != null && msg.contains("fun")) {
						return 4;
					}
					return 0;
				})
				.and()
			.build();
	}

	static class MyException extends RuntimeException {

		MyException(String msg) {
			super(msg);
		}
	}
}
