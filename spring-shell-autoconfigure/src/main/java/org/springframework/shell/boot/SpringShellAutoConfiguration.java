/*
 * Copyright 2017-present the original author or authors.
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

import java.util.Set;

import org.jline.terminal.Terminal;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.core.ResultHandler;
import org.springframework.shell.core.ResultHandlerService;
import org.springframework.shell.core.Shell;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.config.ShellConversionServiceSupplier;
import org.springframework.shell.core.context.ShellContext;
import org.springframework.shell.core.exit.ExitCodeMappings;
import org.springframework.shell.core.result.GenericResultHandlerService;
import org.springframework.shell.core.result.ResultHandlerConfig;

/**
 * Creates supporting beans for running the Shell
 */
@AutoConfiguration
@Import(ResultHandlerConfig.class)
public class SpringShellAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ShellConversionServiceSupplier shellConversionServiceSupplier(ApplicationContext applicationContext) {
		ApplicationConversionService service = new ApplicationConversionService();
		DefaultConversionService.addDefaultConverters(service);
		DefaultConversionService.addCollectionConverters(service);
		ApplicationConversionService.addBeans(service, applicationContext);
		return () -> service;
	}

	@Bean
	public ResultHandlerService resultHandlerService(Set<ResultHandler<?>> resultHandlers) {
		GenericResultHandlerService service = new GenericResultHandlerService();
		for (ResultHandler<?> resultHandler : resultHandlers) {
			service.addResultHandler(resultHandler);
		}
		return service;
	}

	@Bean
	public Shell shell(ResultHandlerService resultHandlerService, CommandRegistry commandRegistry, Terminal terminal,
					   ShellConversionServiceSupplier shellConversionServiceSupplier, ShellContext shellContext,
					   ExitCodeMappings exitCodeMappings) {
		Shell shell = new Shell(resultHandlerService, commandRegistry, terminal, shellContext, exitCodeMappings);
		shell.setConversionService(shellConversionServiceSupplier.get());
		return shell;
	}
}
