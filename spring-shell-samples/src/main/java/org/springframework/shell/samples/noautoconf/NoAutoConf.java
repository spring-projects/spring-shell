/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.samples.noautoconf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.SpringShellAutoConfiguration;
import org.springframework.shell.jcommander.JCommanderParameterResolverAutoConfiguration;
import org.springframework.shell.jline.JLineShellAutoConfiguration;
import org.springframework.shell.samples.jcommander.JCommanderCommands;
import org.springframework.shell.samples.legacy.LegacyCommands;
import org.springframework.shell.samples.standard.Commands;
import org.springframework.shell.samples.standard.DynamicCommands;
import org.springframework.shell.samples.standard.TableCommands;
import org.springframework.shell.standard.FileValueProvider;
import org.springframework.shell.standard.StandardAPIAutoConfiguration;
import org.springframework.shell.standard.commands.StandardCommandsAutoConfiguration;

/**
 * This class shows how to use the full extent of Spring Shell without relying on Boot auto configuration.
 *
 * @author Eric Bottard
 */
@Configuration
@Import({
		// Core runtime
		SpringShellAutoConfiguration.class,
		JLineShellAutoConfiguration.class,
		// Various Resolvers
		JCommanderParameterResolverAutoConfiguration.class,
		StandardAPIAutoConfiguration.class,
		// Built-In Commands
		StandardCommandsAutoConfiguration.class,
		// Allows ${} support
		//PropertyPlaceholderAutoConfiguration.class,
		// Sample Commands
		JCommanderCommands.class,
		LegacyCommands.class,
		Commands.class,
		FileValueProvider.class,
		DynamicCommands.class,
		TableCommands.class,
		})
public class NoAutoConf {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(NoAutoConf.class, args);
	}

}
