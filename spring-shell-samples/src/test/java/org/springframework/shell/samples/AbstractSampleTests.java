/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.samples;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.shell.samples.standard.ResolvedCommands;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.ShellTestClient.BaseShellSession;
import org.springframework.shell.test.ShellTestClient.InteractiveShellSession;
import org.springframework.shell.test.ShellTestClient.NonInteractiveShellSession;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.awaitility.Awaitility.await;

@ShellTest
@Import(ResolvedCommands.ResolvedCommandsConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AbstractSampleTests {

	@Autowired
	protected ShellTestClient client;

	protected void assertScreenContainsText(BaseShellSession<?> session, String text) {
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen()).containsText(text);
		});
	}

	protected BaseShellSession<?> createSession(String command, boolean interactive) {
		if (interactive) {
			InteractiveShellSession session = client.interactive().run();
			session.write(session.writeSequence().command(command).build());
			return session;
		}
		else {
			String[] commands = command.split(" ");
			NonInteractiveShellSession session = client.nonInterative(commands).run();
			return session;
		}
	}
}
