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

import org.springframework.shell.samples.AbstractSampleTests;
import org.springframework.shell.samples.e2e.ArityCommands.LegacyAnnotation;
import org.springframework.shell.samples.e2e.ArityCommands.Registration;
import org.springframework.shell.test.ShellTestClient.BaseShellSession;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { LegacyAnnotation.class, Registration.class })
class ArityCommandsTests extends AbstractSampleTests {

	@ParameterizedTest
	@E2ESource(command = "arity-boolean-default-true")
	void defaultBooleanValue(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello true");
	}

	@ParameterizedTest
	@E2ESource(command = "arity-boolean-default-true --overwrite false")
	void defaultBooleanValueOverwrite(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello false");
	}

	@ParameterizedTest
	@E2ESource(command = "arity-string-array --arg1 foo bar")
	void arityStringArray(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello [foo, bar]");
	}

	@ParameterizedTest
	@E2ESource(command = "arity-float-array --arg1 1 2")
	void arityFloatArray(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello [1.0,2.0]");
	}
}