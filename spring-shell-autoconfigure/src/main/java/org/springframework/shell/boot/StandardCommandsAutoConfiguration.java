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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.Command;

/**
 * Creates beans for standard commands.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
@AutoConfiguration
@EnableConfigurationProperties(SpringShellProperties.class)
public class StandardCommandsAutoConfiguration {

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.help.enabled", havingValue = "true", matchIfMissing = true)
	public Command helpCommand() {
		return new org.springframework.shell.core.commands.Help();
	}

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.clear.enabled", havingValue = "true", matchIfMissing = true)
	public Command clearCommand() {
		return new org.springframework.shell.core.commands.Clear();
	}

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.version.enabled", havingValue = "true", matchIfMissing = true)
	public Command versionCommand() {
		return new org.springframework.shell.core.commands.Version();
	}

}
