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

public class CommandRegistrationSnippets {

	void dump1() {
		// tag::snippet1[]
		CommandRegistration.builder()
			.withOption()
				.longNames("myopt")
				.and()
			.build();
		// end::snippet1[]
	}

	void dump2() {
		// tag::snippet2[]
		CommandRegistration.builder()
			.withOption()
				.shortNames('s')
				.and()
			.build();
		// end::snippet2[]
	}
}
