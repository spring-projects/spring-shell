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
package org.springframework.shell.support.util;

/**
 * ANSI escape codes supported by JLine
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public enum AnsiEscapeCode {

	// These int literals are non-public constants in ANSIBuffer.ANSICodes
	BLINK(5),
	BOLD(1),
	CONCEALED(8),
	FG_BLACK(30),
	FG_BLUE(34),
	FG_CYAN(36),
	FG_GREEN(32),
	FG_MAGENTA(35),
	FG_RED(31),
	FG_YELLOW(33),
	FG_WHITE(37),
	OFF(0),
	REVERSE(7),
	UNDERSCORE(4);

	// Constant for the escape character
	private static final boolean ANSI_SUPPORTED = Boolean.getBoolean("roo.console.ansi");
	private static final char ESC = 27;

	/**
	 * Decorates the given text with the given escape codes (turning them off
	 * afterwards)
	 *
	 * @param text the text to decorate; can be <code>null</code>
	 * @param codes
	 * @return <code>null</code> if <code>null</code> is passed
	 */
	public static String decorate(final String text, final AnsiEscapeCode... codes) {
		if (text == null || "".equals(text)) {
			return text;
		}

		final StringBuilder sb = new StringBuilder();
		if (ANSI_SUPPORTED) {
			for (final AnsiEscapeCode code : codes) {
				sb.append(code.code);
			}
		}
		sb.append(text);
		if (codes != null && codes.length > 0 && ANSI_SUPPORTED) {
			sb.append(OFF.code);
		}
		return sb.toString();
	}

	// Fields
	final String code;

	/**
	 * Constructor
	 *
	 * @param code the numeric ANSI escape code
	 */
	private AnsiEscapeCode(final int code) {
		// Copied from the method ANSIBuffer.ANSICodes#attrib(int)
		this.code = ESC + "[" + code + "m";
	}
}
