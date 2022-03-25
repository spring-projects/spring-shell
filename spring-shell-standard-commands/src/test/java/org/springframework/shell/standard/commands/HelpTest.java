/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.standard.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.Max;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.*;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.StandardParameterResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the {@link Help} command.
 *
 * @author Eric Bottard
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HelpTest.Config.class)
public class HelpTest {

	private static Locale previousLocale;
	private String testName;

	@BeforeAll
	public static void setAssumedLocale() {
		previousLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	@AfterAll
	public static void restorePreviousLocale() {
		Locale.setDefault(previousLocale);
	}

	@BeforeEach
	public void setup(TestInfo testInfo) {
		Optional<Method> testMethod = testInfo.getTestMethod();
		if (testMethod.isPresent()) {
			this.testName = testMethod.get().getName();
		}
	}

	@Autowired
	private Help help;

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

	@Test
	public void testUnknownCommand() throws Exception {
		assertThatThrownBy(() -> {
			this.help.help("some unknown command");
		}).isInstanceOf(IllegalArgumentException.class);
	}

	private String sample() throws IOException {
		InputStream is = new ClassPathResource(HelpTest.class.getSimpleName() + "-" + testName + ".txt", HelpTest.class).getInputStream();
		return FileCopyUtils.copyToString(new InputStreamReader(is, "UTF-8")).replace("&", "");
	}

	@Configuration
	static class Config {

		@Bean
		public Help help(CommandRegistry commandRegistry) {
			return new Help();
		}

		@Bean
		public CommandRegistry shell() {

			return new CommandRegistry() {

				@Override
				public Map<String, MethodTarget> listCommands() {
					Map<String, MethodTarget> result = new HashMap<>();
					MethodTarget methodTarget = MethodTargetFactory.createForUniqueMethodWithoutAvailabilityIndicator("firstCommand", commands(), new Command.Help("A rather extensive description of some command."));
					result.put("first-command", methodTarget);
					result.put("1st-command", methodTarget);

					methodTarget = MethodTargetFactory.createForUniqueMethodWithoutAvailabilityIndicator("secondCommand", commands(), new Command.Help("The second command. This one is known under several aliases as well."));
					result.put("second-command", methodTarget);
					result.put("yet-another-command", methodTarget);

					methodTarget = MethodTargetFactory.createForUniqueMethodWithoutAvailabilityIndicator("thirdCommand", commands(), new Command.Help("The last command."));
					result.put("third-command", methodTarget);

					methodTarget = MethodTargetFactory.createForUniqueMethodWithoutAvailabilityIndicator("firstCommandInGroup", commands(), new Command.Help("The first command in a separate group.", "Example Group"));
					result.put("first-group-command", methodTarget);

					methodTarget = MethodTargetFactory.createForUniqueMethodWithoutAvailabilityIndicator("secondCommandInGroup", commands(), new Command.Help("The second command in a separate group.", "Example Group"));
					result.put("second-group-command", methodTarget);

					return result;
				}

				@Override
				public void addCommand(String name, MethodTarget target) {
				}

				@Override
				public void removeCommand(String name) {
				}
			};
		}

		@Bean
		public ParameterResolver parameterResolver() {
			return new StandardParameterResolver(new DefaultConversionService(), Collections.emptySet());
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
				// Also, bears bean validation annotation
				@ShellOption(help = "The answer to everything", defaultValue = "42", value = "-n") @Max(5) int n,
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

		@ShellMethod
		public void firstCommandInGroup() {

		}

		@ShellMethod
		public void secondCommandInGroup() {

		}

	}
}
