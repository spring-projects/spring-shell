/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell;

import java.util.Collection;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.result.IterableResultHandler;
import org.springframework.shell.result.ResultHandlerConfig;

/**
 * Creates supporting beans for running the Shell
 */
@Configuration
@Import(ResultHandlerConfig.class)
public class SpringShellAutoConfiguration {

	@Bean
	@Qualifier("spring-shell")
	public ConversionService shellConversionService(ApplicationContext applicationContext) {
		Collection<Converter> converters = applicationContext.getBeansOfType(Converter.class).values();
		Collection<GenericConverter> genericConverters = applicationContext.getBeansOfType(GenericConverter.class).values();
		Collection<ConverterFactory> converterFactories = applicationContext.getBeansOfType(ConverterFactory.class).values();

		DefaultConversionService defaultConversionService = new DefaultConversionService();
		for (Converter converter : converters) {
			defaultConversionService.addConverter(converter);
		}
		for (GenericConverter genericConverter : genericConverters) {
			defaultConversionService.addConverter(genericConverter);
		}
		for (ConverterFactory converterFactory : converterFactories) {
			defaultConversionService.addConverterFactory(converterFactory);
		}
		return defaultConversionService;
	}

	@Bean
	@ConditionalOnMissingBean(Validator.class)
	public Validator validator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Bean
	public Shell shell(@Qualifier("main") ResultHandler resultHandler, @Qualifier("iterableResultHandler") IterableResultHandler iterableResultHandler) {
		iterableResultHandler.setDelegate(resultHandler);
		return new Shell(resultHandler);
	}
}
