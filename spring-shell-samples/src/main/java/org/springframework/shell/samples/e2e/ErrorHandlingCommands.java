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
package org.springframework.shell.samples.e2e;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.standard.ShellComponent;

/**
 * Commands used for e2e test.
 *
 * @author Janne Valkealahti
 */
@ShellComponent
public class ErrorHandlingCommands extends BaseE2ECommands {

	@Bean
	public CommandRegistration testErrorHandlingRegistration() {
		return CommandRegistration.builder()
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
					return "Hello " + arg1;
				})
				.and()
			.build();
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


	private static class CustomExceptionResolver implements CommandExceptionResolver {

		@Override
		public CommandHandlingResult resolve(Exception e) {
			if (e instanceof CustomException1) {
				return CommandHandlingResult.of("Hi, handled exception\n", 42);
			}
			return null;
		}
	}
}
