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
package org.springframework.shell.boot;

import java.util.Set;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.TerminalUIBuilder;
import org.springframework.shell.component.view.TerminalUICustomizer;
import org.springframework.shell.style.ThemeActive;
import org.springframework.shell.style.ThemeRegistry;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TerminalUIAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(TerminalUIAutoConfiguration.class));

	@Test
	public void terminalUICreated() {
		this.contextRunner
				.withUserConfiguration(MockConfiguration.class)
				.run(context -> {
					assertThat(context).hasSingleBean(TerminalUIBuilder.class);
				});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void canCustomize() {
		this.contextRunner
				.withUserConfiguration(TestConfiguration.class, MockConfiguration.class)
				.run(context -> {
					TerminalUIBuilder builder = context.getBean(TerminalUIBuilder.class);
					Set<TerminalUICustomizer> customizers = (Set<TerminalUICustomizer>) ReflectionTestUtils
							.getField(builder, "customizers");
					assertThat(customizers).hasSize(1);
				});
	}

	@Configuration(proxyBeanMethods = false)
	static class MockConfiguration {

		@Bean
		Terminal mockTerminal() {
			Terminal terminal = mock(Terminal.class);
			when(terminal.getBufferSize()).thenReturn(new Size());
			return terminal;
		}

		@Bean
		ThemeResolver mockThemeResolver() {
			return new ThemeResolver(new ThemeRegistry(), "default");
		}

		@Bean
		ThemeActive themeActive() {
			return () -> {
				return "default";
			};
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class TestConfiguration {

		@Bean
		TerminalUICustomizer terminalUICustomizer() {
			return new TestTerminalUICustomizer();
		}
	}

	static class TestTerminalUICustomizer implements TerminalUICustomizer {

		@Override
		public void customize(TerminalUI terminalUI) {
			terminalUI.setThemeName("test");
		}
	}

}
