/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.command;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.shell.command.CommandExecution.CommandParserExceptionsException;
import org.springframework.shell.command.CommandParser.CommandParserException;
import org.springframework.shell.command.CommandParser.MissingOptionException;

import static org.assertj.core.api.Assertions.assertThat;

class CommandParserExceptionResolverTests {

	private final CommandParserExceptionResolver resolver = new CommandParserExceptionResolver();

	@Test
	void resolvesMissingLongOption() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("required-value")
			.withOption()
				.longNames("arg1")
				.description("Desc arg1")
				.required()
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();

		CommandHandlingResult resolve = resolver.resolve(of(registration.getOptions().get(0)));
		assertThat(resolve).isNotNull();
		assertThat(resolve.message()).contains("--arg1", "Desc arg1");
	}

	@Test
	void resolvesMissingLongOptionWhenAlsoShort() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("required-value")
			.withOption()
				.longNames("arg1")
				.shortNames('x')
				.description("Desc arg1")
				.required()
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();

		CommandHandlingResult resolve = resolver.resolve(of(registration.getOptions().get(0)));
		assertThat(resolve).isNotNull();
		assertThat(resolve.message()).contains("--arg1", "Desc arg1");
		assertThat(resolve.message()).doesNotContain("-x", "Desc x");
	}

	@Test
	void resolvesMissingShortOption() {
		CommandRegistration registration = CommandRegistration.builder()
			.command("required-value")
			.withOption()
				.shortNames('x')
				.description("Desc x")
				.required()
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();

		CommandHandlingResult resolve = resolver.resolve(of(registration.getOptions().get(0)));
		assertThat(resolve).isNotNull();
		assertThat(resolve.message()).contains("-x", "Desc x");
	}

	static CommandParserExceptionsException of(CommandOption option) {
		MissingOptionException moe = new MissingOptionException("msg", option);
		List<CommandParserException> parserExceptions = Arrays.asList(moe);
		return new CommandParserExceptionsException("msg", parserExceptions);
	}
}
