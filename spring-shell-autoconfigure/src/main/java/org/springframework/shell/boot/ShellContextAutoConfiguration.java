package org.springframework.shell.boot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.context.DefaultShellContext;
import org.springframework.shell.context.ShellContext;

@Configuration(proxyBeanMethods = false)
public class ShellContextAutoConfiguration {

	@Bean
	public ShellContext shellContext() {
		return new DefaultShellContext();
	}
}
