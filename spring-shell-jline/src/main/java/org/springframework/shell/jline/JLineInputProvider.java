package org.springframework.shell.jline;

import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import org.springframework.shell.core.InputProvider;

public class JLineInputProvider implements InputProvider {

	private final LineReader lineReader;

	private PromptProvider promptProvider = () -> new AttributedString("shell:>",
			AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));

	/**
	 * Create a new {@link JLineInputProvider} instance.
	 * @param lineReader the JLine line reader
	 */
	public JLineInputProvider(LineReader lineReader) {
		this.lineReader = lineReader;
	}

	@Override
	public String readInput() {
		AttributedString prompt = this.promptProvider.getPrompt();
		String ansiPrompt = prompt.toAnsi(this.lineReader.getTerminal());
		return this.lineReader.readLine(ansiPrompt);
	}

	public void setPromptProvider(PromptProvider promptProvider) {
		this.promptProvider = promptProvider;
	}

	public LineReader getLineReader() {
		return this.lineReader;
	}

}