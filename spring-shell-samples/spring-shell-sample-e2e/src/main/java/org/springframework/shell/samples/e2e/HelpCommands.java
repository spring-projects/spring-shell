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
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

@ShellComponent
public class HelpCommands extends BaseE2ECommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "help-desc-1", group = GROUP)
		public void helpDesc1(
			@ShellOption(help = "arg1 desc", defaultValue = "hi") String arg1
		) {
		}
	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "help-desc-1")
		public void helpDesc1(
			@Option(longNames = "arg1", defaultValue = "hi", description = "arg1 desc") String arg1
		) {
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration helpDesc1() {
			return getBuilder()
				.command(REG, "help-desc-1")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.defaultValue("hi")
					.description("arg1 desc")
					.and()
				.withTarget()
					.consumer(ctx -> {})
					.and()
				.build();
		}
	}

}
