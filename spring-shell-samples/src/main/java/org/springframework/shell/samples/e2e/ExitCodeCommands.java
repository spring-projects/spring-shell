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

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.stereotype.Component;

/**
 * Commands used for e2e test.
 *
 * @author Janne Valkealahti
 */
public class ExitCodeCommands {

	@Component
	public static class Registration extends BaseE2ECommands {
		@Bean
		public CommandRegistration testExitCodeRegistration() {
			return getBuilder()
				.command(REG, "exit-code")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.required()
					.and()
				.withTarget()
					.consumer(ctx -> {
						String arg1 = ctx.getOptionValue("arg1");
						throw new MyException(arg1);
					})
					.and()
				.withExitCode()
					.map(MyException.class, 3)
					.map(t -> {
						String msg = t.getMessage();
						if (msg != null && msg.contains("ok")) {
							return 0;
						}
						else if (msg != null && msg.contains("fun")) {
							return 4;
						}
						return 0;
					})
					.and()
				.build();
		}
	}

	static class MyException extends RuntimeException {

		MyException(String msg) {
			super(msg);
		}
	}
}
