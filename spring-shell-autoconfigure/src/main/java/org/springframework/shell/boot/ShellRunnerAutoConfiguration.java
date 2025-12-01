/*
 * Copyright 2021-2024 the original author or authors.
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

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.InputProvider;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.jline.InteractiveShellRunner;
import org.springframework.shell.core.jline.JLineInputProvider;
import org.springframework.shell.core.jline.NonInteractiveShellRunner;

@AutoConfiguration
public class ShellRunnerAutoConfiguration {

	@Bean
	public ApplicationRunner springShellApplicationRunner(ShellRunner shellRunner) {
		return args -> shellRunner.run(args.getSourceArgs());
	}

	@Bean
	@ConditionalOnMissingBean
	public InputProvider inputProvider(LineReader reader) {
		return new JLineInputProvider(reader);
	}

	// @Bean
	// @ConditionalOnMissingBean
	// public Terminal terminal() throws Exception {
	// return TerminalBuilder.builder().system(true).build();
	// }

	@Bean
	@ConditionalOnProperty(value = "spring.shell.interactive.enabled", havingValue = "true", matchIfMissing = true)
	public InteractiveShellRunner shellRunner(InputProvider inputProvider, Terminal terminal,
			CommandRegistry commandRegistry) {
		return new InteractiveShellRunner(inputProvider, terminal, commandRegistry);
	}

	@Bean
	@ConditionalOnProperty(value = "spring.shell.interactive.enabled", havingValue = "false")
	public NonInteractiveShellRunner nonInteractiveShellRunner(Parser parser, Terminal terminal,
			CommandRegistry commandRegistry) {
		return new NonInteractiveShellRunner(parser, terminal, commandRegistry);
	}

}
