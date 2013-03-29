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

import static org.springframework.shell.support.util.OsUtils.LINE_SEPARATOR;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import jline.ANSIBuffer;
import jline.ConsoleReader;

import org.springframework.shell.support.util.IOUtils;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.util.Assert;

/**
 * JDK logging {@link Handler} that emits log messages to a JLine {@link ConsoleReader}.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class JLineLogHandler extends Handler {

	// Constants
	private static final boolean ROO_BRIGHT_COLORS = Boolean.getBoolean("roo.bright");
	private static final boolean SHELL_BRIGHT_COLORS = Boolean.getBoolean("spring.shell.bright");
	private static final boolean BRIGHT_COLORS = ROO_BRIGHT_COLORS || SHELL_BRIGHT_COLORS;


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
		this.ansiSupported = reader.getTerminal().isANSISupported();

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
					} catch (Exception ex) {
					} finally {
						IOUtils.closeQuietly(pw);
					}
				}
				return sb.toString();
			}
		});
	}

	@Override
	public void flush() {}

	@Override
	public void close() throws SecurityException {}

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

			StringBuffer buffer = reader.getCursorBuffer().getBuffer();
			int cursor = reader.getCursorBuffer().cursor;
			if (reader.getCursorBuffer().length() > 0) {
				// The user has semi-typed something, so put a new line in so the debug message is separated
				reader.printNewline();

				// We need to cancel whatever they typed (it's reset later on), so the line appears empty
				reader.getCursorBuffer().setBuffer(new StringBuffer());
				reader.getCursorBuffer().cursor = 0;
			}

			// This ensures nothing is ever displayed when redrawing the line
			reader.setDefaultPrompt("");
			reader.redrawLine();
			// Now restore the line formatting settings back to their original
			reader.setDefaultPrompt(shellPromptAccessor.getShellPrompt());

			reader.getCursorBuffer().setBuffer(buffer);
			reader.getCursorBuffer().cursor = cursor;

			reader.printString(toDisplay);

			Boolean prohibitingRedraw = redrawProhibit.get();
			if (prohibitingRedraw == null) {
				reader.redrawLine();
			}

			reader.flushConsole();
		} catch (Exception e) {
			reportError("Could not publish log message", e, Level.SEVERE.intValue());
		}
	}

	private String toDisplay(final LogRecord event) {
		StringBuilder sb = new StringBuilder();

		String threadName;
		String eventString;
		if (includeThreadName && !userInterfaceThreadName.equals(Thread.currentThread().getName()) && !"".equals(Thread.currentThread().getName())) {
			threadName = "[" + Thread.currentThread().getName() + "]";

			// Build an event string that will indent nicely given the left hand side now contains a thread name
			StringBuilder lineSeparatorAndIndentingString = new StringBuilder();
			for (int i = 0; i <= threadName.length(); i++) {
				lineSeparatorAndIndentingString.append(" ");
			}

			eventString = " " + getFormatter().format(event).replace(LINE_SEPARATOR, LINE_SEPARATOR + lineSeparatorAndIndentingString.toString());
			if (eventString.endsWith(lineSeparatorAndIndentingString.toString())) {
				eventString = eventString.substring(0, eventString.length() - lineSeparatorAndIndentingString.length());
			}
		} else {
			threadName = "";
			eventString = getFormatter().format(event);
		}

		if (ansiSupported) {
			if (event.getLevel().intValue() >= Level.SEVERE.intValue()) {
				sb.append(getANSIBuffer().reverse(threadName).red(eventString));
			} else if (event.getLevel().intValue() >= Level.WARNING.intValue()) {
				sb.append(getANSIBuffer().reverse(threadName).magenta(eventString));
			} else if (event.getLevel().intValue() >= Level.INFO.intValue()) {
				sb.append(getANSIBuffer().reverse(threadName).green(eventString));
			} else {
				sb.append(getANSIBuffer().reverse(threadName).append(eventString));
			}
		} else {
			sb.append(threadName).append(eventString);
		}

		return sb.toString();
	}

	/**
	 * Makes text brighter if requested through system property 'roo.bright' and
	 * works around issue on Windows in using reverse() in combination with the
	 * Jansi lib, which leaves its 'negative' flag set unless reset explicitly.
	 *
	 * @return new patched ANSIBuffer
	 */
	public static ANSIBuffer getANSIBuffer() {
		final char esc = (char) 27;
		return new ANSIBuffer() {
			@Override
			public ANSIBuffer reverse(final String str) {
				if (OsUtils.isWindows()) {
					return super.reverse(str).append(ANSICodes.attrib(esc));
				}
				return super.reverse(str);
			};
			@Override
			public ANSIBuffer attrib(final String str, final int code) {
				if (BRIGHT_COLORS && 30 <= code && code <= 37) {
					// This is a color code: add a 'bright' code
					return append(esc + "[" + code + ";1m").append(str).append(ANSICodes.attrib(0));
				}
				return super.attrib(str, code);
			}
		};
	}
}
