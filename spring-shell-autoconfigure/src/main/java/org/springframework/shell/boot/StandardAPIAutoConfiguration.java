/*
 * Copyright 2017-present the original author or authors.
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
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.CommandValueProvider;
import org.springframework.shell.core.EnumValueProvider;
import org.springframework.shell.core.FileValueProvider;
import org.springframework.shell.core.ValueProvider;

/**
 * Sets up all required beans for supporting the standard Shell API.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine
 */
@AutoConfiguration
public class StandardAPIAutoConfiguration {

	@Bean
	public ValueProvider commandValueProvider(CommandRegistry commandRegistry) {
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
	public static LazyInitializationExcludeFilter valueProviderLazyInitializationExcludeFilter(){
		return LazyInitializationExcludeFilter.forBeanTypes(ValueProvider.class);
	}
}
