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

import java.nio.file.Paths;

import org.jline.reader.Completer;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.config.UserConfigPathProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LineReaderAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(LineReaderAutoConfiguration.class));

	@Test
	public void testLineReaderCreated() {
		this.contextRunner
				.withUserConfiguration(MockConfiguration.class)
				.run(context -> {
					assertThat(context).hasSingleBean(LineReader.class);
					LineReader lineReader = context.getBean(LineReader.class);
					assertThat(lineReader.getVariable(LineReader.HISTORY_FILE)).asString().contains("spring-shell.log");
				});
	}

	@Test
	public void testLineReaderCreatedNoHistoryFile() {
		this.contextRunner
				.withUserConfiguration(MockConfiguration.class)
				.withPropertyValues("spring.shell.history.enabled=false")
				.run(context -> {
					assertThat(context).hasSingleBean(LineReader.class);
					LineReader lineReader = context.getBean(LineReader.class);
					assertThat(lineReader.getVariable(LineReader.HISTORY_FILE)).isNull();
				});
	}

	@Test
	public void testLineReaderCreatedCustomHistoryFile() {
		this.contextRunner
				.withUserConfiguration(MockConfiguration.class)
				.withPropertyValues("spring.shell.history.name=fakehistory.txt")
				.run(context -> {
					assertThat(context).hasSingleBean(LineReader.class);
					LineReader lineReader = context.getBean(LineReader.class);
					assertThat(lineReader.getVariable(LineReader.HISTORY_FILE)).asString().contains("fakehistory.txt");
				});
	}

	@Configuration(proxyBeanMethods = false)
	static class MockConfiguration {

		@Bean
		Terminal mockTerminal() {
			Terminal terminal = mock(Terminal.class);
			when(terminal.getBufferSize()).thenReturn(new Size());
			return terminal;
		}

		@Bean
		Completer mockCompleter() {
			return mock(Completer.class);
		}

		@Bean
		Parser mockParser() {
			return mock(Parser.class);
		}

		@Bean
		CommandCatalog mockCommandCatalog() {
			return mock(CommandCatalog.class);
		}

		@Bean
		History mockHistory() {
			return mock(History.class);
		}

		@Bean
		UserConfigPathProvider mockUserConfigPathProvider() {
			return () -> Paths.get("mockpath");
		}
	}
}
