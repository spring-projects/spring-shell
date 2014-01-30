/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.springframework.shell.support.util.OsUtils.LINE_SEPARATOR;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import jline.console.ConsoleReader;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.springframework.shell.support.util.IOUtils;
import org.springframework.util.Assert;

/**
 * JDK logging {@link Handler} that emits log messages to a JLine {@link ConsoleReader}.
 * 
 * @author Ben Alex
 * @since 1.0
 */
public class JLineLogHandler extends Handler {

	// Fields
	private ConsoleReader reader;

	private ShellPromptAccessor shellPromptAccessor;

	private static ThreadLocal<Boolean> redrawProhibit = new ThreadLocal<Boolean>();

	private static String lastMessage;

	private static boolean includeThreadName = false;

	private boolean ansiSupported;

	private String userInterfaceThreadName;

	private static boolean suppressDuplicateMessages = true;

	public JLineLogHandler(final ConsoleReader reader, final ShellPromptAccessor shellPromptAccessor) {
		Assert.notNull(reader, "Console reader required");
		Assert.notNull(shellPromptAccessor, "Shell prompt accessor required");
		this.reader = reader;
		this.shellPromptAccessor = shellPromptAccessor;
		this.userInterfaceThreadName = Thread.currentThread().getName();
		this.ansiSupported = reader.getTerminal().isAnsiSupported();

		setFormatter(new Formatter() {
			@Override
			public String format(final LogRecord record) {
				StringBuffer sb = new StringBuffer();
				if (record.getMessage() != null) {
					sb.append(record.getMessage()).append(LINE_SEPARATOR);
				}
				if (record.getThrown() != null) {
					PrintWriter pw = null;
					try {
						StringWriter sw = new StringWriter();
						pw = new PrintWriter(sw);
						record.getThrown().printStackTrace(pw);
						sb.append(sw.toString());
					}
					catch (Exception ex) {
					}
					finally {
						IOUtils.closeQuietly(pw);
					}
				}
				return sb.toString();
			}
		});
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}

	public static void prohibitRedraw() {
		redrawProhibit.set(true);
	}

	public static void cancelRedrawProhibition() {
		redrawProhibit.remove();
	}

	public static void setIncludeThreadName(final boolean include) {
		includeThreadName = include;
	}

	public static void resetMessageTracking() {
		lastMessage = null; // see ROO-251
	}

	public static boolean isSuppressDuplicateMessages() {
		return suppressDuplicateMessages;
	}

	public static void setSuppressDuplicateMessages(final boolean suppressDuplicateMessages) {
		JLineLogHandler.suppressDuplicateMessages = suppressDuplicateMessages;
	}

	@Override
	public void publish(final LogRecord record) {
		try {
			// Avoid repeating the same message that displayed immediately before the current message (ROO-30, ROO-1873)
			String toDisplay = toDisplay(record);
			if (toDisplay.equals(lastMessage) && suppressDuplicateMessages) {
				return;
			}
			lastMessage = toDisplay;

			StringBuilder buffer = reader.getCursorBuffer().copy().buffer;
			int cursor = reader.getCursorBuffer().cursor;
			if (reader.getCursorBuffer().length() > 0) {
				// The user has semi-typed something, so put a new line in so the debug message is separated
				reader.println();

				// We need to cancel whatever they typed (it's reset later on), so the line appears empty
				reader.getCursorBuffer().clear();
			}

			// This ensures nothing is ever displayed when redrawing the line
			reader.setPrompt("");
			reader.redrawLine();
			// Now restore the line formatting settings back to their original
			reader.setPrompt(shellPromptAccessor.getShellPrompt());

			reader.getCursorBuffer().write(buffer.toString());
			reader.getCursorBuffer().cursor = cursor;

			reader.print(toDisplay);

			Boolean prohibitingRedraw = redrawProhibit.get();
			if (prohibitingRedraw == null) {
				reader.redrawLine();
			}

			reader.flush();
		}
		catch (Exception e) {
			reportError("Could not publish log message", e, Level.SEVERE.intValue());
		}
	}

	private String toDisplay(final LogRecord event) {
		StringBuilder sb = new StringBuilder();

		String threadName;
		String eventString;
		if (includeThreadName && !userInterfaceThreadName.equals(Thread.currentThread().getName())
				&& !"".equals(Thread.currentThread().getName())) {
			threadName = "[" + Thread.currentThread().getName() + "]";

			// Build an event string that will indent nicely given the left hand side now contains a thread name
			StringBuilder lineSeparatorAndIndentingString = new StringBuilder();
			for (int i = 0; i <= threadName.length(); i++) {
				lineSeparatorAndIndentingString.append(" ");
			}

			eventString = " "
					+ getFormatter().format(event).replace(LINE_SEPARATOR,
							LINE_SEPARATOR + lineSeparatorAndIndentingString.toString());
			if (eventString.endsWith(lineSeparatorAndIndentingString.toString())) {
				eventString = eventString.substring(0, eventString.length() - lineSeparatorAndIndentingString.length());
			}
		}
		else {
			threadName = "";
			eventString = getFormatter().format(event);
		}

		if (ansiSupported) {
			Ansi ansi = ansi(sb);
			if (event.getLevel().intValue() >= Level.SEVERE.intValue()) {
				ansi.a(Attribute.NEGATIVE_ON).a(threadName).a(Attribute.NEGATIVE_OFF).fg(RED).a(eventString).reset();
			}
			else if (event.getLevel().intValue() >= Level.WARNING.intValue()) {
				ansi.a(Attribute.NEGATIVE_ON).a(threadName).a(Attribute.NEGATIVE_OFF).fg(MAGENTA).a(eventString)
						.reset();
			}
			else if (event.getLevel().intValue() >= Level.INFO.intValue()) {
				ansi.a(Attribute.NEGATIVE_ON).a(threadName).a(Attribute.NEGATIVE_OFF).fg(GREEN).a(eventString).reset();
			}
			else {
				ansi.a(Attribute.NEGATIVE_ON).a(threadName).a(Attribute.NEGATIVE_OFF).a(eventString);
			}

		}
		else {
			sb.append(threadName).append(eventString);
		}

		return sb.toString();
	}

}
