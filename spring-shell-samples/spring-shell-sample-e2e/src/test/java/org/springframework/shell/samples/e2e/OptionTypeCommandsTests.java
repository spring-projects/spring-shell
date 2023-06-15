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
import org.springframework.shell.samples.e2e.OptionTypeCommands.Annotation;
import org.springframework.shell.samples.e2e.OptionTypeCommands.LegacyAnnotation;
import org.springframework.shell.samples.e2e.OptionTypeCommands.Registration;
import org.springframework.shell.test.ShellTestClient.BaseShellSession;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { LegacyAnnotation.class, Registration.class })
@EnableCommand(Annotation.class)
class OptionTypeCommandsTests extends AbstractSampleTests {

	@ParameterizedTest
	@E2ESource(command = "option-type-string --arg1 hi")
	void optionTypeString(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello hi");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-string hi")
	void optionTypeStringPositional(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello hi");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-boolean", reg = false)
	void optionTypeBooleanWithAnno(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello arg1=false arg2=true arg3=false arg4=false arg5=true arg6=false");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-boolean", annox = false, anno = false)
	void optionTypeBooleanWithReg(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello arg1=false arg2=true arg3=false arg4=null arg5=true arg6=false");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-integer --arg1 1 --arg2 2")
	void optionTypeInteger(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello '1' '2'");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-enum --arg1 ONE")
	void optionTypeEnum(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello ONE");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-string-array --arg1 one two")
	void optionTypeStringArray(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello [one,two]");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-int-array --arg1 1 2")
	void optionTypeIntArray(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello [1,2]");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-string-list --arg1 one two")
	void optionTypeStringList(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello [one, two]");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-string-set --arg1 one two")
	void optionTypeStringSet(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello [one, two]");
	}

	@ParameterizedTest
	@E2ESource(command = "option-type-string-collection --arg1 one two")
	void optionTypeStringCollection(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);
		assertScreenContainsText(session, "Hello [one, two]");
	}
}
