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
package org.springframework.shell.standard.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.Max;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HelpTests.Config.class)
public class HelpTests {

	private static Locale previousLocale;
	private String testName;
	private Map<String, CommandRegistration> registrations = new HashMap<>();
	private CommandsPojo commandsPojo = new CommandsPojo();

	@MockBean
	private CommandCatalog commandCatalog;

	@Autowired
	private Help help;

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
		registrations.clear();
		Optional<Method> testMethod = testInfo.getTestMethod();
		if (testMethod.isPresent()) {
			this.testName = testMethod.get().getName();
		}
		Mockito.when(commandCatalog.getRegistrations()).thenReturn(registrations);
	}

	@Test
	public void testCommandHelp() throws Exception {
		CommandRegistration registration = CommandRegistration.builder()
			.command("first-command")
			.help("A rather extensive description of some command.")
			.withTarget()
				.method(commandsPojo, "firstCommand")
				.and()
			.withOption()
				.shortNames('r')
				.description("Whether to delete recursively")
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('f')
				.description("Do not ask for confirmation. YOLO")
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('n')
				.description("The answer to everything")
				.defaultValue("42")
				.type(int.class)
				.and()
			.withOption()
				.shortNames('o')
				.description("Some other parameters")
				.type(float[].class)
				.and()
			.build();
		registrations.put("first-command", registration);
		registrations.put("1st-command", registration);
		CharSequence help = this.help.help("first-command").toString();
		assertThat(help).isEqualTo(sample());
	}

	@Test
	public void testCommandList() throws Exception {
		CommandRegistration registration1 = CommandRegistration.builder()
			.command("first-command")
			.help("A rather extensive description of some command.")
			.withTarget()
				.method(commandsPojo, "firstCommand")
				.and()
			.withOption()
				.shortNames('r')
				.and()
			.build();
		registrations.put("first-command", registration1);
		registrations.put("1st-command", registration1);

		CommandRegistration registration2 = CommandRegistration.builder()
			.command("second-command")
			.help("The second command. This one is known under several aliases as well.")
			.withTarget()
				.method(commandsPojo, "secondCommand")
				.and()
			.build();
		registrations.put("second-command", registration2);
		registrations.put("yet-another-command", registration2);

		CommandRegistration registration3 = CommandRegistration.builder()
			.command("second-command")
			.help("The last command.")
			.withTarget()
				.method(commandsPojo, "thirdCommand")
				.and()
			.build();
		registrations.put("third-command", registration3);

		CommandRegistration registration4 = CommandRegistration.builder()
			.command("first-group-command")
			.help("The first command in a separate group.")
			.group("Example Group")
			.withTarget()
				.method(commandsPojo, "firstCommandInGroup")
				.and()
			.build();
		registrations.put("first-group-command", registration4);

		CommandRegistration registration5 = CommandRegistration.builder()
			.command("second-group-command")
			.help("The second command in a separate group.")
			.group("Example Group")
			.withTarget()
				.method(commandsPojo, "secondCommandInGroup")
				.and()
			.build();
		registrations.put("second-group-command", registration5);

		String list = this.help.help(null).toString();
		assertThat(list).isEqualTo(sample());
	}

	@Test
	public void testUnknownCommand() throws Exception {
		assertThatThrownBy(() -> {
			this.help.help("some unknown command");
		}).isInstanceOf(IllegalArgumentException.class);
	}

	private String sample() throws IOException {
		InputStream is = new ClassPathResource(HelpTests.class.getSimpleName() + "-" + testName + ".txt", HelpTests.class).getInputStream();
		return FileCopyUtils.copyToString(new InputStreamReader(is, "UTF-8")).replace("&", "");
	}

	@Configuration
	static class Config {

		@Bean
		public Help help() {
			return new Help();
		}

		// @Bean
		// public ParameterResolver parameterResolver() {
		// 	return new StandardParameterResolver(new DefaultConversionService(), Collections.emptySet());
		// }
	}

	@ShellComponent
	static class CommandsPojo {

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
