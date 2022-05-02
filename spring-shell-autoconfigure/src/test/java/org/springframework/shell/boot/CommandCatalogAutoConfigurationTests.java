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
package org.springframework.shell.boot;

import java.util.Collections;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandCatalog.CommandResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandCatalogAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(CommandCatalogAutoConfiguration.class));

	@Test
	void defaultCommandCatalog() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(CommandCatalog.class));
	}

	@Test
	void testCommandResolvers() {
		this.contextRunner.withUserConfiguration(CustomCommandResolverConfiguration.class)
				.run((context) -> {
					CommandCatalog commandCatalog = context.getBean(CommandCatalog.class);
					assertThat(commandCatalog).extracting("resolvers").asInstanceOf(InstanceOfAssertFactories.LIST)
							.hasSize(1);
				});
	}

	@Test
	void customCommandCatalog() {
		this.contextRunner.withUserConfiguration(CustomCommandCatalogConfiguration.class)
				.run((context) -> {
					CommandCatalog commandCatalog = context.getBean(CommandCatalog.class);
					assertThat(commandCatalog).isSameAs(CustomCommandCatalogConfiguration.testCommandCatalog);
				});
	}

	@Test
	void registerCommandRegistration() {
		this.contextRunner.withUserConfiguration(CustomCommandRegistrationConfiguration.class)
				.run((context) -> {
					CommandCatalog commandCatalog = context.getBean(CommandCatalog.class);
					assertThat(commandCatalog.getRegistrations().get("customcommand")).isNotNull();
				});
	}

	@Configuration
	static class CustomCommandResolverConfiguration {

		@Bean
		CommandResolver customCommandResolver() {
			return () -> Collections.emptyMap();
		}
	}

	@Configuration
	static class CustomCommandCatalogConfiguration {

		static final CommandCatalog testCommandCatalog = CommandCatalog.of();

		@Bean
		CommandCatalog customCommandCatalog() {
			return testCommandCatalog;
		}
	}

	@Configuration
	static class CustomCommandRegistrationConfiguration {

		@Bean
		CommandRegistration commandRegistration() {
			return CommandRegistration.builder()
				.command("customcommand")
				.withTarget()
					.function(ctx -> {
						return null;
					})
					.and()
				.build();
		}
	}
}
