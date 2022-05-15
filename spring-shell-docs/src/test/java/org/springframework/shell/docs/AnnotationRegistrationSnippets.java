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

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

public class AnnotationRegistrationSnippets {

	// tag::snippet1[]
	@ShellComponent
	static class MyCommands {

		@ShellMethod
		public void mycommand() {
		}
	}
	// end::snippet1[]

	static class Dump1 {

		// tag::snippet2[]
		@ShellMethod(value = "Add numbers.", key = "sum")
		public int add(int a, int b) {
			return a + b;
		}
		// end::snippet2[]
	}
}
