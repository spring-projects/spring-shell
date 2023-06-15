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
package org.springframework.shell.samples.standard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandRegistration;

@Configuration
public class FunctionCommands {

	@Bean
	public CommandRegistration commandRegistration1() {
		return CommandRegistration.builder()
			.command("function", "command1")
			.description("function sample")
			.group("Function Commands")
			.withTarget()
				.function(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					return String.format("hi, arg1 value is '%s'", arg1);
				})
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.build();
	}

	@Bean
	public CommandRegistration commandRegistration2() {
		return CommandRegistration.builder()
			.command("function", "command2")
			.description("function sample")
			.group("Function Commands")
			.withTarget()
				.function(ctx -> {
					Boolean a = ctx.getOptionValue("a");
					Boolean b = ctx.getOptionValue("b");
					Boolean c = ctx.getOptionValue("c");
					return String.format("hi, boolean values for a, b, c are '%s' '%s' '%s'", a, b, c);
				})
				.and()
			.withOption()
				.shortNames('a')
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('b')
				.type(boolean.class)
				.and()
			.withOption()
				.shortNames('c')
				.type(boolean.class)
				.and()
			.build();
	}

	@Bean
	public CommandRegistration commandRegistration3() {
		return CommandRegistration.builder()
			.command("function", "command3")
			.description("function sample")
			.group("Function Commands")
			.withTarget()
				.consumer(ctx -> {
					String arg1 = ctx.getOptionValue("arg1");
					ctx.getTerminal().writer()
						.println(String.format("hi, arg1 value is '%s'", arg1));
				})
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.build();
	}

	@Bean
	public CommandRegistration commandRegistration4() {
		return CommandRegistration.builder()
			.command("function", "command4")
			.description("function sample")
			.group("Function Commands")
			.withTarget()
				.consumer(ctx -> {
					ctx.getTerminal().writer()
						.println(String.format("hi, command is '%s'", ctx.getCommandRegistration().getCommand()));
				})
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.build();
	}
}
