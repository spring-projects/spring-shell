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
package org.springframework.shell.docs;

import org.springframework.shell.command.CommandRegistration;

class CommandRegistrationHiddenSnippets {

	// tag::snippet1[]
	CommandRegistration commandRegistration() {
		return CommandRegistration.builder()
			.command("mycommand")
			// define as hidden
			.hidden()
			// can be defined via a flag (false)
			.hidden(false)
			// can be defined via a flag (true)
			.hidden(true)
			.build();
	}
	// end::snippet1[]

}
