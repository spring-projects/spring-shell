/*
 * Copyright 2021-2022 the original author or authors.
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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.jline.reader.Completer;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.shell.command.CommandCatalog;

@Configuration(proxyBeanMethods = false)
public class LineReaderAutoConfiguration {

	private Terminal terminal;

	private Completer completer;

	private Parser parser;

	private CommandCatalog commandRegistry;

	private org.jline.reader.History jLineHistory;

	@Value("${spring.application.name:spring-shell}.log")
	private String historyPath;

	public LineReaderAutoConfiguration(Terminal terminal, Completer completer, Parser parser,
			CommandCatalog commandRegistry, org.jline.reader.History jLineHistory) {
		this.terminal = terminal;
		this.completer = completer;
		this.parser = parser;
		this.commandRegistry = commandRegistry;
		this.jLineHistory = jLineHistory;
	}

	@EventListener
	public void onContextClosedEvent(ContextClosedEvent event) throws IOException {
		jLineHistory.save();
	}

	@Bean
	public LineReader lineReader() {
		LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder()
				.terminal(terminal)
				.appName("Spring Shell")
				.completer(completer)
				.history(jLineHistory)
				.highlighter(new Highlighter() {

					@Override
					public AttributedString highlight(LineReader reader, String buffer) {
						int l = 0;
						String best = null;
						for (String command : commandRegistry.getRegistrations().keySet()) {
							if (buffer.startsWith(command) && command.length() > l) {
								l = command.length();
								best = command;
							}
						}
						if (best != null) {
							return new AttributedStringBuilder(buffer.length()).append(best, AttributedStyle.BOLD).append(buffer.substring(l)).toAttributedString();
						}
						else {
							return new AttributedString(buffer, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
						}
					}

					@Override
					public void setErrorPattern(Pattern errorPattern) {
					}

					@Override
					public void setErrorIndex(int errorIndex) {
					}
				})
				.parser(parser);

		LineReader lineReader = lineReaderBuilder.build();
		lineReader.setVariable(LineReader.HISTORY_FILE, Paths.get(historyPath));
		lineReader.unsetOpt(LineReader.Option.INSERT_TAB); // This allows completion on an empty buffer, rather than inserting a tab
		jLineHistory.attach(lineReader);
		return lineReader;
	}

}
