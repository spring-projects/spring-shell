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
import org.springframework.shell.samples.e2e.DefaultValueCommands.Annotation;
import org.springframework.shell.samples.e2e.DefaultValueCommands.LegacyAnnotation;
import org.springframework.shell.samples.e2e.DefaultValueCommands.Registration;
import org.springframework.shell.test.ShellTestClient.BaseShellSession;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { LegacyAnnotation.class, Registration.class })
@EnableCommand(Annotation.class)
class DefaultValueCommandsTests extends AbstractSampleTests {

	@ParameterizedTest
	@E2ESource(command = "default-value")
	void defaultValue(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello hi");
	}

	@ParameterizedTest
	@E2ESource(command = "default-value-boolean1")
	void defaultValueBoolean1(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello false");
	}

	@ParameterizedTest
	@E2ESource(command = "default-value-boolean2")
	void defaultValueBoolean2(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello true");
	}

	@ParameterizedTest
	@E2ESource(command = "default-value-boolean3")
	void defaultValueBoolean3(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello false");
	}
}
