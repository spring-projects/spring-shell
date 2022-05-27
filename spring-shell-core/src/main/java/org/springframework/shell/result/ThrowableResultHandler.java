/*
 * Copyright 2015-2022 the original author or authors.
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
package org.springframework.shell.result;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;
import org.springframework.shell.jline.InteractiveShellRunner;
import org.springframework.util.StringUtils;

/**
 * A {@link ResultHandler} that prints thrown exceptions messages in red.
 *
 * Stores the last exception reported, so that details can be printed using a
 * dedicated command if in interactive mode. Prints stacktrace if in
 * non-interactive mode as dedicated command could not be used.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
public class ThrowableResultHandler extends TerminalAwareResultHandler<Throwable> {

	/**
	 * The name of the command that may be used to print details about the last error.
	 */
	public static final String DETAILS_COMMAND_NAME = "stacktrace";

	private Throwable lastError;

	private CommandCatalog commandCatalog;

	private ObjectProvider<InteractiveShellRunner> interactiveRunner;

	private ShellContext shellContext;

	public ThrowableResultHandler(Terminal terminal, CommandCatalog commandCatalog, ShellContext shellContext,
			ObjectProvider<InteractiveShellRunner> interactiveRunner) {
		super(terminal);
		this.commandCatalog = commandCatalog;
		this.shellContext = shellContext;
		this.interactiveRunner = interactiveRunner;
	}

	@Override
	protected void doHandleResult(Throwable result) {
		lastError = result;
		boolean shouldHandle = shouldHandle();

		if (shouldHandle) {
			String errorMsg = StringUtils.hasLength(result.getMessage()) ? result.getMessage() : result.toString();
			terminal.writer().println(new AttributedString(errorMsg,
				AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi());

			String noteMsg;
			if (showShortError()) {
				noteMsg = new AttributedStringBuilder()
					.append("Details of the error have been omitted. You can use the ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
					.append(DETAILS_COMMAND_NAME, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED).bold())
					.append(" command to print the full stacktrace.", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
					.toAnsi();
			}
			else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				result.printStackTrace(pw);
				String stacktraceStr = sw.toString();
				noteMsg = new AttributedString(stacktraceStr,
						AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi();
			}
			terminal.writer().println(noteMsg);
			terminal.writer().flush();
		}
		else {
			if (result instanceof RuntimeException) {
				throw (RuntimeException) result;
			}
			else if (result instanceof Error) {
				throw (Error) result;
			}
			else {
				throw new RuntimeException((Throwable) result);
			}
		}
	}

	/**
	 * Return the last error that was dealt with by this result handler.
	 */
	public Throwable getLastError() {
		return lastError;
	}

	private boolean shouldHandle() {
		return interactiveRunner.getIfAvailable() != null;
	}

	private boolean showShortError() {
		return commandCatalog.getRegistrations().keySet().contains(DETAILS_COMMAND_NAME)
				&& this.shellContext.getInteractionMode() == InteractionMode.INTERACTIVE;
	}
}
