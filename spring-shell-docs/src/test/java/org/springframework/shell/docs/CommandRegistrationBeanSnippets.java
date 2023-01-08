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
package org.springframework.shell.docs;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.boot.CommandRegistrationCustomizer;
import org.springframework.shell.command.CommandRegistration;

public class CommandRegistrationBeanSnippets {

	class Dump1 {
		// tag::plain[]
		@Bean
		CommandRegistration commandRegistration() {
			return CommandRegistration.builder()
				.command("mycommand")
				.build();
		}
		// end::plain[]
	}

	class Dump2 {
		// tag::fromsupplier[]
		@Bean
		CommandRegistration commandRegistration(CommandRegistration.BuilderSupplier builder) {
			return builder.get()
				.command("mycommand")
				.build();
		}
		// end::fromsupplier[]
	}

	class Dump3 {
		// tag::customizer[]
		@Bean
		CommandRegistrationCustomizer commandRegistrationCustomizerExample() {
			return builder -> {
				// customize instance of CommandRegistration.Builder
			};
		}
		// end::customizer[]
	}
}
