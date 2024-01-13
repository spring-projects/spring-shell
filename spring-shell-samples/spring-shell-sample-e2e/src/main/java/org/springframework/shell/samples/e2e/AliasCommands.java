/*
 * Copyright 2023-2024 the original author or authors.
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
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

public class AliasCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = { LEGACY_ANNO + "alias-1", LEGACY_ANNO + "aliasfor-1" }, group = GROUP)
		public String testAlias1LegacyAnnotation() {
			return "Hello from alias command";
		}

		@ShellMethod(key = { LEGACY_ANNO + "alias-2", LEGACY_ANNO + "alias1for-2", LEGACY_ANNO + "alias2for-2" }, group = GROUP)
		public String testAlias2LegacyAnnotation() {
			return "Hello from alias command";
		}
	}

	@Command(command = BaseE2ECommands.ANNO, alias = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class AliasCommandsAnnotation extends BaseE2ECommands {

		@Command(command = "alias-1", alias = "aliasfor-1")
		public String testAlias1Annotation() {
			return "Hello from alias command";
		}

		@Command(command = "alias-2", alias = { "alias1for-2", "alias2for-2" })
		public String testAlias2Annotation() {
			return "Hello from alias command";
		}

		@Command(command = "alias-3 alias-31", alias = "alias-32 alias-33")
		public String testAlias3Annotation() {
			return "Hello from alias3 command";
		}
	}

	@Component
	public static class AliasCommandsRegistration extends BaseE2ECommands {

		@Bean
		public CommandRegistration testAlias1Registration(CommandRegistration.BuilderSupplier builder) {
			return builder.get()
				.command(REG, "alias-1")
				.group(GROUP)
				.withAlias()
					.command(REG, "aliasfor-1")
					.and()
				.withTarget()
					.function(ctx -> {
						return "Hello from alias command";
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration testAlias2Registration(CommandRegistration.BuilderSupplier builder) {
			return builder.get()
				.command(REG, "alias-2")
				.group(GROUP)
				.withAlias()
					.command(REG, "alias1for-2")
					.and()
				.withAlias()
					.command(REG, "alias2for-2")
					.and()
				.withTarget()
					.function(ctx -> {
						return "Hello from alias command";
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration testAlias3Registration(CommandRegistration.BuilderSupplier builder) {
			return builder.get()
				.command(REG, "alias-3 alias-31")
				.group(GROUP)
				.withAlias()
					.command(REG, "alias-32 alias-33")
					.and()
				.withTarget()
					.function(ctx -> {
						return "Hello from alias3 command";
					})
					.and()
				.build();
		}
	}

}
