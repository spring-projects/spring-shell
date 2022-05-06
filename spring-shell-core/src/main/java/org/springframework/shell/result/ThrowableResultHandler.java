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

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.jline.InteractiveShellRunner;
import org.springframework.util.StringUtils;

/**
 * A {@link ResultHandler} that prints thrown exceptions messages in red.
 *
 * <p>Also stores the last exception reported, so that details can be printed using a dedicated command.</p>
 *
 * @author Eric Bottard
 */
public class ThrowableResultHandler extends TerminalAwareResultHandler<Throwable> {

	/**
	 * The name of the command that may be used to print details about the last error.
	 */
	public static final String DETAILS_COMMAND_NAME = "stacktrace";

	private Throwable lastError;

	private CommandCatalog commandCatalog;

	private ObjectProvider<InteractiveShellRunner> interactiveRunner;

	public ThrowableResultHandler(Terminal terminal, CommandCatalog commandCatalog,
			ObjectProvider<InteractiveShellRunner> interactiveRunner) {
		super(terminal);
		this.commandCatalog = commandCatalog;
		this.interactiveRunner = interactiveRunner;
	}

	@Override
	protected void doHandleResult(Throwable result) {
		lastError = result;
		String toPrint = StringUtils.hasLength(result.getMessage()) ? result.getMessage() : result.toString();
		terminal.writer().println(new AttributedString(toPrint,
				AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi());
		if (interactiveRunner.getIfAvailable() != null && commandCatalog.getRegistrations().keySet().contains(DETAILS_COMMAND_NAME)) {
			terminal.writer().println(
				new AttributedStringBuilder()
					.append("Details of the error have been omitted. You can use the ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
					.append(DETAILS_COMMAND_NAME, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED).bold())
					.append(" command to print the full stacktrace.", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
					.toAnsi()
			);
		}
		terminal.writer().flush();
		if (interactiveRunner.getIfAvailable() == null) {
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
}
