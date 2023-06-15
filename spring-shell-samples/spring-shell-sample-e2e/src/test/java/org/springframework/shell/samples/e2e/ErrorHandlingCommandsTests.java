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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;

import org.springframework.shell.samples.AbstractSampleTests;
import org.springframework.shell.samples.e2e.ErrorHandlingCommands.LegacyAnnotation;
import org.springframework.shell.samples.e2e.ErrorHandlingCommands.Registration;
import org.springframework.shell.test.ShellTestClient.BaseShellSession;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { LegacyAnnotation.class, Registration.class })
class ErrorHandlingCommandsTests extends AbstractSampleTests {

	@ParameterizedTest
	@E2ESource(command = "error-handling --arg1 throw1", annox = false)
	void testErrorHandling1(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hi, handled custom exception");
	}

	@Disabled("trouble with spring-shell-test")
	@ParameterizedTest
	@E2ESource(command = "error-handling --arg1 throw2", annox = false)
	void testErrorHandling2(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "org.springframework.shell.samples.e2e.ErrorHandlingCommands$CustomException2");
	}

	@Disabled("trouble with spring-shell-test")
	@ParameterizedTest
	@E2ESource(command = "error-handling --arg1 throw3", annox = false)
	void testErrorHandling3(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "java.lang.RuntimeException");
	}

	@ParameterizedTest
	@E2ESource(command = "error-handling --arg1 throw4", annox = false)
	void testErrorHandling4(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hi, handled illegal exception");
	}

	@ParameterizedTest
	@E2ESource(command = "error-handling --arg1 throw5", annox = false)
	void testErrorHandling5(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hi, handled custom exception 3");
	}

	@ParameterizedTest
	@E2ESource(command = "error-handling --arg1 throw6", annox = false)
	void testErrorHandling6(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hi, handled custom exception org.springframework.shell.samples.e2e.ErrorHandlingCommands$CustomException4");
	}
}
