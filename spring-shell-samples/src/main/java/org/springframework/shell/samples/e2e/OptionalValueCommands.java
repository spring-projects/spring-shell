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
public class OptionalValueCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "optional-value", group = GROUP)
		public String testOptionalValue(
			@ShellOption(defaultValue = ShellOption.NULL) String arg1
		) {
			return "Hello " + arg1;
		}
	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "optional-value")
		public String testOptionalValueAnnotation(
				@Option(longNames = "arg1")
				String arg1
		) {
				return "Hello " + arg1;
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration testOptionalValueRegistration() {
			return getBuilder()
				.command(REG, "optional-value")
				.group(GROUP)
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
	}
}
