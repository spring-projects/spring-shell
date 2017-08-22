/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.standard.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.standard.StandardParameterResolver;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Tests for the {@link Help} command.
 *
 * @author Eric Bottard
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HelpTest.Config.class)
public class HelpTest {

	@Autowired
	private Help help;

	@Rule
	public TestName testName = new TestName();

	@Test
	public void testCommandHelp() throws Exception {
		CharSequence help = this.help.help("first-command").toString();
		Assertions.assertThat(help).isEqualTo(sample());
	}

	@Test
	public void testCommandList() throws Exception {
		String list = this.help.help(null).toString();
		Assertions.assertThat(list).isEqualTo(sample());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnknownCommand() throws Exception {
		this.help.help("some unknown command");
	}

	private String sample() throws IOException {
		InputStream is = new ClassPathResource(HelpTest.class.getSimpleName() + "-" + testName.getMethodName() + ".txt", HelpTest.class).getInputStream();
		return FileCopyUtils.copyToString(new InputStreamReader(is, "UTF-8")).replace("&", "");
	}

	@Configuration
	static class Config {

		@Bean
		public Help help() {
			return new Help(Collections.singletonList(parameterResolver()));
		}

		@Bean
		public CommandRegistry shell() {
			return () -> {
				Map<String, MethodTarget> result = new HashMap<>();
				MethodTarget methodTarget = MethodTarget.of("firstCommand", commands(), "A rather extensive description of some command.");
				result.put("first-command", methodTarget);
				result.put("1st-command", methodTarget);

				methodTarget = MethodTarget.of("secondCommand", commands(), "The second command. This one is known under several aliases as well.");
				result.put("second-command", methodTarget);
				result.put("yet-another-command", methodTarget);

				methodTarget = MethodTarget.of("thirdCommand", commands(), "The last command.");
				result.put("third-command", methodTarget);

				return result;
			};
		}

		@Bean
		public ParameterResolver parameterResolver() {
			return new StandardParameterResolver(new DefaultConversionService());
		}

		@Bean
		public Object commands() {
			return new Commands();
		}

	}

	@ShellComponent
	static class Commands {

		@ShellMethod(prefix = "--")
		public void firstCommand(
				// Single key and arity = 0. Help displayed on same line
				@ShellOption(help = "Whether to delete recursively", arity = 0, value = "-r") boolean r,
				// Multiple keys and arity 0. Help displayed on next line
				@ShellOption(help = "Do not ask for confirmation. YOLO", arity = 0, value = {"-f", "--force"}) boolean force,
				// Single key, arity >= 1. Help displayed on next line. Optional
				@ShellOption(help = "The answer to everything", defaultValue = "42", value = "-n") int n,
		        // Single key, arity > 1.
		        @ShellOption(help = "Some other parameters", arity = 3, value = "-o") float[] o
		) {

		}

		@ShellMethod
		public void secondCommand() {

		}

		@ShellMethod
		public void thirdCommand() {

		}

	}
}
