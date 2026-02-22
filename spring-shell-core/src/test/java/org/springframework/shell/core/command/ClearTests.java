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
package org.springframework.shell.core.command;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.InputReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClearTests {

	@Test
	void executeShouldSendAnsiClearSequence() throws Exception {
		// given
		Clear clear = new Clear();
		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("clear").build();
		CommandContext context = new CommandContext(parsedInput, new CommandRegistry(), new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when
		ExitStatus exitStatus = clear.execute(context);

		// then
		assertEquals(ExitStatus.OK, exitStatus);
		assertTrue(stringWriter.toString().contains("\033[H\033[2J"));
	}

	@Test
	void shouldHaveCorrectMetadata() {
		// given
		Clear clear = new Clear();

		// then
		assertEquals("Clear the terminal screen", clear.getDescription());
		assertEquals("Built-In Commands", clear.getGroup());
	}

}
