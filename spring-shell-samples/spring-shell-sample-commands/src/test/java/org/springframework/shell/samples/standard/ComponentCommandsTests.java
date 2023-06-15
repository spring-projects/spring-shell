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
package org.springframework.shell.samples.standard;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.springframework.shell.samples.AbstractSampleTests;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient.BaseShellSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class ComponentCommandsTests extends AbstractSampleTests {

	@ParameterizedTest
	@CsvSource({
		"component single,false",
		"component single,true"
	})
	void componentSingle(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(session.screen().lines()).anySatisfy(line -> {
				assertThat(line).containsPattern("[>❯] key1");
			});
		});

		session.write(session.writeSequence().keyDown().build());
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(session.screen().lines()).anySatisfy(line -> {
				assertThat(line).containsPattern("[>❯] key2");
			});
		});

		session.write(session.writeSequence().cr().build());
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen()).containsText("Got value value2");
		});
	}

	@ParameterizedTest
	@CsvSource({
		"component multi,false",
		"component multi,true"
	})
	void componentMulti(String command, boolean interactive) {
		BaseShellSession<?> session = createSession(command, interactive);

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(session.screen().lines()).anySatisfy(line -> {
				assertThat(line).containsPattern("[>❯] (☐|\\[ \\])  key1");
			});
		});

		session.write(session.writeSequence().space().build());
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(session.screen().lines()).anySatisfy(line -> {
				assertThat(line).containsPattern("[>❯] (☒|\\[x\\])  key1");
			});
		});

		session.write(session.writeSequence().cr().build());
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen()).containsText("Got value value1,value2");
		});
	}
}
