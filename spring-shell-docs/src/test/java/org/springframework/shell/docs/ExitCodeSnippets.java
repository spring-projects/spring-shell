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

public class ExitCodeSnippets {

	// tag::my-exception-class[]
	static class MyException extends RuntimeException {

		private final int code;

		MyException(String msg, int code) {
			super(msg);
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	// end::my-exception-class[]

	void dump1() {
		// tag::example1[]
		CommandRegistration.builder()
			.withExitCode()
				.map(MyException.class, 3)
				.map(t -> {
					if (t instanceof MyException) {
						return ((MyException) t).getCode();
					}
					return 0;
				})
				.and()
			.build();
		// end::example1[]
	}
}
