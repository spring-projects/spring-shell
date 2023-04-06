/*
 * Copyright 2022 the original author or authors.
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

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.command.execution.CommandExecution.CommandExecutionHandlerMethodArgumentResolvers;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.config.ShellConversionServiceSupplier;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterResolverAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(ParameterResolverAutoConfiguration.class));

	@Test
	void defaultCompletionResolverExists() {
		this.contextRunner.withUserConfiguration(CustomShellConversionServiceConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(CompletionResolver.class);
				});
	}

	@Test
	void defaultCommandExecutionHandlerMethodArgumentResolversExists() {
		this.contextRunner.withUserConfiguration(CustomShellConversionServiceConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(CommandExecutionHandlerMethodArgumentResolvers.class);
				});
	}

	@Configuration
	static class CustomShellConversionServiceConfiguration {

		@Bean
		ShellConversionServiceSupplier shellConversionServiceSupplier() {
			return () -> new DefaultConversionService();
		}
	}
}
