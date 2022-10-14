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
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;

class ErrorHandlingSnippets {

	// tag::my-exception-class[]
	static class CustomException extends RuntimeException {}
	// end::my-exception-class[]

	// tag::my-exception-resolver-class[]
	static class CustomExceptionResolver implements CommandExceptionResolver {

		@Override
		public CommandHandlingResult resolve(Exception e) {
			if (e instanceof CustomException) {
				return CommandHandlingResult.of("Hi, handled exception\n", 42);
			}
			return null;
		}
	}
	// end::my-exception-resolver-class[]

	void dump1() {
		// tag::example1[]
		CommandRegistration.builder()
			.withErrorHandling()
				.resolver(new CustomExceptionResolver())
				.and()
			.build();
		// end::example1[]
	}

}
