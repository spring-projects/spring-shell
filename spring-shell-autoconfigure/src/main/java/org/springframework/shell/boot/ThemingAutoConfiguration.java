/*
 * Copyright 2022 the original author or authors.
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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeActive;
import org.springframework.shell.style.ThemeRegistry;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeSettings;
import org.springframework.util.StringUtils;

@AutoConfiguration
@EnableConfigurationProperties(SpringShellProperties.class)
public class ThemingAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ThemeActive themeActive() {
		return () -> {
			if (System.getenv("CI") != null || System.getenv("NO_COLOR") != null) {
				return "dump";
			}
			return "default";
		};
	}

	@Bean
	public ThemeRegistry themeRegistry(ObjectProvider<Theme> themes) {
		ThemeRegistry registry = new ThemeRegistry();
		registry.register(Theme.of("default", ThemeSettings.defaults()));
		registry.register(Theme.of("dump", ThemeSettings.dump()));
		themes.orderedStream().forEachOrdered(registry::register);
		return registry;
	}

	@Bean
	public ThemeResolver shellThemeResolver(ThemeRegistry themeRegistry, SpringShellProperties properties,
			ThemeActive themeActive) {
		String themeName = properties.getTheme().getName();
		if (!StringUtils.hasText(themeName)) {
			themeName = themeActive.get();
		}
		if (!StringUtils.hasText(themeName)) {
			themeName = "default";
		}
		return new ThemeResolver(themeRegistry, themeName);
	}

	@Bean
	public TemplateExecutor templateExecutor(ThemeResolver themeResolver) {
		return new TemplateExecutor(themeResolver);
	}
}
