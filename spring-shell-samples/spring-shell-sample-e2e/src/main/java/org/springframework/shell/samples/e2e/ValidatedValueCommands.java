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

import jakarta.validation.constraints.Min;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

/**
 * Commands used for e2e test.
 *
 * @author Janne Valkealahti
 */
public class ValidatedValueCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "validated-value", group = GROUP)
		public String testValidatedValueAnnotation(
			@ShellOption @Min(value = 1) Integer arg1,
			@ShellOption @Min(value = 1) Integer arg2
		) {
			return "Hello " + arg1;
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration testValidatedValueRegistration() {
			return getBuilder()
				.command(REG, "validated-value")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(Integer.class)
					.required()
					.and()
				.withOption()
					.longNames("arg2")
					.type(Integer.class)
					.required()
					.and()
				.withTarget()
					.function(ctx -> {
						Integer arg1 = ctx.getOptionValue("arg1");
						return "Hello " + arg1;
					})
					.and()
				.build();
		}
	}
}
