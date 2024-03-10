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
package org.springframework.shell.boot;

import org.jline.terminal.Terminal;
import org.jline.terminal.spi.TerminalExt;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.context.DefaultShellContext;
import org.springframework.shell.context.ShellContext;

@AutoConfiguration
public class ShellContextAutoConfiguration {

	@Bean
	public ShellContext shellContext(Terminal terminal) {
		boolean pty = false;
		if (terminal instanceof TerminalExt ext) {
			pty = ext.getSystemStream() != null;
		}
		return new DefaultShellContext(pty);
	}
}
