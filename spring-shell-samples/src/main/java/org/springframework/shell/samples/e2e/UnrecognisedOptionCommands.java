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
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

public class UnrecognisedOptionCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "unrecognised-option-noother", group = GROUP)
		public String testUnrecognisedOptionNoOtherAnnotation(
		) {
			return "Hi";
		}

		@ShellMethod(key = LEGACY_ANNO + "unrecognised-option-withrequired", group = GROUP)
		public String testUnrecognisedOptionWithRequiredAnnotation(
			@ShellOption(help = "Desc arg1") String arg1
		) {
			return "Hello " + arg1;
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration testUnrecognisedOptionNoOtherRegistration() {
			return getBuilder()
				.command(REG, "unrecognised-option-noother")
				.group(GROUP)
				.withTarget()
					.function(ctx -> {
						return "Hi";
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration testUnrecognisedOptionWithRequiredRegistration() {
			return getBuilder()
				.command(REG, "unrecognised-option-withrequired")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.description("Desc arg1")
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
	}
}
