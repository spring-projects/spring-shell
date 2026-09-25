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
package org.springframework.shell.jline.tui.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import org.springframework.shell.core.InputReader;
import org.springframework.shell.jline.tui.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.jline.tui.component.NumberInput.NumberInputContext;
import org.springframework.shell.jline.tui.component.PathInput.PathInputContext;
import org.springframework.shell.jline.tui.component.PathSearch.PathSearchContext;
import org.springframework.shell.jline.tui.component.StringInput.StringInputContext;
import org.springframework.shell.jline.tui.component.context.ComponentContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for single value components falling back to an {@link InputReader} when there is
 * no tty.
 *
 * @author David Pilar
 */
@Timeout(5)
class InputReaderFallbackTests {

	private final Deque<String> inputs = new ArrayDeque<>();

	private final List<String> prompts = new ArrayList<>();

	private final InputReader inputReader = new InputReader() {
		@Override
		public String readInput(String prompt) {
			prompts.add(prompt);
			String input = inputs.pollFirst();
			return input == null ? "" : input;
		}
	};

	private static DumbTerminal noTtyTerminal() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return new DumbTerminal("terminal", "ansi", in, out, StandardCharsets.UTF_8);
	}

	@Test
	void stringInputReadsLineAndPassesPrompt() throws Exception {
		// given
		inputs.add("some reason");
		StringInput component = new StringInput(noTtyTerminal(), "Reason:", "default");
		component.setInputReader(inputReader);

		// when
		StringInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isEqualTo("some reason");
		assertThat(prompts).containsExactly("Reason: ");
		assertThat(inputs).isEmpty();
	}

	@Test
	void stringInputEmptyLineUsesDefaultValue() throws Exception {
		// given
		inputs.add("");
		StringInput component = new StringInput(noTtyTerminal(), "Reason:", "default");
		component.setInputReader(inputReader);

		// when
		StringInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isEqualTo("default");
	}

	@Test
	void stringInputRequiredWithoutInputTerminatesWithoutResult() throws Exception {
		// given
		StringInput component = new StringInput(noTtyTerminal(), "Reason:", null, null, true);
		component.setInputReader(inputReader);

		// when
		StringInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isNull();
	}

	@Test
	void stringInputWithoutInputReaderKeepsResultNull() throws Exception {
		// given
		StringInput component = new StringInput(noTtyTerminal(), "Reason:", "default");

		// when
		StringInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isNull();
	}

	@Test
	void numberInputParsesLine() throws Exception {
		// given
		inputs.add("42");
		NumberInput component = new NumberInput(noTtyTerminal(), "Age:", null);
		component.setInputReader(inputReader);

		// when
		NumberInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).hasToString("42");
	}

	@Test
	void numberInputInvalidLineTerminatesWithoutResult() throws Exception {
		// given
		inputs.add("not a number");
		NumberInput component = new NumberInput(noTtyTerminal(), "Age:", null);
		component.setInputReader(inputReader);

		// when
		NumberInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isNull();
	}

	@Test
	void pathInputReadsPath() throws Exception {
		// given
		inputs.add("target/some-file");
		PathInput component = new PathInput(noTtyTerminal(), "Path:");
		component.setInputReader(inputReader);

		// when
		PathInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isEqualTo(Paths.get("target/some-file"));
	}

	@Test
	void confirmationInputParsesYesAndNo() throws Exception {
		// given
		inputs.addAll(List.of("y", "no"));
		ConfirmationInput yes = new ConfirmationInput(noTtyTerminal(), "Confirm?", false);
		yes.setInputReader(inputReader);
		ConfirmationInput no = new ConfirmationInput(noTtyTerminal(), "Confirm?", true);
		no.setInputReader(inputReader);

		// when
		ConfirmationInputContext yesContext = yes.run(ComponentContext.empty());
		ConfirmationInputContext noContext = no.run(ComponentContext.empty());

		// then
		assertThat(yesContext.getResultValue()).isTrue();
		assertThat(noContext.getResultValue()).isFalse();
	}

	@Test
	void confirmationInputEmptyLineUsesDefaultValue() throws Exception {
		// given
		inputs.add("");
		ConfirmationInput component = new ConfirmationInput(noTtyTerminal(), "Confirm?", true);
		component.setInputReader(inputReader);

		// when
		ConfirmationInputContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isTrue();
	}

	@Test
	void pathSearchDoesNotConsumeInputReader() throws Exception {
		// given
		inputs.add("target");
		PathSearch component = new PathSearch(noTtyTerminal(), "Path:");
		component.setInputReader(inputReader);

		// when
		PathSearchContext context = component.run(ComponentContext.empty());

		// then
		assertThat(context.getResultValue()).isNull();
		assertThat(inputs).containsExactly("target");
		assertThat(prompts).isEmpty();
	}

	@Test
	void failingInputReaderIsReportedAsIllegalState() throws Exception {
		// given
		StringInput component = new StringInput(noTtyTerminal(), "Reason:", null);
		component.setInputReader(new InputReader() {
			@Override
			public String readInput(String prompt) throws Exception {
				throw new Exception("boom");
			}
		});

		// then
		assertThatThrownBy(() -> component.run(ComponentContext.empty())).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Reason:")
			.hasRootCauseMessage("boom");
	}

}
