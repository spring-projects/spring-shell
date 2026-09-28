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
package org.springframework.shell.test.autoconfigure;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellInputProvider;
import org.springframework.shell.test.ShellScreen;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests that queued inputs from {@link ShellInputProvider} are consumed by single value
 * components of a component flow.
 *
 * @author David Pilar
 */
@ShellTest
@ContextConfiguration(classes = ComponentFlowShellApplication.class)
class ComponentFlowShellTests {

	@Test
	void testComponentFlowConfirmed(@Autowired ShellTestClient client) throws Exception {
		// given
		ShellInputProvider inputProvider = ShellInputProvider.providerFor("example")
			.withInput("some reason")
			.withInput("y")
			.build();

		// when
		ShellScreen screen = client.sendCommand(inputProvider);

		// then
		ShellAssertions.assertThat(screen).containsText("Reason:");
		ShellAssertions.assertThat(screen).containsText("Confirm?");
		ShellAssertions.assertThat(screen).containsText("Confirmed");
	}

	@Test
	void testComponentFlowCancelled(@Autowired ShellTestClient client) throws Exception {
		// given
		ShellInputProvider inputProvider = ShellInputProvider.providerFor("example")
			.withInput("some reason")
			.withInput("n")
			.build();

		// when
		ShellScreen screen = client.sendCommand(inputProvider);

		// then
		ShellAssertions.assertThat(screen).containsText("Cancelled");
	}

}
