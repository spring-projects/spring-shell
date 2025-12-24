/*
 * Copyright 2025-present the original author or authors.
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

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.jline.JLineInputProvider;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.test.util.ReflectionTestUtils;

public class CustomPromptProviderAutoConfigurationTests {

	@Test
	void testCustomPromptProviderAutoConfiguration() {
		// given
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withUserConfiguration(SpringShellApplication.class)
			.withConfiguration(AutoConfigurations.of(SpringShellAutoConfiguration.class))
			.withPropertyValues("spring.shell.interactive.enabled=true");

		// when
		contextRunner.run(context -> {
			// then
			assert (context.getBeansOfType(JLineInputProvider.class).size() == 1);
			JLineInputProvider jLineInputProvider = context.getBean(JLineInputProvider.class);
			PromptProvider promptProvider = (PromptProvider) ReflectionTestUtils.getField(jLineInputProvider,
					"promptProvider");
			AttributedString prompt = promptProvider.getPrompt();
			Assertions.assertEquals("myprompt:>", prompt.toString());
		});

	}

	@SpringBootApplication
	static class SpringShellApplication {

		@Command
		public void hi() {
			System.out.println("Hello world!");
		}

		@Bean
		public PromptProvider myPromptProvider() {
			return () -> new AttributedString("myprompt:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
		}

	}

}
