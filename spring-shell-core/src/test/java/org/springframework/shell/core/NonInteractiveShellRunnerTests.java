/*
 * Copyright 2026-present the original author or authors.
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
package org.springframework.shell.core;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Set;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.Test;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandExecutionException;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.ParsedInput;
import org.springframework.shell.core.command.adapter.MethodInvokerCommandAdapter;
import org.springframework.shell.core.command.annotation.Option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link NonInteractiveShellRunner}.
 *
 * @author David Pilar
 */
class NonInteractiveShellRunnerTests {

	static class PlanCommand {

		public void plan(@Option(shortName = 'c', longName = "count") @Min(value = 1,
				message = "count must be at least 1") int count) {
		}

	}

	@Test
	void validationErrorsArePrintedBeforeFailing() throws Exception {
		// given
		Method method = PlanCommand.class.getDeclaredMethod("plan", int.class);
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Command command = new MethodInvokerCommandAdapter("plan", "Plan something", "", "", false, method,
				new PlanCommand(), new DefaultConversionService(), validator);
		CommandParser commandParser = input -> ParsedInput.builder()
			.commandName("plan")
			.addOption(CommandOption.with().shortName('c').value("0").build())
			.build();
		StringWriter output = new StringWriter();
		// a buffered writer, like the default one created on top of System.out
		PrintWriter outputWriter = new PrintWriter(new BufferedWriter(output));
		NonInteractiveShellRunner shellRunner = new NonInteractiveShellRunner(commandParser,
				new CommandRegistry(Set.of(command)), outputWriter);

		// when
		assertThatThrownBy(() -> shellRunner.run(new String[] { "plan", "-c", "0" }))
			.isInstanceOf(CommandExecutionException.class)
			.extracting(exception -> ((CommandExecutionException) exception).getExitCode())
			.isEqualTo(ExitStatus.USAGE_ERROR.code());

		// then
		assertThat(output.toString()).contains("The following constraints were not met:")
			.contains("--count: count must be at least 1");
	}

	@Test
	void commandOutputIsFlushedBeforeFailing() {
		// given
		Command command = new AbstractCommand("fail", "A failing command") {

			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				commandContext.outputWriter().println("something went wrong");
				return ExitStatus.EXECUTION_ERROR;
			}
		};
		CommandParser commandParser = input -> ParsedInput.builder().commandName(input).build();
		StringWriter output = new StringWriter();
		PrintWriter outputWriter = new PrintWriter(new BufferedWriter(output));
		NonInteractiveShellRunner shellRunner = new NonInteractiveShellRunner(commandParser,
				new CommandRegistry(Set.of(command)), outputWriter);

		// when
		assertThatThrownBy(() -> shellRunner.run(new String[] { "fail" }))
			.isInstanceOf(CommandExecutionException.class);

		// then
		assertThat(output.toString()).contains("something went wrong");
	}

}
