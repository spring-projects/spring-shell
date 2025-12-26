/*
 * Copyright 2022-present the original author or authors.
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

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Command;

class ReadingSnippets {

	class Dump1 {

		// tag::input-reader[]
		@Command
		public void example(CommandContext ctx) throws Exception {
			String name = ctx.inputReader().readInput("Enter your name: ");
			char[] chars = ctx.inputReader().readPassword("Enter new password: ");
			// do something with name and password
		}
		// end::input-reader[]

	}

}
