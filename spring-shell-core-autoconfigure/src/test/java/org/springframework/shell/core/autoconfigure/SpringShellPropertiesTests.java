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
package org.springframework.shell.core.autoconfigure;

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SpringShellPropertiesTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	void defaultNoPropertiesSet() {
		this.contextRunner.withUserConfiguration(Config1.class).run(context -> {
			SpringShellProperties properties = context.getBean(SpringShellProperties.class);
			assertThat(properties.getHistory().isEnabled()).isTrue();
			assertThat(properties.getHistory().getName()).isNull();
			assertThat(properties.getConfig().getLocation()).isNull();
			assertThat(properties.getConfig().getEnv()).isNull();
			assertThat(properties.getInteractive().isEnabled()).isTrue();
			assertThat(properties.getTheme().getName()).isNull();
			assertThat(properties.getCommand().getClear().isEnabled()).isTrue();
			assertThat(properties.getCommand().getHelp().isEnabled()).isTrue();
			assertThat(properties.getCommand().getHistory().isEnabled()).isTrue();
			assertThat(properties.getCommand().getScript().isEnabled()).isTrue();
			assertThat(properties.getCommand().getVersion().isEnabled()).isTrue();
		});
	}

	@Test
	void setProperties() {
		this.contextRunner.withPropertyValues("spring.shell.history.enabled=false")
			.withPropertyValues("spring.shell.history.name=fakename")
			.withPropertyValues("spring.shell.config.location=fakelocation")
			.withPropertyValues("spring.shell.config.env=FAKE_ENV")
			.withPropertyValues("spring.shell.script.enabled=true")
			.withPropertyValues("spring.shell.interactive.enabled=true")
			.withPropertyValues("spring.shell.debug.enabled=true")
			.withPropertyValues("spring.shell.theme.name=fake")
			.withPropertyValues("spring.shell.command.clear.enabled=false")
			.withPropertyValues("spring.shell.command.help.enabled=false")
			.withPropertyValues("spring.shell.command.history.enabled=false")
			.withPropertyValues("spring.shell.command.quit.enabled=false")
			.withPropertyValues("spring.shell.command.script.enabled=false")
			.withPropertyValues("spring.shell.command.version.enabled=false")
			.withPropertyValues("spring.shell.help.enabled=false")
			.withPropertyValues("spring.shell.help.command=fake")
			.withPropertyValues("spring.shell.context.close=true")
			.withUserConfiguration(Config1.class)
			.run(context -> {
				SpringShellProperties properties = context.getBean(SpringShellProperties.class);
				assertThat(properties.getHistory().isEnabled()).isFalse();
				assertThat(properties.getHistory().getName()).isEqualTo("fakename");
				assertThat(properties.getConfig().getLocation()).isEqualTo("fakelocation");
				assertThat(properties.getConfig().getEnv()).isEqualTo("FAKE_ENV");
				assertThat(properties.getInteractive().isEnabled()).isTrue();
				assertThat(properties.getDebug().isEnabled()).isTrue();
				assertThat(properties.getTheme().getName()).isEqualTo("fake");
				assertThat(properties.getCommand().getClear().isEnabled()).isFalse();
				assertThat(properties.getCommand().getHelp().isEnabled()).isFalse();
				assertThat(properties.getCommand().getHistory().isEnabled()).isFalse();
				assertThat(properties.getCommand().getScript().isEnabled()).isFalse();
				assertThat(properties.getCommand().getVersion().isEnabled()).isFalse();
			});
	}

	@EnableConfigurationProperties({ SpringShellProperties.class })
	private static class Config1 {

	}

}
