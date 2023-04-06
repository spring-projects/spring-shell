/*
 * Copyright 2021-2023 the original author or authors.
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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.boot.SpringShellProperties.Help;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandCatalogCustomizer;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.BuilderSupplier;
import org.springframework.shell.command.CommandRegistration.OptionNameModifier;
import org.springframework.shell.command.support.OptionNameModifierSupport;
import org.springframework.shell.command.CommandResolver;
import org.springframework.shell.context.ShellContext;

@AutoConfiguration
@EnableConfigurationProperties(SpringShellProperties.class)
public class CommandCatalogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(CommandCatalog.class)
	public CommandCatalog commandCatalog(ObjectProvider<MethodTargetRegistrar> methodTargetRegistrars,
			ObjectProvider<CommandResolver> commandResolvers,
			ObjectProvider<CommandCatalogCustomizer> commandCatalogCustomizers,
			ShellContext shellContext) {
		List<CommandResolver> resolvers = commandResolvers.orderedStream().collect(Collectors.toList());
		CommandCatalog catalog = CommandCatalog.of(resolvers, shellContext);
		methodTargetRegistrars.orderedStream().forEach(resolver -> {
			resolver.register(catalog);
		});
		commandCatalogCustomizers.orderedStream().forEach(customizer -> {
			customizer.customize(catalog);
		});
		return catalog;
	}

	@Bean
	public CommandCatalogCustomizer defaultCommandCatalogCustomizer(ObjectProvider<CommandRegistration> commandRegistrations) {
		return catalog -> {
			commandRegistrations.orderedStream().forEach(registration -> {
				catalog.register(registration);
			});
		};
	}

	@Bean
	public CommandRegistrationCustomizer helpOptionsCommandRegistrationCustomizer(SpringShellProperties properties) {
		return registration -> {
			Help help = properties.getHelp();
			if (help.isEnabled()) {
				registration.withHelpOptions()
					.enabled(true)
					.longNames(help.getLongNames())
					.shortNames(help.getShortNames())
					.command(help.getCommand());
			}
		};
	}

	@Bean
	@ConditionalOnBean(OptionNameModifier.class)
	public CommandRegistrationCustomizer customOptionNameModifierCommandRegistrationCustomizer(OptionNameModifier modifier) {
		return builder -> {
			builder.defaultOptionNameModifier(modifier);
		};
	}

	@Bean
	@ConditionalOnMissingBean(OptionNameModifier.class)
	@ConditionalOnProperty(prefix = "spring.shell.option.naming", name = "case-type")
	public CommandRegistrationCustomizer defaultOptionNameModifierCommandRegistrationCustomizer(SpringShellProperties properties) {
		return builder -> {
			switch (properties.getOption().getNaming().getCaseType()) {
				case NOOP:
					break;
				case CAMEL:
					builder.defaultOptionNameModifier(OptionNameModifierSupport.CAMELCASE);
					break;
				case SNAKE:
					builder.defaultOptionNameModifier(OptionNameModifierSupport.SNAKECASE);
					break;
				case KEBAB:
					builder.defaultOptionNameModifier(OptionNameModifierSupport.KEBABCASE);
					break;
				case PASCAL:
					builder.defaultOptionNameModifier(OptionNameModifierSupport.PASCALCASE);
					break;
				default:
					break;
			}
		};
	}

	@Bean
	@ConditionalOnMissingBean
	public BuilderSupplier commandRegistrationBuilderSupplier(
			ObjectProvider<CommandRegistrationCustomizer> customizerProvider) {
		return () -> {
			CommandRegistration.Builder builder = CommandRegistration.builder();
			customizerProvider.orderedStream().forEach((customizer) -> customizer.customize(builder));
			return builder;
		};
	}
}
