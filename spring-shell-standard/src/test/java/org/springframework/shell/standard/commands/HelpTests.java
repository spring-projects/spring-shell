/*
 * Copyright 2022-2023 the original author or authors.
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
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import jakarta.validation.constraints.Max;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeRegistry;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeSettings;
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
	private CommandsPojo commandsPojo = new CommandsPojo();

	@Autowired
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
		Optional<Method> testMethod = testInfo.getTestMethod();
		if (testMethod.isPresent()) {
			this.testName = testMethod.get().getName();
		}
		Collection<CommandRegistration> regs = this.commandCatalog.getRegistrations().values();
		regs.stream().forEach(r -> {
			this.commandCatalog.unregister(r);
		});
	}

	@Test
	public void testCommandHelp() throws Exception {
		CommandRegistration registration = CommandRegistration.builder()
			.command("first-command")
			.description("A rather extensive description of some command.")
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
		commandCatalog.register(registration);
		String help = this.help.help(new String[] { "first-command" }).toString();
		help = removeNewLines(help);
		assertThat(help).isEqualTo(sample());
	}

	@Test
	public void testCommandListDefault() throws Exception {
		registerCommandListCommands();
		String list = this.help.help(null).toString();
		list = removeNewLines(list);
		assertThat(list).isEqualTo(sample());
	}

	@Test
	public void testCommandListFlat() throws Exception {
		registerCommandListCommands();
		this.help.setShowGroups(false);
		String list = this.help.help(null).toString();
		list = removeNewLines(list);
		assertThat(list).isEqualTo(sample());
	}

	@Test
	public void testUnknownCommand() throws Exception {
		assertThatThrownBy(() -> {
			this.help.help(new String[] { "some", "unknown", "command" });
		}).isInstanceOf(IllegalArgumentException.class);
	}

	private String removeNewLines(String str) {
		return str.replace("\r", "").replace("\n", "");
	}

	private String sample() throws IOException {
		InputStream is = new ClassPathResource(HelpTests.class.getSimpleName() + "-" + testName + ".txt", HelpTests.class).getInputStream();
		return removeNewLines(FileCopyUtils.copyToString(new InputStreamReader(is, "UTF-8")));
	}

	private void registerCommandListCommands() throws Exception {
		CommandRegistration registration1 = CommandRegistration.builder()
			.command("first-command")
			.description("A rather extensive description of some command.")
			.withAlias()
				.command("1st-command")
				.and()
			.withTarget()
				.method(commandsPojo, "firstCommand")
				.and()
			.withOption()
				.shortNames('r')
				.and()
			.build();
		commandCatalog.register(registration1);

		CommandRegistration registration2 = CommandRegistration.builder()
			.command("second-command")
			.description("The second command. This one is known under several aliases as well.")
			.withAlias()
				.command("yet-another-command")
				.and()
			.withTarget()
				.method(commandsPojo, "secondCommand")
				.and()
			.build();
		commandCatalog.register(registration2);

		CommandRegistration registration3 = CommandRegistration.builder()
			.command("third-command")
			.description("The last command.")
			.withTarget()
				.method(commandsPojo, "thirdCommand")
				.and()
			.build();
		commandCatalog.register(registration3);

		CommandRegistration registration4 = CommandRegistration.builder()
			.command("first-group-command")
			.description("The first command in a separate group.")
			.group("Example Group")
			.withTarget()
				.method(commandsPojo, "firstCommandInGroup")
				.and()
			.build();
		commandCatalog.register(registration4);

		CommandRegistration registration5 = CommandRegistration.builder()
			.command("second-group-command")
			.description("The second command in a separate group.")
			.group("Example Group")
			.withTarget()
				.method(commandsPojo, "secondCommandInGroup")
				.and()
			.build();
		commandCatalog.register(registration5);
	}

	@Configuration
	static class Config {

		@Bean
		public CommandCatalog commandCatalog() {
			return CommandCatalog.of();
		}

		@Bean
		public Help help() {
			ThemeRegistry registry = new ThemeRegistry();
			registry.register(Theme.of("default", ThemeSettings.defaults()));
			ThemeResolver resolver = new ThemeResolver(registry, "default");
			TemplateExecutor executor = new TemplateExecutor(resolver);
			Help help = new Help(executor);
			help.setCommandTemplate("classpath:template/help-command-default.stg");
			help.setCommandsTemplate("classpath:template/help-commands-default.stg");
			return help;
		}
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
