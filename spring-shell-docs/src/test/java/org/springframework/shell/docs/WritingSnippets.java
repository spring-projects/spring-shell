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

import org.jline.terminal.Terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellMethod;

class WritingSnippets {

	class Dump1 {

		// tag::legacyanno-terminal-writer[]
		@Autowired
		Terminal terminal;

		@ShellMethod
		public void example() {
			terminal.writer().println("hi");
			terminal.writer().flush();
		}
		// end::legacyanno-terminal-writer[]
	}

	class Dump2 {

		// tag::anno-terminal-writer[]
		@Command
		public void example(CommandContext ctx) {
			ctx.getTerminal().writer().println("hi");
			ctx.getTerminal().writer().flush();
		}
		// end::anno-terminal-writer[]
	}

	void dump1() {
		// tag::reg-terminal-writer[]
		CommandRegistration.builder()
			.command("example")
			.withTarget()
				.consumer(ctx -> {
					ctx.getTerminal().writer().println("hi");
					ctx.getTerminal().writer().flush();
				})
				.and()
			.build();
		// end::reg-terminal-writer[]
	}
}
