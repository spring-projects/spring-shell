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

public class OptionNamingCommands {

	@ShellComponent
	public static class OptionNamingCommandsLegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "option-naming-1", group = GROUP)
		public void testOptionNaming1Annotation(
			@ShellOption("from_snake") String snake,
			@ShellOption("fromCamel") String camel,
			@ShellOption("from-kebab") String kebab,
			@ShellOption("FromPascal") String pascal
		) {
		}

	}

	@Component
	public static class OptionNamingCommandsRegistration extends BaseE2ECommands {

		@Bean
		public CommandRegistration testOptionNaming1Registration(CommandRegistration.BuilderSupplier builder) {
			return builder.get()
				.command(REG, "option-naming-1")
				.group(GROUP)
				.withOption()
					.longNames("from_snake")
					.required()
					.and()
				.withOption()
					.longNames("fromCamel")
					.required()
					.and()
				.withOption()
					.longNames("from-kebab")
					.required()
					.and()
				.withOption()
					.longNames("FromPascal")
					.required()
					.and()
				.withOption()
					.longNames("arg1")
					.nameModifier(name -> "x" + name)
					.required()
					.and()
				.withTarget()
					.consumer(ctx -> {})
					.and()
				.build();
		}
	}
}
