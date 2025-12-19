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
package org.springframework.shell.docs;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.AbstractCommand;

public class CommandRegistrationBeanSnippets {

	class Dump1 {

		// tag::plain[]
		@Bean
		Command myCommand() {
			return Command.builder().name("mycommand").execute(context -> {
				context.outputWriter().println("This is my command!");
			});
		}
		// end::plain[]

	}

	class Dump2 {

		// tag::abstractcommand[]
		@Bean
		Command myCommand() {
			return new AbstractCommand("mycommand", "This is my command") {
				@Override
				public ExitStatus doExecute(CommandContext commandContext) {
					println("This is my command!", commandContext);
					return ExitStatus.OK;
				}
			};
		}
		// end::abstractcommand[]

	}

}
