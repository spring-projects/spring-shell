/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.shell2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.converters.ArrayConverter;
import org.springframework.shell.converters.AvailableCommandsConverter;
import org.springframework.shell.converters.SimpleFileConverter;

/**
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = {ArrayConverter.class, Bootstrap.class}, excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		value = {AvailableCommandsConverter.class, SimpleFileConverter.class}))
public class Bootstrap {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Bootstrap.class);
		context.getBean(JLineShell.class).run();
	}

	@Bean
	public ConversionService conversionService() {
		return new DefaultConversionService();
	}

	@Bean
	public ParameterResolver parameterResolver(ConversionService conversionService) {
		return new DefaultParameterResolver(conversionService);
	}

}
