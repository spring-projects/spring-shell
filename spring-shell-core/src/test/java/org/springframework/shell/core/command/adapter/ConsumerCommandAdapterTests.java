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
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.InputReader;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.ParsedInput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumerCommandAdapterTests {

	@Test
	void doExecuteShouldInvokeConsumerAndReturnOk() {
		// given
		AtomicBoolean invoked = new AtomicBoolean(false);
		ConsumerCommandAdapter adapter = new ConsumerCommandAdapter("test", "test desc", "group", "help", false,
				ctx -> invoked.set(true));

		ParsedInput parsedInput = ParsedInput.builder().commandName("test").build();
		CommandContext context = new CommandContext(parsedInput, new CommandRegistry(),
				new PrintWriter(new StringWriter()), new InputReader() {
				});

		// when
		ExitStatus exitStatus = adapter.doExecute(context);

		// then
		assertTrue(invoked.get());
		assertEquals(ExitStatus.OK, exitStatus);
	}

	@Test
	void shouldSetCommandProperties() {
		// given
		ConsumerCommandAdapter adapter = new ConsumerCommandAdapter("mycmd", "my description", "mygroup", "my help",
				true, ctx -> {
				});

		// then
		assertEquals("mycmd", adapter.getName());
		assertEquals("my description", adapter.getDescription());
		assertEquals("mygroup", adapter.getGroup());
		assertEquals("my help", adapter.getHelp());
		assertTrue(adapter.isHidden());
	}

}
