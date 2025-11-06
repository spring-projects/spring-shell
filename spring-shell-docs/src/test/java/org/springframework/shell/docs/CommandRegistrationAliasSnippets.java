/*
 * Copyright 2024 the original author or authors.
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

import org.springframework.shell.core.command.Command;

class CommandRegistrationAliasSnippets {

	// tag::builder[]
	Command commandRegistration() {
		return Command.builder()
			.command("mycommand")
			// define alias as myalias
			.withAlias(aliasSpec -> aliasSpec.command("myalias"))
			// define alias as myalias1 and myalias2
			.withAlias(aliasSpec -> aliasSpec.command("myalias1", "myalias2"))
			.build();
	}
	// end::builder[]

	class Dump1 {

		// tag::command1[]
		@org.springframework.shell.core.command.annotation.Command
		class MyCommands {

			@org.springframework.shell.core.command.annotation.Command(command = "mycommand", alias = "myalias")
			void myCommand() {
			}

		}
		// end::command1[]

	}

	class Dump2 {

		// tag::command2[]
		@org.springframework.shell.core.command.annotation.Command
		class MyCommands {

			@org.springframework.shell.core.command.annotation.Command(command = "mycommand",
					alias = { "myalias1", "myalias2" })
			void myCommand() {
			}

		}
		// end::command2[]

	}

	class Dump3 {

		// tag::command3[]
		@org.springframework.shell.core.command.annotation.Command(alias = "myalias")
		class MyCommands {

			@org.springframework.shell.core.command.annotation.Command(command = "mycommand")
			void myCommand() {
			}

		}
		// end::command3[]

	}

	class Dump4 {

		// tag::command4[]
		@org.springframework.shell.core.command.annotation.Command(alias = "myalias1")
		class MyCommands {

			@org.springframework.shell.core.command.annotation.Command(command = "mycommand", alias = "myalias2")
			void myCommand() {
			}

		}
		// end::command4[]

	}

	class Dump5 {

		// tag::command5[]
		@org.springframework.shell.core.command.annotation.Command(command = "mycommand", alias = "myalias")
		class MyCommands {

			@org.springframework.shell.core.command.annotation.Command(command = "", alias = "")
			void myMainCommand() {
			}

			@org.springframework.shell.core.command.annotation.Command(command = "mysubcommand", alias = "mysubalias")
			void mySubCommand() {
			}

		}
		// end::command5[]

	}

}
