/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.command.parser;

import java.text.MessageFormat;

/**
 * Contains all the messages that can be produced during parsing. Each message
 * has a kind (WARNING, ERROR) and a code number. Code is used to identify
 * particular message and makes it easier to test what messages are produced.
 * Code numbers are split so that ones within {@code 1xxx} are from lexer
 * and {@code 2xxx} from parser.
 *
 * Messages with {@code ERROR} should be treated as terminating messages because
 * those are most likely hard errors based on manual validation or exception
 * thrown within lexing or parsing.
 *
 * Messages with {@code WARNING} can be ignored but can be used to provide
 * info to user. For example parsing may detect some ambiguities with a command
 * and option model related to what user tries to use as an input. This
 * because there are limits how clever a parser can be as command model
 * is beyond its control.
 *
 * @author Janne Valkealahti
 */
public enum ParserMessage {

	ILLEGAL_CONTENT_BEFORE_COMMANDS(Type.ERROR, 1000, "Illegal content before commands ''{0}''"),
	MANDATORY_OPTION_MISSING(Type.ERROR, 2000, "Missing mandatory option ''{0}''{1}"),
	UNRECOGNISED_OPTION(Type.ERROR, 2001, "Unrecognised option ''{0}''"),
	ILLEGAL_OPTION_VALUE(Type.ERROR, 2002, "Illegal option value ''{0}'', reason ''{1}''"),
	NOT_ENOUGH_OPTION_ARGUMENTS(Type.ERROR, 2003, "Not enough arguments for option ''{0}'', requires at least ''{1}''"),
	TOO_MANY_OPTION_ARGUMENTS(Type.ERROR, 2004, "Too many arguments for option ''{0}'', requires at most ''{1}''")
	;

	private Type type;
	private int code;
	private String message;

	ParserMessage(Type type, int code, String message) {
		this.type = type;
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public Type getType() {
		return type;
	}

	/**
	 * Format message without code and position parts.
	 *
	 * @param inserts the inserts
	 * @return formatted message
	 */
	public String formatMessage(Object... inserts) {
		return formatMessage(false, -1, inserts);
	}

	/**
	 * Format message.
	 *
	 * <p>For example code and position 2000E:(pos 0):
	 *
	 * @param useCode Add code part
	 * @param position position info, not printed if negative
	 * @param inserts the inserts
	 * @return formatted message
	 */
	public String formatMessage(boolean useCode, int position, Object... inserts) {
		StringBuilder msg = new StringBuilder();
		if (useCode) {
			msg.append(code);
			switch (type) {
				case WARNING:
					msg.append("W");
					break;
				case ERROR:
					msg.append("E");
					break;
			}
			msg.append(":");
		}
		if (position != -1) {
			msg.append("(pos ").append(position).append("): ");
		}
		msg.append(MessageFormat.format(message, inserts));
		return msg.toString();
	}

	public enum Type {
		WARNING,
		ERROR
	}
}
