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
package org.springframework.shell.boot;

import java.util.Collections;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandResolver;
import org.springframework.shell.core.command.Command.Builder;
import org.springframework.shell.core.command.BuilderSupplier;
import org.springframework.shell.core.command.OptionNameModifier;

import static org.assertj.core.api.Assertions.assertThat;

class CommandRegistryAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(CommandRegistryAutoConfiguration.class,
				JLineShellAutoConfiguration.class, ShellContextAutoConfiguration.class));

	@Test
	void defaultCommandRegistry() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(CommandRegistry.class));
	}

	@Test
	void testCommandResolvers() {
		this.contextRunner.withUserConfiguration(CustomCommandResolverConfiguration.class).run((context) -> {
			CommandRegistry commandRegistry = context.getBean(CommandRegistry.class);
			assertThat(commandRegistry).extracting("resolvers").asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
		});
	}

	@Test
	void customCommandRegistry() {
		this.contextRunner.withUserConfiguration(CustomCommandRegistryConfiguration.class).run((context) -> {
			CommandRegistry commandRegistry = context.getBean(CommandRegistry.class);
			assertThat(commandRegistry).isSameAs(CustomCommandRegistryConfiguration.TEST_COMMAND_REGISTRY);
		});
	}

	@Test
	void registerCommandRegistration() {
		this.contextRunner.withUserConfiguration(CustomCommandRegistrationConfiguration.class).run(context -> {
			CommandRegistry commandRegistry = context.getBean(CommandRegistry.class);
			assertThat(commandRegistry.getRegistrations().get("customcommand")).isNotNull();
		});
	}

	@Test
	void builderSupplierIsCreated() {
		this.contextRunner.run(context -> {
			BuilderSupplier builderSupplier = context.getBean(BuilderSupplier.class);
			assertThat(builderSupplier).isNotNull();
		});
	}

	@Test
	void defaultOptionNameModifierIsNull() {
		this.contextRunner.run(context -> {
			BuilderSupplier builderSupplier = context.getBean(BuilderSupplier.class);
			Builder builder = builderSupplier.get();
			assertThat(builder).extracting("defaultOptionNameModifier").isNull();
		});
	}

	@Test
	void defaultOptionNameModifierIsSet() {
		this.contextRunner.withUserConfiguration(CustomOptionNameModifierConfiguration.class).run(context -> {
			BuilderSupplier builderSupplier = context.getBean(BuilderSupplier.class);
			Builder builder = builderSupplier.get();
			assertThat(builder).extracting("defaultOptionNameModifier").isNotNull();
		});
	}

	@Test
	void defaultOptionNameModifierIsSetFromProperties() {
		this.contextRunner.withPropertyValues("spring.shell.option.naming.case-type=kebab").run(context -> {
			BuilderSupplier builderSupplier = context.getBean(BuilderSupplier.class);
			Builder builder = builderSupplier.get();
			assertThat(builder).extracting("defaultOptionNameModifier").isNotNull();
		});
	}

	@Test
	void defaultOptionNameModifierNoopNotSetFromProperties() {
		this.contextRunner.withPropertyValues("spring.shell.option.naming.case-type=noop").run(context -> {
			BuilderSupplier builderSupplier = context.getBean(BuilderSupplier.class);
			Builder builder = builderSupplier.get();
			assertThat(builder).extracting("defaultOptionNameModifier").isNull();
			// there is customizer but it doesn't do anything
			assertThat(context).hasBean("defaultOptionNameModifierCommandRegistrationCustomizer");
		});
	}

	@Test
	void noCustomizerIfPropertyIsNotSet() {
		this.contextRunner.run(context -> {
			BuilderSupplier builderSupplier = context.getBean(BuilderSupplier.class);
			Builder builder = builderSupplier.get();
			assertThat(builder).extracting("defaultOptionNameModifier").isNull();
			// no customizer added without property
			assertThat(context).doesNotHaveBean("defaultOptionNameModifierCommandRegistrationCustomizer");
		});
	}

	// defaultOptionNameModifierCommandRegistrationCustomizer
	@Configuration
	static class CustomOptionNameModifierConfiguration {

		@Bean
		OptionNameModifier customOptionNameModifier() {
			return name -> name;
		}

	}

	@Configuration
	static class CustomCommandResolverConfiguration {

		@Bean
		CommandResolver customCommandResolver() {
			return Collections::emptyList;
		}

	}

	@Configuration
	static class CustomCommandRegistryConfiguration {

		static final CommandRegistry TEST_COMMAND_REGISTRY = CommandRegistry.of();

		@Bean
		CommandRegistry customCommandRegistry() {
			return TEST_COMMAND_REGISTRY;
		}

	}

	@Configuration
	static class CustomCommandRegistrationConfiguration {

		@Bean
		Command commandRegistration() {
			return Command.builder()
				.command("customcommand")
				.withTarget(targetSpec -> targetSpec.function(ctx -> null))
				.build();
		}

	}

}
