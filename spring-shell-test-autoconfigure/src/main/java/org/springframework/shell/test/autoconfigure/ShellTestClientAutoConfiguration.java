/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.test.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.DefaultCommandParser;
import org.springframework.shell.test.ShellTestClient;

import java.util.Optional;

/**
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
@AutoConfiguration
public class ShellTestClientAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	ShellTestClient shellTestClient(CommandParser commandParser, CommandRegistry commandRegistry) {
		return new ShellTestClient(commandParser, commandRegistry);
	}

	@Bean
	@ConditionalOnMissingBean
	public CommandRegistry commandRegistry() {
		return new CommandRegistry();
	}

	@Bean
	@ConditionalOnMissingBean
	public CommandParser commandParser(CommandRegistry commandRegistry,
			Optional<ConfigurableConversionService> conversionService) {
		return new DefaultCommandParser(commandRegistry, conversionService);
	}

}
