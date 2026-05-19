/*
 * Copyright 2026-present the original author or authors.
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
package org.springframework.shell.test.autoconfigure;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellScreen;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.test.context.ContextConfiguration;

/**
 * Regression coverage for gh-1352. Mirrors the scenarios from the issue reproducer: an
 * annotation-based command with a custom-typed option, an annotation-based command with a
 * plain String option, and a programmatically-registered command, all running against a
 * context that defines a {@code Converter<String, Message>} bean.
 *
 * @author David Pilar
 */
@ShellTest
@ContextConfiguration(classes = CustomConverterShellTests.App.class)
class CustomConverterShellTests {

	@Test
	void customConverterIsAppliedToAnnotatedCommandOption(@Autowired ShellTestClient client) throws Exception {
		ShellScreen shellScreen = client.sendCommand("custom --msg hello");

		ShellAssertions.assertThat(shellScreen).containsText("hello");
	}

	@Test
	void plainStringOptionStillWorks(@Autowired ShellTestClient client) throws Exception {
		ShellScreen shellScreen = client.sendCommand("simple --msg hello");

		ShellAssertions.assertThat(shellScreen).containsText("hello");
	}

	@Test
	void programmaticCommandStillWorks(@Autowired ShellTestClient client) throws Exception {
		ShellScreen shellScreen = client.sendCommand("programmatic --msg hello");

		ShellAssertions.assertThat(shellScreen).containsText("hello");
	}

	@SpringBootApplication
	static class App {

		@Command(name = "custom")
		public String custom(@Option(longName = "msg", required = true) Message msg) {
			return msg.getText();
		}

		@Command(name = "simple")
		public String simple(@Option(longName = "msg", required = true) String msg) {
			return msg;
		}

		@Bean
		org.springframework.shell.core.command.Command programmatic() {
			Function<CommandContext, String> action = ctx -> ctx.getOptionByName("msg").value();
			return org.springframework.shell.core.command.Command.builder()
				.name("programmatic")
				.options(CommandOption.with().longName("msg").type(Message.class).build())
				.execute(action);
		}

		@Bean
		Converter<String, Message> messageConverter() {
			return source -> {
				Message m = new Message();
				m.setText(source);
				return m;
			};
		}

	}

	static class Message {

		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

}
