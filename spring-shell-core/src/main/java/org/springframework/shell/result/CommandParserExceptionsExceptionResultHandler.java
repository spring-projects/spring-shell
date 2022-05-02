package org.springframework.shell.result;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.shell.command.CommandExecution.CommandParserExceptionsException;;

/**
 * Displays command parsing errors on the terminal.
 *
 * @author Janne Valkealahti
 */
public class CommandParserExceptionsExceptionResultHandler extends TerminalAwareResultHandler<CommandParserExceptionsException> {

	public CommandParserExceptionsExceptionResultHandler(Terminal terminal) {
		super(terminal);
	}

	@Override
	protected void doHandleResult(CommandParserExceptionsException result) {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		result.getParserExceptions().stream().forEach(e -> {
			builder.append(new AttributedString(e.getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)));
			builder.append("\n");
		});
		terminal.writer().append(builder.toAnsi());
	}
}
