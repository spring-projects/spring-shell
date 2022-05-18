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
package org.springframework.shell.samples.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandResolver;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

public class ResolvedCommands {

	private static final String GROUP = "Resolve Commands";

	@Configuration
	public static class ResolvedCommandsConfiguration {

		@Bean
		Server1CommandResolver server1CommandResolver() {
			return new Server1CommandResolver();
		}

		@Bean
		Server2CommandResolver server2CommandResolver() {
			return new Server2CommandResolver();
		}
	}

	@ShellComponent
	public static class ResolvedCommandsCommands {

		private final Server1CommandResolver server1CommandResolver;
		private final Server2CommandResolver server2CommandResolver;

		ResolvedCommandsCommands(Server1CommandResolver server1CommandResolver,
				Server2CommandResolver server2CommandResolver) {
			this.server1CommandResolver = server1CommandResolver;
			this.server2CommandResolver = server2CommandResolver;
		}

		@ShellMethod(key = "resolve enableserver1", group = GROUP)
		public String server1Enable() {
			server1CommandResolver.enabled = true;
			return "Enabled server1";
		}

		@ShellMethod(key = "resolve disableserver1", group = GROUP)
		public String server1Disable() {
			server1CommandResolver.enabled = false;
			return "Disabled server1";
		}

		@ShellMethod(key = "resolve enableserver2", group = GROUP)
		public String server2Enable() {
			server2CommandResolver.enabled = true;
			return "Enabled server2";
		}

		@ShellMethod(key = "resolve disableserver2", group = GROUP)
		public String server2Disable() {
			server2CommandResolver.enabled = false;
			return "Disabled server2";
		}
	}

	static class Server1CommandResolver implements CommandResolver {

		private final List<CommandRegistration> registrations = new ArrayList<>();
		boolean enabled = false;

		Server1CommandResolver() {
			CommandRegistration resolved1 = CommandRegistration.builder()
				.command("resolve server1 command1")
				.group(GROUP)
				.description("server1 command1")
				.withTarget()
					.function(ctx -> {
						return "hi from server1 command1";
					})
					.and()
				.build();
			registrations.add(resolved1);
		}

		@Override
		public List<CommandRegistration> resolve() {
			return enabled ? registrations : Collections.emptyList();
		}
	}

	static class Server2CommandResolver implements CommandResolver {

		private final List<CommandRegistration> registrations = new ArrayList<>();
		boolean enabled = false;

		Server2CommandResolver() {
			CommandRegistration resolved1 = CommandRegistration.builder()
				.command("resolve server2 command1")
				.group(GROUP)
				.description("server2 command1")
				.withTarget()
					.function(ctx -> {
						return "hi from server2 command1";
					})
					.and()
				.build();
			CommandRegistration resolved2 = CommandRegistration.builder()
				.command("resolve server2 command2")
				.group(GROUP)
				.description("server2 command2")
				.withTarget()
					.function(ctx -> {
						return "hi from server2 command2";
					})
					.and()
				.build();
			registrations.add(resolved1);
			registrations.add(resolved2);
		}

		@Override
		public List<CommandRegistration> resolve() {
			return enabled ? registrations : Collections.emptyList();
		}
	}
}
