/*
 * Copyright 2022-2024 the original author or authors.
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

import java.util.stream.Stream;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.style.FigureSettings;
import org.springframework.shell.style.SpinnerSettings;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeRegistry;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeSettings;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class ThemingAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(ThemingAutoConfiguration.class));

	@Test
	void createsDefaultBeans() {
		this.contextRunner
				.run(context -> {
					assertThat(context).hasSingleBean(TemplateExecutor.class);
					assertThat(context).hasSingleBean(ThemeRegistry.class);
					ThemeRegistry registry = context.getBean(ThemeRegistry.class);
					assertThat(registry.get("default")).isNotNull();
					assertThat(registry.get("dump")).isNotNull();
					assertThat(context).hasSingleBean(ThemeResolver.class);
					ThemeResolver resolver = context.getBean(ThemeResolver.class);
					assertThat(resolver).extracting("theme").asInstanceOf(THEME).hasName("default", "dump");
				});
	}

	@Test
	public void canRegisterCustomTheme() {
		this.contextRunner
				.withUserConfiguration(CustomThemeConfig.class)
				.run(context -> {
					assertThat(context).hasSingleBean(ThemeRegistry.class);
					ThemeRegistry registry = context.getBean(ThemeRegistry.class);
					assertThat(registry.get("default")).isNotNull();
					assertThat(registry.get("mytheme")).isNotNull();
				});
	}

	@Configuration
	static class CustomThemeConfig {

		@Bean
		public Theme myTheme() {
			return new Theme() {

				@Override
				public String getName() {
					return "mytheme";
				}

				@Override
				public ThemeSettings getSettings() {
					return new MyThemeSettings();
				}
			};
		}
	}

	static class MyThemeSettings extends ThemeSettings {
		MyThemeSettings() {
			super(StyleSettings.defaults(), FigureSettings.defaults(), SpinnerSettings.defaults());
		}
	}

	InstanceOfAssertFactory<Theme, ThemeAssert> THEME = new InstanceOfAssertFactory<>(Theme.class,
			ThemeAssertions::assertThat);

	static class ThemeAssertions {

		public static ThemeAssert assertThat(Theme actual) {
			return new ThemeAssert(actual);
		}
	}

	static class ThemeAssert extends AbstractAssert<ThemeAssert, Theme> {

		public ThemeAssert(Theme actual) {
			super(actual, ThemeAssert.class);
		}

		public ThemeAssert hasName(String... names) {
			isNotNull();
			boolean match = Stream.of(names).filter(n -> actual.getName().equals(n)).findFirst().isPresent();
			if (!match) {
				failWithMessage("Expected theme to have names %s but was %s",
						StringUtils.arrayToCommaDelimitedString(names), actual.getName());
			}
			return this;
		}
	}

}
