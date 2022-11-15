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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.config.UserConfigPathProvider;
import org.springframework.util.StringUtils;

@AutoConfiguration
@EnableConfigurationProperties(SpringShellProperties.class)
public class UserConfigAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(UserConfigPathProvider.class)
	public UserConfigPathProvider userConfigPathProvider(SpringShellProperties springShellProperties) {
		return () -> {
			LocationResolver resolver = new LocationResolver(springShellProperties.getConfig().getEnv(),
					springShellProperties.getConfig().getLocation());
			return resolver.resolve();
		};
	}

	static class LocationResolver {

		private final static String XDG_CONFIG_HOME = "XDG_CONFIG_HOME";
		private final static String APP_DATA = "APP_DATA";
		private static final String USERCONFIG_PLACEHOLDER = "{userconfig}";
		private Function<String, Path> pathProvider = (path) -> Paths.get(path);
		private final String configDirEnv;
		private final String configDirLocation;

		LocationResolver(String configDirEnv, String configDirLocation) {
			this.configDirEnv = configDirEnv;
			this.configDirLocation = configDirLocation;
		}

		Path resolve() {
			String location;
			if (StringUtils.hasText(configDirEnv) && StringUtils.hasText(System.getenv(configDirEnv))) {
				location = System.getenv(configDirEnv);
			}
			else if (StringUtils.hasText(configDirLocation)) {
				location = configDirLocation;
			}
			else {
				location = "";
			}
			if (usesUserConfigLocation(location)) {
				location = resolveUserConfigLocation(location);
			}
			return pathProvider.apply(location);
		}

		private boolean usesUserConfigLocation(String location) {
			return location.contains(USERCONFIG_PLACEHOLDER);
		}

		private String resolveUserConfigLocation(String location) {
			String userConfigHome = "";
			if (StringUtils.hasText(System.getenv(XDG_CONFIG_HOME))) {
				userConfigHome = System.getenv(XDG_CONFIG_HOME);
			}
			else if (isWindows() && StringUtils.hasText(System.getenv(APP_DATA))) {
				userConfigHome = System.getenv(APP_DATA);
			}
			else {
				userConfigHome = System.getProperty("user.home") + "/" + ".config";
			}
			return location.replace(USERCONFIG_PLACEHOLDER, userConfigHome);
		}

		private boolean isWindows() {
			String os = System.getProperty("os.name");
			return os.startsWith("Windows");
		}
	}
}
