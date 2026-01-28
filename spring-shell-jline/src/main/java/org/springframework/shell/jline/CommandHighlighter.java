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
package org.springframework.shell.jline;

import java.util.Comparator;
import java.util.stream.Stream;

import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.shell.core.command.CommandRegistry;

/**
 * @author Piotr Olaszewski
 * @since 4.0.1
 */
public class CommandHighlighter implements Highlighter {

	private final CommandRegistry commandRegistry;

	/**
	 * Create a new {@link CommandHighlighter} instance.
	 * @param commandRegistry the command registry
	 */
	public CommandHighlighter(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Override
	public AttributedString highlight(LineReader reader, String buffer) {
		return this.commandRegistry.getCommands()
			.stream()
			.flatMap(command -> Stream.concat(Stream.of(command.getName()), command.getAliases().stream()))
			.filter(buffer::startsWith)
			.max(Comparator.comparingInt(String::length))
			.map(bestMatch -> new AttributedStringBuilder(buffer.length()).append(bestMatch, AttributedStyle.BOLD)
				.append(buffer.substring(bestMatch.length()))
				.toAttributedString())
			.orElseGet(() -> new AttributedString(buffer, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)));
	}

}
