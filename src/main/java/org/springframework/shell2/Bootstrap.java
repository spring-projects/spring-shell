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
