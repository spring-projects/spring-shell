/*
 * Copyright 2022-present the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.CommandRegistryCustomizer;
import org.springframework.shell.core.command.CommandResolver;

public class CommandRegistrySnippets {

	CommandRegistry catalog = CommandRegistry.of();

	void dump1() {
		// tag::snippet1[]
		Command registration = Command.builder().build();
		catalog.register(registration);
		// end::snippet1[]
	}

	// tag::snippet2[]
	static class CustomCommandResolver implements CommandResolver {

		List<Command> registrations = new ArrayList<>();

		CustomCommandResolver() {
			Command resolved = Command.builder().command("resolve command").build();
			registrations.add(resolved);
		}

		@Override
		public List<Command> resolve() {
			return registrations;
		}

	}
	// end::snippet2[]

	// tag::snippet3[]
	static class CustomCommandRegistryCustomizer implements CommandRegistryCustomizer {

		@Override
		public void customize(CommandRegistry commandRegistry) {
			Command registration = Command.builder().command("resolve command").build();
			commandRegistry.register(registration);
		}

	}
	// end::snippet3[]

}
