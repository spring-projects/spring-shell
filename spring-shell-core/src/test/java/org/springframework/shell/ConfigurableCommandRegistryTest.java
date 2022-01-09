/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell;

import org.junit.jupiter.api.Test;

import org.springframework.shell.context.DefaultShellContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ConfigurableCommandRegistry}.
 *
 * @author Eric Bottard
 */
public class ConfigurableCommandRegistryTest {

	@Test
	public void testRegistration() {
		ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry(new DefaultShellContext());
		registry.register("foo", MethodTarget.of("toString", this, new Command.Help("some command")));
		assertThat(registry.listCommands()).containsKeys("foo");
	}

	@Test
	public void testDoubleRegistration() {
		ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry(new DefaultShellContext());
		registry.register("foo", MethodTarget.of("toString", this, new Command.Help("some command")));

		assertThatThrownBy(() -> {
			registry.register("foo", MethodTarget.of("hashCode", this, new Command.Help("some command")));
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("foo")
				.hasMessageContaining("toString")
				.hasMessageContaining("hashCode");
	}
}
