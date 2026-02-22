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

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.InputReader;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.ParsedInput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionCommandAdapterTests {

	@Test
	void doExecuteShouldInvokeFunctionAndPrintOutput() {
		// given
		FunctionCommandAdapter adapter = new FunctionCommandAdapter("hello", "Says hello", "group", "help", false,
				ctx -> "Hello World");

		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("hello").build();
		CommandContext context = new CommandContext(parsedInput, new CommandRegistry(), new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when
		ExitStatus exitStatus = adapter.doExecute(context);

		// then
		assertEquals(ExitStatus.OK, exitStatus);
		assertTrue(stringWriter.toString().contains("Hello World"));
	}

	@Test
	void doExecuteShouldFlushOutput() {
		// given
		FunctionCommandAdapter adapter = new FunctionCommandAdapter("cmd", "desc", "", "", false, ctx -> "output");

		StringWriter stringWriter = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("cmd").build();
		CommandContext context = new CommandContext(parsedInput, new CommandRegistry(), new PrintWriter(stringWriter),
				new InputReader() {
				});

		// when
		adapter.doExecute(context);

		// then
		assertTrue(stringWriter.toString().contains("output"));
	}

}
