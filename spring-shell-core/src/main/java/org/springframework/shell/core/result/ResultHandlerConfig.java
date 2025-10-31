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
package org.springframework.shell.core.result;

import org.jline.terminal.Terminal;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.CommandExceptionResolver;
import org.springframework.shell.core.command.CommandParserExceptionResolver;
import org.springframework.shell.core.context.ShellContext;
import org.springframework.shell.core.jline.InteractiveShellRunner;

/**
 * Used for explicit configuration of {@link ResultHandler}s.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
@Configuration(proxyBeanMethods = false)
public class ResultHandlerConfig {

	@Bean
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
	public ThrowableResultHandler throwableResultHandler(Terminal terminal, CommandRegistry commandRegistry,
			ShellContext shellContext, ObjectProvider<InteractiveShellRunner> interactiveApplicationRunner) {
		return new ThrowableResultHandler(terminal, commandRegistry, shellContext, interactiveApplicationRunner);
	}

	@Bean
	public CommandNotFoundResultHandler commandNotFoundResultHandler(Terminal terminal,
		ObjectProvider<CommandNotFoundMessageProvider> provider) {
		return new CommandNotFoundResultHandler(terminal, provider);
	}
}
