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

/**
 * Encapsulating {@link ParserMessage} with position and {@code inserts}.
 *
 * @author Janne Valkealahti
 */
public record MessageResult(ParserMessage parserMessage, int position, Object[] inserts) {

	/**
	 * Constructs {@code MessageResult} with parser message, position and inserts.
	 *
	 * @param parserMessage the parser message
	 * @param position the position
	 * @param inserts the inserts
	 * @return a message result
	 */
	public static MessageResult of(ParserMessage parserMessage, int position, Object... inserts) {
		return new MessageResult(parserMessage, position, inserts);
	}

	/**
	 * Gets a formatted message using position and inserts.
	 *
	 * @return a formatted message
	 */
	public String getMessage() {
		return parserMessage.formatMessage(inserts);
	}
}
