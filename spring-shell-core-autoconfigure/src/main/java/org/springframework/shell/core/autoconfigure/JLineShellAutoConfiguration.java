/*
 * Copyright 2017-present the original author or authors.
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.TerminalBuilder.SystemOutput;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.config.UserConfigPathProvider;
import org.springframework.shell.jline.CommandCompleter;
import org.springframework.shell.jline.CommandHighlighter;
import org.springframework.shell.jline.ExtendedDefaultParser;
import org.springframework.shell.jline.JLineInputProvider;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.jline.command.History;
import org.springframework.util.StringUtils;

/**
 * Shell implementation using JLine to capture input and trigger completions.
 *
 * @author Eric Bottard
 * @author Florent Biville
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
@AutoConfiguration
@EnableConfigurationProperties(SpringShellProperties.class)
public class JLineShellAutoConfiguration {

	private final static Log log = LogFactory.getLog(JLineShellAutoConfiguration.class);

	private org.jline.reader.History jLineHistory;

	@Value("${spring.application.name:spring-shell}.log")
	private String fallbackHistoryFileName = "spring-shell.log";

	private SpringShellProperties springShellProperties;

	private UserConfigPathProvider userConfigPathProvider;

	public JLineShellAutoConfiguration(org.jline.reader.History jLineHistory,
			SpringShellProperties springShellProperties, UserConfigPathProvider userConfigPathProvider) {
		this.jLineHistory = jLineHistory;
		this.springShellProperties = springShellProperties;
		this.userConfigPathProvider = userConfigPathProvider;
	}

	@EventListener
	public void onContextClosedEvent(ContextClosedEvent event) throws IOException {
		jLineHistory.save();
	}

	@Bean
	public LineReader lineReader(Terminal terminal, Parser parser, CommandCompleter commandCompleter,
			CommandRegistry commandRegistry) {
		LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder()
			.terminal(terminal)
			.appName("Spring Shell")
			.completer(commandCompleter)
			.history(jLineHistory)
			.highlighter(new CommandHighlighter(commandRegistry))
			.parser(parser);

		LineReader lineReader = lineReaderBuilder.build();
		if (this.springShellProperties.getHistory().isEnabled()) {
			// Discover history location
			Path userConfigPath = this.userConfigPathProvider.provide();
			log.debug("Resolved userConfigPath " + userConfigPath);
			String historyFileName = this.springShellProperties.getHistory().getName();
			if (!StringUtils.hasText(historyFileName)) {
				historyFileName = fallbackHistoryFileName;
			}
			log.debug("Resolved historyFileName " + historyFileName);
			String historyPath = userConfigPath.resolve(historyFileName).toAbsolutePath().toString();
			log.debug("Resolved historyPath " + historyPath);
			// set history file
			lineReader.setVariable(LineReader.HISTORY_FILE, Paths.get(historyPath));
		}
		lineReader.unsetOpt(LineReader.Option.INSERT_TAB); // This allows completion on an
		// empty buffer, rather than
		// inserting a tab
		jLineHistory.attach(lineReader);
		return lineReader;
	}

	@Bean
	public JLineInputProvider inputProvider(LineReader lineReader, PromptProvider promptProvider) {
		JLineInputProvider inputProvider = new JLineInputProvider(lineReader);
		inputProvider.setPromptProvider(promptProvider);
		return inputProvider;
	}

	@Bean(destroyMethod = "close")
	public Terminal terminal(ObjectProvider<TerminalCustomizer> customizers) {
		try {
			TerminalBuilder builder = TerminalBuilder.builder();
			builder.systemOutput(SystemOutput.SysOut);
			customizers.orderedStream().forEach(customizer -> customizer.customize(builder));
			return builder.build();
		}
		catch (IOException e) {
			throw new BeanCreationException("Could not create Terminal", e);
		}
	}

	@Bean
	@ConditionalOnMissingBean(PromptProvider.class)
	public PromptProvider promptProvider() {
		return () -> new AttributedString("shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}

	@Bean
	public Parser parser() {
		ExtendedDefaultParser parser = new ExtendedDefaultParser();
		parser.setEofOnUnclosedQuote(true);
		parser.setEofOnEscapedNewLine(true);
		return parser;
	}

	@Bean
	@ConditionalOnMissingBean
	public CommandCompleter commandCompleter(CommandRegistry commandRegistry) {
		return new CommandCompleter(commandRegistry);
	}

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.history.enabled", havingValue = "true", matchIfMissing = true)
	public Command historyCommand(org.jline.reader.History jLineHistory) {
		return new History(jLineHistory);
	}

	@Configuration
	@ConditionalOnMissingBean(org.jline.reader.History.class)
	public static class JLineHistoryConfiguration {

		@Bean
		public org.jline.reader.History history() {
			return new DefaultHistory();
		}

	}

}
