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
package org.springframework.shell.boot;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.ShellRunner;
import org.springframework.util.ClassUtils;

/**
 * @author Janne Valkealahti
 * @author Chris Bono
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
@AutoConfiguration
@EnableConfigurationProperties(SpringShellProperties.class)
public class ApplicationRunnerAutoConfiguration {

	@Bean
	public ApplicationRunner applicationRunner(ObjectProvider<ShellRunner> shellRunner) {
		return new ShellApplicationRunner(shellRunner);
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.shell.context", name = "close", havingValue = "true")
	public ApplicationListener<ApplicationReadyEvent> applicationReadyEventListener() {
		return new ApplicationReadyEventListener();
	}

	static class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {
			// request context close after application runners so that
			// shell exits in case that context is kept alive
			event.getApplicationContext().close();
		}

	}

	record ShellApplicationRunner(ObjectProvider<ShellRunner> shellRunner) implements ApplicationRunner {
		@Override
		public void run(ApplicationArguments args) throws Exception {
			shellRunner.orderedStream().forEachOrdered(runner -> {
				try {
					boolean run = runner.run(args.getSourceArgs());
					if (run) {
						return;
					}
				}
				catch (Exception e) {
					throw new IllegalStateException(
							"Unable to run '" + ClassUtils.getShortName(runner.getClass()) + "'", e);
				}
			});
		}
	}

}
