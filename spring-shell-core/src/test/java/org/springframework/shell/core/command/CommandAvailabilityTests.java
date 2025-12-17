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

import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Tests for command availability.
 *
 * @author Mahmoud Ben Hassine
 */
public class CommandAvailabilityTests {

	@Test
	public void testCommandAvailability() throws Exception {
		// given
		Command command = new AbstractCommand("test", "A test command") {

			@Override
			public AvailabilityProvider getAvailabilityProvider() {
				return AvailabilityProvider.of(new Availability("you are not allowed to run this command"));
			}

			@Override
			public ExitStatus doExecute(CommandContext commandContext) {
				return ExitStatus.OK;
			}
		};
		StringWriter stringWriter = new StringWriter();
		CommandContext commandContext = new CommandContext(mock(ParsedInput.class), mock(CommandRegistry.class),
				new PrintWriter(stringWriter));

		// when
		ExitStatus exitStatus = command.execute(commandContext);

		// then
		assertEquals(ExitStatus.AVAILABILITY_ERROR, exitStatus);
		assertEquals(
				"Command 'test' exists but is not currently available because you are not allowed to run this command",
				stringWriter.toString().trim());
	}

}
