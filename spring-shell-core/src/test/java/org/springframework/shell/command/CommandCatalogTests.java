/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandCatalogTests extends AbstractCommandTests {

	@Test
	public void testCommandCatalog () {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("group1 sub1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		CommandCatalog catalog = CommandCatalog.of();
		catalog.register(r1);
		assertThat(catalog.getRegistrations()).hasSize(1);
		catalog.unregister(r1);
		assertThat(catalog.getRegistrations()).hasSize(0);
	}

	@Test
	public void testCommandAliases () {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("group1 sub1")
			.withAlias()
				.command("group1 sub2")
				.and()
			.withTarget()
				.function(function1)
				.and()
			.build();
		CommandCatalog catalog = CommandCatalog.of();
		catalog.register(r1);
		assertThat(catalog.getRegistrations()).hasSize(2);
	}

	@Test
	public void testResolver() {
		// catalog itself would not have any registered command but
		// this custom resolver adds one which may dymanically go away.
		DynamicCommandResolver resolver = new DynamicCommandResolver();
		CommandCatalog catalog = CommandCatalog.of(Arrays.asList(resolver), null);
		assertThat(catalog.getRegistrations()).hasSize(1);
		resolver.enabled = false;
		assertThat(catalog.getRegistrations()).hasSize(0);
	}

	class DynamicCommandResolver implements CommandResolver {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("group1 sub1")
			.withTarget()
				.function(function1)
				.and()
			.build();
		boolean enabled = true;

		@Override
		public List<CommandRegistration> resolve() {
			List<CommandRegistration> regs = new ArrayList<>();
			if (enabled) {
				regs.add(r1);
			}
			return regs;
		}
	}
}
