/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.samples.e2e;

import org.junit.jupiter.params.ParameterizedTest;

import org.springframework.shell.command.annotation.EnableCommand;
import org.springframework.shell.samples.AbstractSampleTests;
import org.springframework.shell.samples.e2e.AliasCommands.AliasCommandsAnnotation;
import org.springframework.shell.samples.e2e.AliasCommands.AliasCommandsRegistration;
import org.springframework.shell.test.ShellTestClient.BaseShellSession;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { AliasCommandsRegistration.class })
@EnableCommand(AliasCommandsAnnotation.class)
public class AliasCommandsTests extends AbstractSampleTests {

	@ParameterizedTest
	@E2ESource(command = "alias-1", anno = false)
	void mainCommandWorks(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello from alias command");
	}

	@ParameterizedTest
	@E2ESource(command = "aliasfor-1", anno = false)
	void aliasCommandWorks(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello from alias command");
	}
}
