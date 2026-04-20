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
package org.springframework.shell.test.e2e;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellScreen;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;

@ShellTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
		useMainMethod = SpringBootTest.UseMainMethod.ALWAYS, classes = { GreetingShellApplication.class },
		properties = { "spring.shell.interactive.enabled=false" })
public class ShellApplicationEndToEndMultipleCommandsTests {

	@Test
	void testCommandExecution(@Autowired ShellTestClient client) throws Exception {
		// when
		ShellScreen shellScreen = client.sendCommand("help");

		// then
		ShellAssertions.assertThat(shellScreen).containsText("AVAILABLE COMMANDS");
	}

	@Test
	void testHiCommandExecution(@Autowired ShellTestClient client) throws Exception {
		// when
		ShellScreen shellScreen = client.sendCommand("hi");

		// then
		ShellAssertions.assertThat(shellScreen).containsText("Hello world!");
	}

	@Test
	void testByeCommandExecution(@Autowired ShellTestClient client) throws Exception {
		// when
		ShellScreen shellScreen = client.sendCommand("bye");

		// then
		ShellAssertions.assertThat(shellScreen).containsText("Goodbye world!");
	}

}