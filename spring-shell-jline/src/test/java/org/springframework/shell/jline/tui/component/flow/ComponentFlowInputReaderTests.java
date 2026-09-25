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
package org.springframework.shell.jline.tui.component.flow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;

import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import org.springframework.shell.core.InputReader;
import org.springframework.shell.jline.tui.component.context.ComponentContext;
import org.springframework.shell.jline.tui.component.flow.ComponentFlow.Builder;
import org.springframework.shell.jline.tui.component.flow.ComponentFlow.ComponentFlowResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ComponentFlow} consuming an {@link InputReader} when there is no tty.
 *
 * @author David Pilar
 */
@Timeout(5)
class ComponentFlowInputReaderTests {

	private final Deque<String> inputs = new ArrayDeque<>();

	private final InputReader inputReader = new InputReader() {
		@Override
		public String readInput(String prompt) {
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
	void flowConsumesQueuedInputsInOrder() throws Exception {
		// given
		inputs.addAll(List.of("some reason", "42", "y"));
		ComponentFlow flow = ComponentFlow.builder()
			.terminal(noTtyTerminal())
			.inputReader(inputReader)
			.withStringInput("reason")
			.name("Reason:")
			.and()
			.withNumberInput("age")
			.name("Age:")
			.numberClass(Integer.class)
			.and()
			.withConfirmationInput("confirm")
			.name("Confirm?")
			.and()
			.build();

		// when
		ComponentFlowResult result = flow.run();
		ComponentContext<?> context = result.getContext();

		// then
		assertThat((String) context.get("reason")).isEqualTo("some reason");
		assertThat((Object) context.get("age")).hasToString("42");
		assertThat((Boolean) context.get("confirm")).isTrue();
		assertThat(inputs).isEmpty();
	}

	@Test
	void cloneAndResetKeepInputReader() throws Exception {
		// given
		inputs.add("some reason");
		Builder builder = ComponentFlow.builder().terminal(noTtyTerminal()).inputReader(inputReader);

		// when
		ComponentFlowResult result = builder.clone()
			.reset()
			.withStringInput("reason")
			.name("Reason:")
			.and()
			.build()
			.run();

		// then
		assertThat((String) result.getContext().get("reason")).isEqualTo("some reason");
	}

	@Test
	void flowWithoutInputReaderLeavesContextEmpty() throws Exception {
		// given
		ComponentFlow flow = ComponentFlow.builder()
			.terminal(noTtyTerminal())
			.withStringInput("reason")
			.name("Reason:")
			.and()
			.build();

		// when
		ComponentContext<?> context = flow.run().getContext();

		// then
		assertThatThrownBy(() -> context.get("reason")).isInstanceOf(NoSuchElementException.class);
	}

}
