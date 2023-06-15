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
import org.springframework.stereotype.Component;

public class AliasCommands {

	@Command(command = BaseE2ECommands.ANNO, alias = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class AliasCommandsAnnotation extends BaseE2ECommands {

		@Command(command = "alias-1", alias = "aliasfor-1")
		public String testAlias1Annotation() {
			return "Hello from alias command";
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
	}

}
