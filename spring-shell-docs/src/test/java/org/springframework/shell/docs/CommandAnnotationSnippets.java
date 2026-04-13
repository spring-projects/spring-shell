/*
 * Copyright 2023-present the original author or authors.
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

import org.springframework.shell.core.command.annotation.*;
import org.springframework.stereotype.Component;

class CommandAnnotationSnippets {

	class Dump1 {

		// tag::enablecommand-with-class[]
		@EnableCommand({ MyCommands.class })
		class App {

		}
		// end::enablecommand-with-class[]

		// tag::command-anno-in-method[]
		@Component
		class MyCommands {

			@Command(name = "example")
			public String example() {
				return "Hello";
			}

		}
		// end::command-anno-in-method[]

		// tag::command-option-arg[]
		@Component
		class GreetingCommands {

			@Command(name = "hi", description = "Say hi to a given name", group = "greetings",
					help = "A command that greets the user with 'Hi ${name}!' with a configurable suffix. Example usage: hi -s=! John")
			public void sayHi(
					@Argument(index = 0, description = "the name of the person to greet",
							defaultValue = "world") String name,
					@Option(shortName = 's', longName = "suffix", description = "the suffix of the greeting message",
							defaultValue = "!") String suffix) {
				System.out.println("Hi " + name + suffix);
			}

		}
		// end::command-option-arg[]

		// tag::multi-valued-argument[]
		@Component
		class ArgumentsCommands {

			@Command(name = "hi", description = "Say hi to given names", group = "greetings",
					help = "A command that greets users with a configurable suffix. Example usage: hi -s=! Foo Bar")
			public void sayHi(@Option(shortName = 's', longName = "suffix",
					description = "the suffix of the greeting message", defaultValue = "!") String suffix,
					@Arguments String[] names) {
				System.out.println("Hi " + String.join(", ", names) + suffix);
			}

		}
		// end::multi-valued-argument[]

		// tag::multi-valued-argument-with-arity[]
		@Component
		class ArgumentsWithArityCommands {

			/**
			 * Real part of (a + bi)(c + di) = ac − bd.
			 */
			@Command(name = "real-part", description = "Calculate the real part of the product of two complex numbers",
					group = "math",
					help = "Calculate the real part of the product of two complex numbers. Example usage: real-part 1 2 3 4")
			public double realPartOfComplexProduct(@Arguments(arity = 2) double[] realParts,
					@Arguments(arity = 2) double[] imaginaryParts) {
				return realParts[0] * realParts[1] - imaginaryParts[0] * imaginaryParts[1];
			}

		}
		// end::multi-valued-argument-with-arity[]

	}

}
