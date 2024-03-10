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
import org.jline.reader.Parser;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Shell;
import org.springframework.shell.boot.condition.OnNotPrimaryCommandCondition;
import org.springframework.shell.boot.condition.OnPrimaryCommandCondition;
import org.springframework.shell.context.ShellContext;
import org.springframework.shell.jline.InteractiveShellRunner;
import org.springframework.shell.jline.NonInteractiveShellRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.jline.ScriptShellRunner;

@AutoConfiguration
public class ShellRunnerAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@EnableConfigurationProperties(SpringShellProperties.class)
	@Conditional(OnPrimaryCommandCondition.class)
	public static class PrimaryCommandConfiguration {

		@Bean
		@ConditionalOnProperty(prefix = "spring.shell.noninteractive", value = "enabled", havingValue = "true", matchIfMissing = true)
		public NonInteractiveShellRunner nonInteractiveApplicationRunner(Shell shell, ShellContext shellContext,
				ObjectProvider<NonInteractiveShellRunnerCustomizer> customizer, SpringShellProperties properties) {
			NonInteractiveShellRunner shellRunner = new NonInteractiveShellRunner(shell, shellContext,
					properties.getNoninteractive().getPrimaryCommand());
			customizer.orderedStream().forEach((c) -> c.customize(shellRunner));
			return shellRunner;
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Conditional(OnNotPrimaryCommandCondition.class)
	public static class NonePrimaryCommandConfiguration {

		@Bean
		@ConditionalOnProperty(prefix = "spring.shell.interactive", value = "enabled", havingValue = "true", matchIfMissing = false)
		public InteractiveShellRunner interactiveApplicationRunner(LineReader lineReader, PromptProvider promptProvider,
				Shell shell, ShellContext shellContext) {
			return new InteractiveShellRunner(lineReader, promptProvider, shell, shellContext);
		}

		@Bean
		@ConditionalOnProperty(prefix = "spring.shell.noninteractive", value = "enabled", havingValue = "true", matchIfMissing = true)
		public NonInteractiveShellRunner nonInteractiveApplicationRunner(Shell shell, ShellContext shellContext,
				ObjectProvider<NonInteractiveShellRunnerCustomizer> customizer) {
			NonInteractiveShellRunner shellRunner = new NonInteractiveShellRunner(shell, shellContext);
			customizer.orderedStream().forEach((c) -> c.customize(shellRunner));
			return shellRunner;
		}

		@Bean
		@ConditionalOnProperty(prefix = "spring.shell.script", value = "enabled", havingValue = "true", matchIfMissing = false)
		public ScriptShellRunner scriptApplicationRunner(Parser parser, Shell shell) {
			return new ScriptShellRunner(parser, shell);
		}

	}
}
