/*
 * Copyright 2021-present the original author or authors.
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

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.ConsoleInputProvider;
import org.springframework.shell.core.NonInteractiveShellRunner;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.SystemShellRunner;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;
import org.springframework.shell.jline.JLineInputProvider;
import org.springframework.shell.jline.JLineShellRunner;

@AutoConfiguration
public class ShellRunnerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ApplicationRunner springShellApplicationRunner(ShellRunner shellRunner) {
		return args -> shellRunner.run(args.getSourceArgs());
	}

	@Bean
	@ConditionalOnMissingClass("org.springframework.shell.jline.DefaultJLineShellConfiguration")
	public ShellRunner systemShellRunner(ConsoleInputProvider consoleInputProvider, CommandParser commandParser,
			CommandRegistry commandRegistry) {
		return new SystemShellRunner(consoleInputProvider, commandParser, commandRegistry);
	}

	@Bean
	@ConditionalOnClass(name = "org.springframework.shell.jline.JLineInputProvider")
	public ShellRunner jlineShellRunner(JLineInputProvider inputProvider, CommandParser commandParser,
			CommandRegistry commandRegistry) {
		return new JLineShellRunner(inputProvider, commandParser, commandRegistry);
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.shell.interactive", name = "enabled", havingValue = "false")
	public ShellRunner nonInteractiveShellRunner(CommandParser commandParser, CommandRegistry commandRegistry) {
		return new NonInteractiveShellRunner(commandParser, commandRegistry);
	}

	@Bean
	@ConditionalOnMissingBean
	public ConsoleInputProvider consoleInputProvider() {
		return new ConsoleInputProvider();
	}

	@Bean
	@ConditionalOnMissingBean
	public CommandParser commandParser() {
		return new DefaultCommandParser();
	}

}
