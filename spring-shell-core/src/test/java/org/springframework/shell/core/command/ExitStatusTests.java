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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExitStatusTests {

	@Test
	void okStatusShouldHaveCodeZero() {
		assertEquals(0, ExitStatus.OK.code());
		assertEquals("OK", ExitStatus.OK.description());
	}

	@Test
	void executionErrorStatusShouldHaveCodeMinusOne() {
		assertEquals(-1, ExitStatus.EXECUTION_ERROR.code());
		assertEquals("EXECUTION_ERROR", ExitStatus.EXECUTION_ERROR.description());
	}

	@Test
	void usageErrorStatusShouldHaveCodeMinusTwo() {
		assertEquals(-2, ExitStatus.USAGE_ERROR.code());
		assertEquals("USAGE_ERROR", ExitStatus.USAGE_ERROR.description());
	}

	@Test
	void availabilityErrorStatusShouldHaveCodeMinusThree() {
		assertEquals(-3, ExitStatus.AVAILABILITY_ERROR.code());
		assertEquals("AVAILABILITY_ERROR", ExitStatus.AVAILABILITY_ERROR.description());
	}

	@Test
	void customExitStatusShouldPreserveValues() {
		// when
		ExitStatus custom = new ExitStatus(42, "CUSTOM");

		// then
		assertEquals(42, custom.code());
		assertEquals("CUSTOM", custom.description());
	}

}
