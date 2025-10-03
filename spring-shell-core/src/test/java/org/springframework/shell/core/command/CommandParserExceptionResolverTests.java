/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.core.command;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.command.CommandExecution.CommandParserExceptionsException;
import org.springframework.shell.core.command.CommandHandlingResult;
import org.springframework.shell.core.command.CommandParser.CommandParserException;
import org.springframework.shell.core.command.CommandParserExceptionResolver;

import static org.assertj.core.api.Assertions.assertThat;

class CommandParserExceptionResolverTests {

	private final CommandParserExceptionResolver resolver = new CommandParserExceptionResolver();

	@Test
	void resolvesCommandParserException() {
		CommandHandlingResult resolve = resolver.resolve(genericParserException());
		assertThat(resolve).isNotNull();
		assertThat(resolve.message()).contains("hi");
	}

	static CommandParserExceptionsException genericParserException() {
		CommandParserException e = new CommandParserException("hi");
		List<CommandParserException> parserExceptions = List.of(e);
		return new CommandParserExceptionsException("msg", parserExceptions);
	}

}
