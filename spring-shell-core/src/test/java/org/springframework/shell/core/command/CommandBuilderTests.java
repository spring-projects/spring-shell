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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.InputReader;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandBuilderTests {

	@Test
	void buildWithConsumerExecutor() throws Exception {
		// given
		AtomicBoolean invoked = new AtomicBoolean(false);
		AbstractCommand command = Command.builder().name("test").description("Test command").execute(ctx -> {
			invoked.set(true);
		});

		StringWriter sw = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("test").build();
		CommandContext ctx = new CommandContext(parsedInput, new CommandRegistry(), new PrintWriter(sw),
				new InputReader() {
				});

		// when
		ExitStatus status = command.execute(ctx);

		// then
		assertEquals(ExitStatus.OK, status);
		assertTrue(invoked.get());
		assertEquals("test", command.getName());
		assertEquals("Test command", command.getDescription());
	}

	@Test
	void buildWithFunctionExecutor() throws Exception {
		// given
		AbstractCommand command = Command.builder()
			.name("hello")
			.description("Says hello")
			.execute(ctx -> "Hello World");

		StringWriter sw = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("hello").build();
		CommandContext ctx = new CommandContext(parsedInput, new CommandRegistry(), new PrintWriter(sw),
				new InputReader() {
				});

		// when
		ExitStatus status = command.execute(ctx);

		// then
		assertEquals(ExitStatus.OK, status);
		assertTrue(sw.toString().contains("Hello World"));
	}

	@Test
	void buildWithAliases() {
		// given
		AbstractCommand command = Command.builder().name("list").aliases("ls", "dir").execute(ctx -> {
		});

		// then
		assertEquals(List.of("ls", "dir"), command.getAliases());
	}

	@Test
	void buildWithOptions() {
		// given
		CommandOption opt = CommandOption.with().longName("verbose").shortName('v').build();
		AbstractCommand command = Command.builder().name("cmd").options(opt).execute(ctx -> {
		});

		// then
		assertEquals(1, command.getOptions().size());
		assertEquals("verbose", command.getOptions().get(0).longName());
	}

	@Test
	void buildWithAvailabilityProvider() throws Exception {
		// given
		AbstractCommand command = Command.builder()
			.name("restricted")
			.availabilityProvider(() -> Availability.unavailable("not logged in"))
			.execute(ctx -> {
			});

		StringWriter sw = new StringWriter();
		ParsedInput parsedInput = ParsedInput.builder().commandName("restricted").build();
		CommandContext ctx = new CommandContext(parsedInput, new CommandRegistry(), new PrintWriter(sw),
				new InputReader() {
				});

		// when
		ExitStatus status = command.execute(ctx);

		// then
		assertEquals(ExitStatus.AVAILABILITY_ERROR, status);
		assertTrue(sw.toString().contains("not logged in"));
	}

	@Test
	void buildWithHidden() {
		// given
		AbstractCommand command = Command.builder().name("secret").hidden(true).execute(ctx -> {
		});

		// then
		assertTrue(command.isHidden());
	}

	@Test
	void buildWithoutNameShouldThrow() {
		// when / then
		assertThrows(IllegalArgumentException.class, () -> Command.builder().execute(ctx -> {
		}));
	}

}
