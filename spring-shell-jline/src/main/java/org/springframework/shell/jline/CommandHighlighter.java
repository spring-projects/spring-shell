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

	public CommandHighlighter(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Override
	public AttributedString highlight(LineReader reader, String buffer) {
		return commandRegistry.getCommands()
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
