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

public class Token {

	private final static int IMPLICIT_POSITION = -1;
	private final String value;
	private final TokenType type;
	private final int position;

	public Token(String value, TokenType type) {
		this(value, type, IMPLICIT_POSITION);
	}

	public Token(String value, TokenType type, int position) {
		this.value = value;
		this.type = type;
		this.position = position;
	}

	public static Token of(String value, TokenType type) {
		return new Token(value, type);
	}

	public static Token of(String value, TokenType type, int position) {
		return new Token(value, type, position);
	}

	public String getValue() {
		return value;
	}

	public TokenType getType() {
		return type;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", position=" + position + ", value=" + value + "]";
	}
}
