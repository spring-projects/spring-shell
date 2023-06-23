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
package org.springframework.shell.docs;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.result.CommandNotFoundMessageProvider;

@SuppressWarnings("unused")
class CommandNotFoundSnippets {

	// tag::custom-provider[]
	class CustomProvider implements CommandNotFoundMessageProvider {

		@Override
		public String apply(ProviderContext context) {
			// parsed commands without options
			List<String> commands = context.commands();
			// actual error, usually CommandNotFound exception
			Throwable error = context.error();
			// access to registrations at this time
			Map<String, CommandRegistration> registrations = context.registrations();
			// raw text input from a user
			String text = context.text();
			return "My custom message";
		}
	}
	// end::custom-provider[]

	class Dump1 {

		// tag::provider-bean-1[]
		@Bean
		CommandNotFoundMessageProvider provider1() {
			return new CustomProvider();
		}
		// end::provider-bean-1[]

		// tag::provider-bean-2[]
		@Bean
		CommandNotFoundMessageProvider provider2() {
			return ctx -> "My custom message";
		}
		// end::provider-bean-2[]
	}

}
