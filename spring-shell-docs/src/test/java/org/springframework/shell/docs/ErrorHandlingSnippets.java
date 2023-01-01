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

import java.io.PrintWriter;

import org.jline.terminal.Terminal;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.ExceptionResolver;
import org.springframework.shell.command.annotation.ExitCode;

class ErrorHandlingSnippets {

	// tag::my-exception-class[]
	static class CustomException extends RuntimeException implements ExitCodeGenerator {

		@Override
		public int getExitCode() {
			return 0;
		}
	}
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

	static class Dump1 {

		// tag::exception-resolver-with-type-in-annotation[]
		@ExceptionResolver({ RuntimeException.class })
		CommandHandlingResult errorHandler(Exception e) {
			// Exception would be type of RuntimeException,
			// optionally do something with it
			return CommandHandlingResult.of("Hi, handled exception\n", 42);
		}
		// end::exception-resolver-with-type-in-annotation[]
	}

	static class Dump2 {

		// tag::exception-resolver-with-type-in-method[]
		@ExceptionResolver
		CommandHandlingResult errorHandler(RuntimeException e) {
			return CommandHandlingResult.of("Hi, handled custom exception\n", 42);
		}
		// end::exception-resolver-with-type-in-method[]
	}

	static class Dump3 {

		// tag::my-exception-resolver-class-as-bean[]
		@Bean
		CustomExceptionResolver customExceptionResolver() {
			return new CustomExceptionResolver();
		}
		// end::my-exception-resolver-class-as-bean[]
	}

	static class Dump4 {

		// tag::exception-resolver-with-exitcode-annotation[]
		@ExceptionResolver
		@ExitCode(code = 5)
		String errorHandler(Exception e) {
			return "Hi, handled exception";
		}
		// end::exception-resolver-with-exitcode-annotation[]
	}

	static class Dump5 {

		// tag::exception-resolver-with-void[]
		@ExceptionResolver
		@ExitCode(code = 5)
		void errorHandler(Exception e, Terminal terminal) {
			PrintWriter writer = terminal.writer();
			String msg =  "Hi, handled exception " + e.toString();
			writer.println(msg);
			writer.flush();
		}
		// end::exception-resolver-with-void[]
	}
}
