/*
 * Copyright 2017-2023 the original author or authors.
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

import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.CommandValueProvider;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.FileValueProvider;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;
import org.springframework.shell.standard.ValueProvider;

/**
 * Sets up all required beans for supporting the standard Shell API.
 *
 * @author Eric Bottard
 */
@AutoConfiguration
public class StandardAPIAutoConfiguration {

	@Bean
	public ValueProvider commandValueProvider(CommandCatalog commandRegistry) {
		return new CommandValueProvider(commandRegistry);
	}

	@Bean
	public ValueProvider enumValueProvider() {
		return new EnumValueProvider();
	}

	@Bean
	public ValueProvider fileValueProvider() {
		return new FileValueProvider();
	}

	@Bean
	public MethodTargetRegistrar standardMethodTargetResolver(ApplicationContext applicationContext,
			CommandRegistration.BuilderSupplier builder) {
		return new StandardMethodTargetRegistrar(applicationContext, builder);
	}

	@Bean
	public static LazyInitializationExcludeFilter valueProviderLazyInitializationExcludeFilter(){
		return LazyInitializationExcludeFilter.forBeanTypes(ValueProvider.class);
	}
}
