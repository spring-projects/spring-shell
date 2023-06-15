/*
 * Copyright 2022-2023 the original author or authors.
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
package org.springframework.shell.samples.e2e;

import java.io.IOException;
import java.io.PrintWriter;

import org.jline.terminal.Terminal;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.ExceptionResolver;
import org.springframework.shell.command.annotation.ExitCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

/**
 * Commands used for e2e test.
 *
 * @author Janne Valkealahti
 */
public class ErrorHandlingCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "error-handling", group = GROUP)
		String testErrorHandling(String arg1) throws IOException {
			if ("throw1".equals(arg1)) {
				throw new CustomException1();
			}
			if ("throw2".equals(arg1)) {
				throw new CustomException2(11);
			}
			if ("throw3".equals(arg1)) {
				throw new RuntimeException();
			}
			if ("throw4".equals(arg1)) {
				throw new IllegalArgumentException();
			}
			if ("throw5".equals(arg1)) {
				throw new CustomException3();
			}
			if ("throw6".equals(arg1)) {
				throw new CustomException4();
			}
			return "Hello " + arg1;
		}

		@ExceptionResolver({ CustomException1.class })
		CommandHandlingResult errorHandler1(CustomException1 e) {
			return CommandHandlingResult.of("Hi, handled custom exception\n", 42);
		}

		@ExceptionResolver
		CommandHandlingResult errorHandler2(IllegalArgumentException e) {
			return CommandHandlingResult.of("Hi, handled illegal exception\n", 42);
		}

		@ExceptionResolver({ CustomException3.class })
		@ExitCode(3)
		String errorHandler3(CustomException3 e) {
			return "Hi, handled custom exception 3\n";
		}

		@ExceptionResolver({ CustomException4.class })
		@ExitCode(code = 4)
		void errorHandler3(CustomException4 e, Terminal terminal) {
			PrintWriter writer = terminal.writer();
			writer.println(String.format("Hi, handled custom exception %s", e));
			writer.flush();
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		CommandRegistration testErrorHandlingRegistration() {
			return getBuilder()
				.command(REG, "error-handling")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.required()
					.and()
				.withErrorHandling()
					.resolver(new CustomExceptionResolver())
					.and()
				.withTarget()
					.function(ctx -> {
						String arg1 = ctx.getOptionValue("arg1");
						if ("throw1".equals(arg1)) {
							throw new CustomException1();
						}
						if ("throw2".equals(arg1)) {
							throw new CustomException2(11);
						}
						if ("throw3".equals(arg1)) {
							throw new RuntimeException();
						}
						if ("throw4".equals(arg1)) {
							throw new IllegalArgumentException();
						}
						if ("throw5".equals(arg1)) {
							throw new CustomException3();
						}
						if ("throw6".equals(arg1)) {
							throw new CustomException4();
						}

						return "Hello " + arg1;
					})
					.and()
				.build();
		}
	}

	private static class CustomException1 extends RuntimeException {
	}

	private static class CustomException2 extends RuntimeException implements ExitCodeGenerator {

		private int code;

		CustomException2(int code) {
			this.code = code;
		}

		@Override
		public int getExitCode() {
			return code;
		}
	}

	private static class CustomException3 extends RuntimeException {
	}

	private static class CustomException4 extends RuntimeException {
	}

	private static class CustomExceptionResolver implements CommandExceptionResolver {

		@Override
		public CommandHandlingResult resolve(Exception e) {
			if (e instanceof CustomException1) {
				return CommandHandlingResult.of("Hi, handled custom exception\n", 42);
			}
			if (e instanceof CustomException3) {
				return CommandHandlingResult.of("Hi, handled custom exception 3\n", 3);
			}
			if (e instanceof CustomException4) {
				String msg = String.format("Hi, handled custom exception %s\n", e);
				return CommandHandlingResult.of(msg, 42);
			}
			if (e instanceof IllegalArgumentException) {
				return CommandHandlingResult.of("Hi, handled illegal exception\n", 42);
			}
			return null;
		}
	}
}
