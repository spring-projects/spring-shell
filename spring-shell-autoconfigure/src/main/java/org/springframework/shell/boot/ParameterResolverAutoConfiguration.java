package org.springframework.shell.boot;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.standard.StandardParameterResolver;
import org.springframework.shell.standard.ValueProvider;

@Configuration(proxyBeanMethods = false)
public class ParameterResolverAutoConfiguration {

	@Bean
	public ParameterResolver standardParameterResolver(ConversionService conversionService,
			ObjectProvider<ValueProvider> valueProviders) {
		Set<ValueProvider> collect = valueProviders.orderedStream().collect(Collectors.toSet());
		return new StandardParameterResolver(conversionService, collect);
	}

}
