/*
 * Copyright 2022-2023 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.shell.command.ArgumentHeaderMethodArgumentResolver;
import org.springframework.shell.command.CommandContextMethodArgumentResolver;
import org.springframework.shell.command.CommandExecution.CommandExecutionHandlerMethodArgumentResolvers;
import org.springframework.shell.command.annotation.support.OptionMethodArgumentResolver;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.completion.RegistrationOptionsCompletionResolver;
import org.springframework.shell.config.ShellConversionServiceSupplier;
import org.springframework.shell.standard.ShellOptionMethodArgumentResolver;

@AutoConfiguration
public class ParameterResolverAutoConfiguration {

	@Bean
	public CompletionResolver defaultCompletionResolver() {
		return new RegistrationOptionsCompletionResolver();
	}

	@Bean
	public CommandExecutionHandlerMethodArgumentResolvers commandExecutionHandlerMethodArgumentResolvers(
		ShellConversionServiceSupplier shellConversionServiceSupplier) {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
		resolvers.add(new ArgumentHeaderMethodArgumentResolver(shellConversionServiceSupplier.get(), null));
		resolvers.add(new HeadersMethodArgumentResolver());
		resolvers.add(new CommandContextMethodArgumentResolver());
		resolvers.add(new ShellOptionMethodArgumentResolver(shellConversionServiceSupplier.get(), null));
		resolvers.add(new OptionMethodArgumentResolver(shellConversionServiceSupplier.get(), null));
		return new CommandExecutionHandlerMethodArgumentResolvers(resolvers);
	}
}
