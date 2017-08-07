/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.standard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.ParameterResolver;

/**
 * Sets up all required beans for supporting the standard Shell API.
 *
 * @author Eric Bottard
 */
@Configuration
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
	public MethodTargetRegistrar standardMethodTargetResolver() {
		return new StandardMethodTargetRegistrar();
	}

	@Bean
	public ParameterResolver standardParameterResolver(ConversionService conversionService) {
		return new StandardParameterResolver(conversionService);
	}
}
