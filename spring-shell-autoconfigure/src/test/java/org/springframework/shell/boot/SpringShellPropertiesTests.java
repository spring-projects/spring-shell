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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringShellPropertiesTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	public void defaultNoPropertiesSet() {
		this.contextRunner
				.withUserConfiguration(Config1.class)
				.run((context) -> {
					SpringShellProperties properties = context.getBean(SpringShellProperties.class);
					assertThat(properties.getScript().isEnabled()).isTrue();
					assertThat(properties.getInteractive().isEnabled()).isTrue();
					assertThat(properties.getNoninteractive().isEnabled()).isTrue();
					assertThat(properties.getCommand().getClear().isEnabled()).isTrue();
					assertThat(properties.getCommand().getHelp().isEnabled()).isTrue();
					assertThat(properties.getCommand().getHistory().isEnabled()).isTrue();
					assertThat(properties.getCommand().getQuit().isEnabled()).isTrue();
					assertThat(properties.getCommand().getScript().isEnabled()).isTrue();
					assertThat(properties.getCommand().getStacktrace().isEnabled()).isTrue();
				});
	}

	@Test
	public void setProperties() {
		this.contextRunner
				.withInitializer(context -> {
					Map<String, Object> map = new HashMap<>();
					map.put("spring.shell.script.enabled", "false");
					map.put("spring.shell.interactive.enabled", "false");
					map.put("spring.shell.noninteractive.enabled", "false");
					map.put("spring.shell.command.clear.enabled", "false");
					map.put("spring.shell.command.help.enabled", "false");
					map.put("spring.shell.command.history.enabled", "false");
					map.put("spring.shell.command.quit.enabled", "false");
					map.put("spring.shell.command.script.enabled", "false");
					map.put("spring.shell.command.stacktrace.enabled", "false");
					context.getEnvironment().getPropertySources().addLast(new SystemEnvironmentPropertySource(
							StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, map));
				})
				.withUserConfiguration(Config1.class)
				.run((context) -> {
					SpringShellProperties properties = context.getBean(SpringShellProperties.class);
					assertThat(properties.getScript().isEnabled()).isFalse();
					assertThat(properties.getInteractive().isEnabled()).isFalse();
					assertThat(properties.getNoninteractive().isEnabled()).isFalse();
					assertThat(properties.getCommand().getClear().isEnabled()).isFalse();
					assertThat(properties.getCommand().getHelp().isEnabled()).isFalse();
					assertThat(properties.getCommand().getHistory().isEnabled()).isFalse();
					assertThat(properties.getCommand().getQuit().isEnabled()).isFalse();
					assertThat(properties.getCommand().getScript().isEnabled()).isFalse();
					assertThat(properties.getCommand().getStacktrace().isEnabled()).isFalse();
				});
	}

	@EnableConfigurationProperties({ SpringShellProperties.class })
	private static class Config1 {
	}
}
