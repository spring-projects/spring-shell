/*
 * Copyright 2023 the original author or authors.
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

import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.EnableCommand;

class CommandAnnotationSnippets {

	class Dump1 {

		// tag::enablecommand-with-class[]
		@EnableCommand({ Example1.class, Example2.class })
		class App {

		}
		// end::enablecommand-with-class[]

		// tag::command-anno-in-method[]
		class Example1 {

			@Command(name = "example")
			public String example() {
				return "Hello";
			}

		}
		// end::command-anno-in-method[]

		// tag::command-option-arg[]
		class Example2 {

			@Command(name = "hi", description = "Say hi to a given name", group = "greetings",
					help = "A command that greets the user with 'Hi ${name}!' with a configurable suffix. Example usage: hi -s=! John")
			public void sayHi(
					@Argument(index = 0, description = "the name of the person to greet",
							defaultValue = "world") String name,
					@Option(shortName = 's', longName = "suffix", description = "the suffix of the greeting message",
							defaultValue = "!") char suffix) {
				System.out.println("Hi " + name + suffix);
			}

		}
		// end::command-option-arg[]

	}

}
