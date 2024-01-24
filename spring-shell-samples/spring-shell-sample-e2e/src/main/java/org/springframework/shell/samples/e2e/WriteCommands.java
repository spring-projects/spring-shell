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
package org.springframework.shell.samples.e2e;

import org.jline.terminal.Terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

public class WriteCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@Autowired
		Terminal terminal;

		@ShellMethod(key = LEGACY_ANNO + "write-terminalwriter", group = GROUP)
		public void writeTerminalWriterAnnotation() {
			terminal.writer().println("hi");
			terminal.writer().flush();
		}

		@ShellMethod(key = LEGACY_ANNO + "write-systemout", group = GROUP)
		public void writeSystemOutAnnotation() {
			System.out.println("hi");
		}
	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "write-terminalwriter")
		public void writeTerminalWriter(CommandContext ctx) {
			ctx.getTerminal().writer().println("hi");
			ctx.getTerminal().writer().flush();
		}

		@Command(command = "write-systemout")
		public void writeSystemOut() {
			System.out.println("hi");
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration writeTerminalWriterRegistration() {
			return getBuilder()
				.command(REG, "write-terminalwriter")
				.group(GROUP)
				.withTarget()
					.consumer(ctx -> {
						ctx.getTerminal().writer().println("hi");
						ctx.getTerminal().writer().flush();
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration writeSystemOutRegistration() {
			return getBuilder()
				.command(REG, "write-terminalwriter")
				.group(GROUP)
				.withTarget()
					.consumer(ctx -> {
						System.out.println("hi");
					})
					.and()
				.build();
		}
	}
}
