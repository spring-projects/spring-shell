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
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class AliasCommands {

	private final static String DESCRIPTION = "main1 with main2 as alias";

	@ShellMethod(key = { "alias anno main1", "alias anno main2" }, group = "Alias Commands", value = DESCRIPTION)
	public String annoMain1() {
		return "Hello annoMain1";
	}

	@Bean
	public CommandRegistration regMain1() {
		return CommandRegistration.builder()
			.command("alias", "reg", "main1")
			.group("Alias Commands")
			.description(DESCRIPTION)
			.withAlias()
				.command("alias", "reg", "main2")
				.group("Alias Commands")
				.and()
			.withTarget()
				.function(ctx -> {
					return "Hello regMain1";
				})
				.and()
			.build();
	}

}
