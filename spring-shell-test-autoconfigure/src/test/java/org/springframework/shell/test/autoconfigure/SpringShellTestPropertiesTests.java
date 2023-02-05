/*
 * Copyright 2023 the original author or authors.
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

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SpringShellTestPropertiesTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	public void defaultNoPropertiesSet() {
		this.contextRunner
				.withUserConfiguration(Config1.class)
				.run((context) -> {
					SpringShellTestProperties properties = context.getBean(SpringShellTestProperties.class);
					assertThat(properties.getTerminalWidth()).isEqualTo(80);
					assertThat(properties.getTerminalHeight()).isEqualTo(24);
				});
	}

	@Test
	public void setProperties() {
		this.contextRunner
				.withPropertyValues("spring.shell.test.terminal-width=81")
				.withPropertyValues("spring.shell.test.terminal-height=25")
				.withUserConfiguration(Config1.class)
				.run((context) -> {
					SpringShellTestProperties properties = context.getBean(SpringShellTestProperties.class);
					assertThat(properties.getTerminalWidth()).isEqualTo(81);
					assertThat(properties.getTerminalHeight()).isEqualTo(25);
				});
	}

	@EnableConfigurationProperties({ SpringShellTestProperties.class })
	private static class Config1 {
	}
}
