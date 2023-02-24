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

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.EnableCommand;

class CommandAnnotationSnippets {

		class Dump1 {

			// tag::enablecommand-with-class[]
			@EnableCommand(Example.class)
			class App {
			}
			// end::enablecommand-with-class[]

			// tag::command-anno-in-method[]
			class Example {

				@Command(command = "example")
				public String example() {
					return "Hello";
				}
			}
			// end::command-anno-in-method[]
		}

		class Dump2 {

			// tag::command-anno-in-class[]
			@Command(command = "parent")
			class Example {

				@Command(command = "example")
				public String example() {
					return "Hello";
				}
			}
			// end::command-anno-in-class[]
		}

		class Dump3 {

			// tag::commandscan-no-args[]
			@CommandScan
			class App {
			}
			// end::commandscan-no-args[]
		}

}
