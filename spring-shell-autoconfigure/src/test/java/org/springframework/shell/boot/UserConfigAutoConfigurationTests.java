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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.shell.config.UserConfigPathProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class UserConfigAutoConfigurationTests {

	private final static Logger log = LoggerFactory.getLogger(UserConfigAutoConfigurationTests.class);

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(UserConfigAutoConfiguration.class));

	@Test
	public void testDefaults() {
		this.contextRunner
				.run(context -> {
					assertThat(context).hasSingleBean(UserConfigPathProvider.class);
					UserConfigPathProvider provider = context.getBean(UserConfigPathProvider.class);
					Path path = provider.provide();
					assertThat(path).isNotNull();
					log.info("Path testDefaults: {}", path.toAbsolutePath());
				});
	}

	@Test
	public void testUserConfig() {
		this.contextRunner
				.withPropertyValues("spring.shell.config.location={userconfig}/test")
				.run(context -> {
					assertThat(context).hasSingleBean(UserConfigPathProvider.class);
					UserConfigPathProvider provider = context.getBean(UserConfigPathProvider.class);
					Path path = provider.provide();
					assertThat(path).isNotNull();
					log.info("Path testUserConfig: {}", path.toAbsolutePath());
				});
	}
}
