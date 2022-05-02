/*
 * Copyright 2021-2022 the original author or authors.
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandCatalog.CommandCatalogCustomizer;
import org.springframework.shell.command.CommandCatalog.CommandResolver;

@Configuration(proxyBeanMethods = false)
public class CommandCatalogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(CommandCatalog.class)
	public CommandCatalog commandCatalog(ObjectProvider<MethodTargetRegistrar> methodTargetRegistrars,
			ObjectProvider<CommandResolver> commandResolvers,
			ObjectProvider<CommandCatalogCustomizer> commandCatalogCustomizers) {
		List<CommandResolver> resolvers = commandResolvers.orderedStream().collect(Collectors.toList());
		CommandCatalog catalog = CommandCatalog.of(resolvers, null);
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
}
