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

	@ShellMethod(key = LEGACY_ANNO + "boolean-arity1-default-true", group = GROUP)
	public String testBooleanArity1DefaultTrue(
		@ShellOption(value = "--overwrite", arity = 1, defaultValue = "true") Boolean overwrite
	) {
		return "Hello " + overwrite;
	}

	@Bean
	public CommandRegistration testBooleanArity1DefaultTrueRegistration(CommandRegistration.BuilderSupplier builder) {
		return builder.get()
			.command(REG, "boolean-arity1-default-true")
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
}
