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

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenAssert extends AbstractAssert<TokenAssert, Token> {

	public TokenAssert(Token actual) {
		super(actual, TokenAssert.class);
	}

	public TokenAssert isType(TokenType type) {
		isNotNull();
		assertThat(actual.getType()).isEqualTo(type);
		return this;
	}

	public TokenAssert hasPosition(int position) {
		isNotNull();
		assertThat(actual.getPosition()).isEqualTo(position);
		return this;
	}

	public TokenAssert hasValue(String value) {
		isNotNull();
		assertThat(actual.getValue()).isEqualTo(value);
		return this;
	}
}
