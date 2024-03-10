/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.shell.docs;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.ShellTestClient.InteractiveShellSession;
import org.springframework.shell.test.ShellTestClient.NonInteractiveShellSession;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.awaitility.Awaitility.await;

class TestingSnippets {

	// tag::testing-shelltest-interactive[]
	@ShellTest
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	class InteractiveTestSample {

		@Autowired
		ShellTestClient client;

		@Test
		void test() {
			InteractiveShellSession session = client
					.interactive()
					.run();

			await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
				ShellAssertions.assertThat(session.screen())
					.containsText("shell");
			});

			session.write(session.writeSequence().text("help").carriageReturn().build());
			await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
				ShellAssertions.assertThat(session.screen())
					.containsText("AVAILABLE COMMANDS");
			});
		}
	}
	// end::testing-shelltest-interactive[]

	// tag::testing-shelltest-noninteractive[]
	@ShellTest
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	class NonInteractiveTestSample {

		@Autowired
		ShellTestClient client;

		@Test
		void test() {
			NonInteractiveShellSession session = client
				.nonInterative("help")
				.run();

			await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
				ShellAssertions.assertThat(session.screen())
					.containsText("AVAILABLE COMMANDS");
			});
		}
	}
	// end::testing-shelltest-noninteractive[]

	class Dump1 {

		// tag::testing-shelltest-dimensions-props[]
		@ShellTest(properties = {
			"spring.shell.test.terminal-width=120",
			"spring.shell.test.terminal-height=40"
		})
		class ShellSettingsSample {}
		// end::testing-shelltest-dimensions-props[]
	}
	class Dump2 {

		// tag::testing-shelltest-dimensions-field[]
		@ShellTest(terminalWidth = 120, terminalHeight = 40)
		class ShellSettingsSample {}
		// end::testing-shelltest-dimensions-field[]
	}

}
