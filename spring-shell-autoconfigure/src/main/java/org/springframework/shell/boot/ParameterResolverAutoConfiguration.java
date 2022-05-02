package org.springframework.shell.boot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.shell.command.ArgumentHeaderMethodArgumentResolver;
import org.springframework.shell.command.CommandContextMethodArgumentResolver;
import org.springframework.shell.command.CommandExecution.CommandExecutionHandlerMethodArgumentResolvers;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.completion.DefaultCompletionResolver;
import org.springframework.shell.standard.ShellOptionMethodArgumentResolver;

@Configuration(proxyBeanMethods = false)
public class ParameterResolverAutoConfiguration {

	@Bean
	public CompletionResolver defaultCompletionResolver() {
		return new DefaultCompletionResolver();
	}

	@Bean
	public CommandExecutionHandlerMethodArgumentResolvers commandExecutionHandlerMethodArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
		resolvers.add(new ArgumentHeaderMethodArgumentResolver(new DefaultConversionService(), null));
		resolvers.add(new HeadersMethodArgumentResolver());
		resolvers.add(new CommandContextMethodArgumentResolver());
		resolvers.add(new ShellOptionMethodArgumentResolver(new DefaultConversionService(), null));
		return new CommandExecutionHandlerMethodArgumentResolvers(resolvers);
	}
}
