package org.springframework.shell.jline;

import java.util.Collections;
import java.util.List;

import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Colors;
import org.junit.jupiter.api.Test;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.jline.tui.style.ThemeResolver;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Piotr Olaszewski
 */
class CommandHighlighterTest {

	private final LineReader lineReader = mock(LineReader.class);

	private final CommandRegistry commandRegistry = new CommandRegistry();

	private final CommandHighlighter highlighter = new CommandHighlighter(commandRegistry);

	@Test
	void shouldHighlightCommandNameInBold() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("help");
		when(command.getAliases()).thenReturn(Collections.emptyList());

		commandRegistry.registerCommand(command);

		AttributedString result = highlighter.highlight(lineReader, "help argument");

		assertThat(result).hasToString("help argument");
		assertThat(result.styleAt(0).getStyle()).isEqualTo(AttributedStyle.BOLD.getStyle());
		assertThat(result.styleAt(5).getStyle()).isNotEqualTo(AttributedStyle.BOLD.getStyle());
	}

	@Test
	void shouldHighlightAliasInBold() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("help");
		when(command.getAliases()).thenReturn(List.of("h"));

		commandRegistry.registerCommand(command);

		AttributedString result = highlighter.highlight(lineReader, "h argument");

		assertThat(result).hasToString("h argument");
		assertThat(result.styleAt(0).getStyle()).isEqualTo(AttributedStyle.BOLD.getStyle());
		assertThat(result.styleAt(2).getStyle()).isNotEqualTo(AttributedStyle.BOLD.getStyle());
	}

	@Test
	void shouldHighlightRedWhenNoMatchFound() {
		AttributedString result = highlighter.highlight(lineReader, "unknown");

		assertThat(result).hasToString("unknown");
		assertThat(ThemeResolver.resolveValues(result.styleAt(0)).foreground())
			.isEqualTo(Colors.DEFAULT_COLORS_256[AttributedStyle.RED]);
	}

}