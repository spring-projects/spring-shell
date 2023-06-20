/*
 * Copyright 2017-2023 the original author or authors.
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
package org.springframework.shell.result;

import org.jline.terminal.Terminal;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.shell.TerminalSizeAware;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandParserExceptionResolver;
import org.springframework.shell.context.ShellContext;
import org.springframework.shell.jline.InteractiveShellRunner;

/**
 * Used for explicit configuration of {@link org.springframework.shell.ResultHandler}s.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
@Configuration(proxyBeanMethods = false)
public class ResultHandlerConfig {

	@Bean
	@ConditionalOnClass(TerminalSizeAware.class)
	public TerminalSizeAwareResultHandler terminalSizeAwareResultHandler(Terminal terminal) {
		return new TerminalSizeAwareResultHandler(terminal);
	}

	@Bean
	public AttributedCharSequenceResultHandler attributedCharSequenceResultHandler(Terminal terminal) {
		return new AttributedCharSequenceResultHandler(terminal);
	}

	@Bean
	public DefaultResultHandler defaultResultHandler(Terminal terminal) {
		return new DefaultResultHandler(terminal);
	}

	@Bean
	public ParameterValidationExceptionResultHandler parameterValidationExceptionResultHandler(Terminal terminal) {
		return new ParameterValidationExceptionResultHandler(terminal);
	}

	@Bean
	@Order(CommandExceptionResolver.DEFAULT_PRECEDENCE)
	public CommandParserExceptionResolver commandParserExceptionResolver() {
		return new CommandParserExceptionResolver();
	}

	@Bean
	public ThrowableResultHandler throwableResultHandler(Terminal terminal, CommandCatalog commandCatalog,
			ShellContext shellContext, ObjectProvider<InteractiveShellRunner> interactiveApplicationRunner) {
		return new ThrowableResultHandler(terminal, commandCatalog, shellContext, interactiveApplicationRunner);
	}

	@Bean
	public CommandNotFoundResultHandler commandNotFoundResultHandler(Terminal terminal,
		ObjectProvider<CommandNotFoundMessageProvider> provider) {
		return new CommandNotFoundResultHandler(terminal, provider);
	}
}
