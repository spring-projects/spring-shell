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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.NonInteractiveShellApplicationRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.jline.ScriptShellApplicationRunner;

@Configuration(proxyBeanMethods = false)
public class ShellRunnerAutoConfiguration {

	private Shell shell;
	private PromptProvider promptProvider;
	private LineReader lineReader;
	private Parser parser;

	public ShellRunnerAutoConfiguration(Shell shell, PromptProvider promptProvider, LineReader lineReader, Parser parser) {
		this.shell = shell;
		this.promptProvider = promptProvider;
		this.lineReader = lineReader;
		this.parser = parser;
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.shell.interactive", value = "enabled", havingValue = "true", matchIfMissing = true)
	public InteractiveShellApplicationRunner interactiveApplicationRunner() {
		return new InteractiveShellApplicationRunner(lineReader, promptProvider, shell);
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.shell.noninteractive", value = "enabled", havingValue = "true", matchIfMissing = true)
	public NonInteractiveShellApplicationRunner nonInteractiveApplicationRunner() {
		return new NonInteractiveShellApplicationRunner(shell);
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.shell.script", value = "enabled", havingValue = "true", matchIfMissing = true)
	public ScriptShellApplicationRunner scriptApplicationRunner() {
		return new ScriptShellApplicationRunner(parser, shell);
	}
}
