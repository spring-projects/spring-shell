/*
 * Copyright 2022-present the original author or authors.
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
import java.nio.charset.StandardCharsets;
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
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.CommandRegistration;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.tui.style.TemplateExecutor;
import org.springframework.shell.tui.style.Theme;
import org.springframework.shell.tui.style.ThemeRegistry;
import org.springframework.shell.tui.style.ThemeResolver;
import org.springframework.shell.tui.style.ThemeSettings;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HelpTests.Config.class)
class HelpTests {

	private static Locale previousLocale;
	private String testName;
	private final CommandsPojo commandsPojo = new CommandsPojo();

	@Autowired
	private CommandRegistry commandRegistry;

	@Autowired
	private Help help;

	@BeforeAll
	static void setAssumedLocale() {
		previousLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	@AfterAll
	static void restorePreviousLocale() {
		Locale.setDefault(previousLocale);
	}

	@BeforeEach
	void setup(TestInfo testInfo) {
		Optional<Method> testMethod = testInfo.getTestMethod();
		testMethod.ifPresent(method -> this.testName = method.getName());
		Collection<CommandRegistration> regs = this.commandRegistry.getRegistrations().values();
		regs.forEach(r -> this.commandRegistry.unregister(r));
	}

	@Test
	void testCommandHelp() throws Exception {
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
		commandRegistry.register(registration);
		String help = this.help.help(new String[] { "first-command" }).toString();
		help = removeNewLines(help);
		assertThat(help).isEqualTo(sample());
	}

	@Test
	void testCommandListDefault() throws Exception {
		registerCommandListCommands();
		String list = this.help.help(null).toString();
		list = removeNewLines(list);
		assertThat(list).isEqualTo(sample());
	}

	@Test
	void testCommandListFlat() throws Exception {
		registerCommandListCommands();
		this.help.setShowGroups(false);
		String list = this.help.help(null).toString();
		list = removeNewLines(list);
		assertThat(list).isEqualTo(sample());
	}

	@Test
	void testUnknownCommand() {
		assertThatThrownBy(() -> this.help.help(new String[] { "some", "unknown", "command" }))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private String removeNewLines(String str) {
		return str.replace("\r", "").replace("\n", "");
	}

	private String sample() throws IOException {
		InputStream is = new ClassPathResource(HelpTests.class.getSimpleName() + "-" + testName + ".txt", HelpTests.class).getInputStream();
		return removeNewLines(FileCopyUtils.copyToString(new InputStreamReader(is, StandardCharsets.UTF_8)));
	}

	private void registerCommandListCommands() {
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
		commandRegistry.register(registration1);

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
		commandRegistry.register(registration2);

		CommandRegistration registration3 = CommandRegistration.builder()
			.command("third-command")
			.description("The last command.")
			.withTarget()
				.method(commandsPojo, "thirdCommand")
				.and()
			.build();
		commandRegistry.register(registration3);

		CommandRegistration registration4 = CommandRegistration.builder()
			.command("first-group-command")
			.description("The first command in a separate group.")
			.group("Example Group")
			.withTarget()
				.method(commandsPojo, "firstCommandInGroup")
				.and()
			.build();
		commandRegistry.register(registration4);

		CommandRegistration registration5 = CommandRegistration.builder()
			.command("second-group-command")
			.description("The second command in a separate group.")
			.group("Example Group")
			.withTarget()
				.method(commandsPojo, "secondCommandInGroup")
				.and()
			.build();
		commandRegistry.register(registration5);
	}

	@Configuration
	static class Config {

		@Bean
		public CommandRegistry commandRegistry() {
			return CommandRegistry.of();
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

	@Command
	static class CommandsPojo {

		@Command // FIXME how to migrate (prefix = "--")
		public void firstCommand(
				// Single key and arity = 0. Help displayed on same line
				@Option(description = "Whether to delete recursively", arity = CommandRegistration.OptionArity.ZERO, shortNames = {'r'}) boolean r,
				// Multiple keys and arity 0. Help displayed on next line
				@Option(description = "Do not ask for confirmation. YOLO", arity = CommandRegistration.OptionArity.ZERO, shortNames = {'f'}, longNames = {"force"}) boolean force,
				// Single key, arity >= 1. Help displayed on next line. Optional
				// Also, bears bean validation annotation
				@Option(description = "The answer to everything", defaultValue = "42", shortNames = {'n'}) @Max(5) int n,
		        // Single key, arity > 1.
		        @Option(description = "Some other parameters", arity = CommandRegistration.OptionArity.ONE_OR_MORE, shortNames = {'o'}) float[] o
		) {
		}

		@Command
		public void secondCommand() {
		}

		@Command
		public void thirdCommand() {
		}

		@Command
		public void firstCommandInGroup() {
		}

		@Command
		public void secondCommandInGroup() {
		}
	}
}
