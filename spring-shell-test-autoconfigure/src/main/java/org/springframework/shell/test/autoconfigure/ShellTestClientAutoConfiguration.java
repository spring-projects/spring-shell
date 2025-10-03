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
package org.springframework.shell.test.autoconfigure;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.jediterm.terminal.ui.TerminalSession;

/**
 * @author Janne Valkealahti
 */
@AutoConfiguration
public class ShellTestClientAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	ShellTestClient shellTestClient(TerminalSession widget, Shell shell, PromptProvider promptProvider,
			LineReader lineReader, Terminal terminal) {
		return ShellTestClient.builder(widget, shell, promptProvider, lineReader, terminal).build();
	}

}
