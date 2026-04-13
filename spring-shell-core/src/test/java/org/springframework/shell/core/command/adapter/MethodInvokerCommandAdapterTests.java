/*
 * Copyright 2025-present the original author or authors.
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
package org.springframework.shell.core.command.adapter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.core.command.CommandArgument;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.annotation.Arguments;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andrey Litvitski
 * @author Mahmoud Ben Hassine
 */
class MethodInvokerCommandAdapterTests {

	static class Target {

		int seen;

		public void run(@Option(longName = "retries", defaultValue = "3") int retries) {
			this.seen = retries;
		}

	}

	static class ArgumentsCommand {

		float base;

		Float[] numbers;

		Float[] moreNumbers;

		@Command(name = "add", description = "Add numbers together", group = "Math commands",
				help = "A command that adds numbers together. Example usage: add --base 10 1 2 3 4 5 6 7 8 9 10")
		public void add(
				@Option(shortName = 'b', longName = "base", description = "the base number to add to") float base,
				@Arguments(arity = 2) Float[] numbers, @Arguments(arity = 3) Float[] moreNumbers) {
			this.base = base;
			this.numbers = numbers;
			this.moreNumbers = moreNumbers;
		}

	}

	@Test
	void optionDefaultValueIsUsedForPrimitiveWhenOptionMissing() throws Exception {
		Target target = new Target();
		Method method = Target.class.getDeclaredMethod("run", int.class);

		DefaultConversionService conversionService = new DefaultConversionService();
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		CommandContext ctx = Mockito.mock(CommandContext.class);
		Mockito.when(ctx.getOptionByLongName("retries")).thenReturn(null);

		StringWriter out = new StringWriter();
		Mockito.when(ctx.outputWriter()).thenReturn(new PrintWriter(out));

		MethodInvokerCommandAdapter adapter = new MethodInvokerCommandAdapter("name", "desc", "group", "help", false,
				method, target, conversionService, validator);

		ExitStatus status = adapter.doExecute(ctx);

		assertThat(status).isEqualTo(ExitStatus.OK);
		assertThat(target.seen).isEqualTo(3);
	}

	@Test
	void argumentsAreParsed() throws Exception {
		// given
		ArgumentsCommand target = new ArgumentsCommand();
		Method method = ArgumentsCommand.class.getDeclaredMethod("add", float.class, Float[].class, Float[].class);
		DefaultConversionService conversionService = new DefaultConversionService();
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		CommandContext ctx = Mockito.mock(CommandContext.class, Mockito.RETURNS_DEEP_STUBS);
		Mockito.when(ctx.getOptionByLongName("base"))
			.thenReturn(CommandOption.with().longName("base").value("10").build());
		StringWriter out = new StringWriter();
		Mockito.when(ctx.parsedInput().arguments())
			.thenReturn(java.util.List.of(new CommandArgument(0, "1"), new CommandArgument(1, "2"),
					new CommandArgument(2, "3"), new CommandArgument(3, "4"), new CommandArgument(4, "5"),
					new CommandArgument(5, "6"), new CommandArgument(6, "7"), new CommandArgument(7, "8"),
					new CommandArgument(8, "9"), new CommandArgument(9, "10")));
		Mockito.when(ctx.outputWriter()).thenReturn(new PrintWriter(out));
		MethodInvokerCommandAdapter adapter = new MethodInvokerCommandAdapter("name", "desc", "group", "help", false,
				method, target, conversionService, validator);

		// when
		ExitStatus status = adapter.doExecute(ctx);

		// then
		assertThat(status).isEqualTo(ExitStatus.OK);
		assertThat(target.base).isEqualTo(10f);
		assertThat(target.numbers).containsExactly(1f, 2f);
		assertThat(target.moreNumbers).containsExactly(3f, 4f, 5f);
	}

}