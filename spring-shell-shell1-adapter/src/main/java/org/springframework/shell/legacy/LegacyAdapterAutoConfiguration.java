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
package org.springframework.shell.legacy;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.shell.converters.ArrayConverter;
import org.springframework.shell.converters.AvailableCommandsConverter;
import org.springframework.shell.converters.SimpleFileConverter;

/**
 * Main configuration class for the Shell 2 - Shell 1 adapter.
 *
 * @author Camilo Gonzalez
 */
@Configuration
@ComponentScan(basePackageClasses = {ArrayConverter.class}, excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		value = {AvailableCommandsConverter.class, SimpleFileConverter.class}))
public class LegacyAdapterAutoConfiguration {

	@Bean
	public LegacyMethodTargetRegistrar legacyMethodTargetResolver() {
		return new LegacyMethodTargetRegistrar();
	}

	@Bean
	public LegacyParameterResolver legacyParameterResolver() {
		return new LegacyParameterResolver();
	}

}
