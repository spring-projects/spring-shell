package org.springframework.shell.core.jline;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import org.springframework.shell.core.Input;
import org.springframework.shell.core.InputProvider;

public class JLineInputProvider implements InputProvider {

	private final LineReader lineReader;

	private PromptProvider promptProvider = () -> new AttributedString("shell:>",
			AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));

	public JLineInputProvider(LineReader lineReader) {
		this.lineReader = lineReader;
	}

	@Override
	public Input readInput() {
		try {
			AttributedString prompt = promptProvider.getPrompt();
			lineReader.readLine(prompt.toAnsi(lineReader.getTerminal()));
		}
		catch (UserInterruptException e) {
			return Input.INTERRUPTED;
		}
		catch (EndOfFileException e) {
			return Input.EMPTY;
		}
		return new ParsedLineInput(lineReader.getParsedLine());
	}

	public void setPromptProvider(PromptProvider promptProvider) {
		this.promptProvider = promptProvider;
	}

}