/*
 * Copyright 2022-2023 the original author or authors.
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
import org.springframework.core.env.Environment;
import org.springframework.shell.test.autoconfigure.app.ExampleShellApplication;
import org.springframework.shell.test.jediterm.terminal.ui.TerminalSession;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link ShellTest#properties properties} attribute of
 * {@link ShellTest @ShellTest}.
 *
 * @author Janne Valkealahti
 */
@ShellTest(properties = { "spring.profiles.active=test", "spring.shell.test.terminal-width=81",
		"spring.shell.test.terminal-height=25" })
@ContextConfiguration(classes = ExampleShellApplication.class)
public class ShellTestPropertiesIntegrationTests {

	@Autowired
	private Environment environment;

	@Autowired
	private TerminalSession session;

	@Test
	void environmentWithNewProfile() {
		assertThat(this.environment.getActiveProfiles()).containsExactly("test");
	}

	@Test
	void dimensionsSet() {
		assertThat(session.getTerminal().getTerminalWidth()).isEqualTo(81);
		assertThat(session.getTerminal().getTerminalHeight()).isEqualTo(25);
	}

}
