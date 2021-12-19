/*
 * Copyright 2021 the original author or authors.
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
import org.jline.reader.Parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.jline.ScriptShellApplicationRunner;

import static org.springframework.shell.jline.InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE;
import static org.springframework.shell.jline.ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT;

@Configuration
public class ApplicationRunnerAutoConfiguration {

	@Autowired
	private Shell shell;

	@Autowired
	private PromptProvider promptProvider;

	@Autowired
	private LineReader lineReader;

	@Bean
	@ConditionalOnProperty(prefix = SPRING_SHELL_INTERACTIVE, value = InteractiveShellApplicationRunner.ENABLED, havingValue = "true", matchIfMissing = true)
	public ApplicationRunner interactiveApplicationRunner(Environment environment) {
		return new InteractiveShellApplicationRunner(lineReader, promptProvider, shell, environment);
	}

	@Bean
	@ConditionalOnProperty(prefix = SPRING_SHELL_SCRIPT, value = ScriptShellApplicationRunner.ENABLED, havingValue = "true", matchIfMissing = true)
	public ApplicationRunner scriptApplicationRunner(Parser parser, ConfigurableEnvironment environment) {
		return new ScriptShellApplicationRunner(parser, shell, environment);
	}
}
