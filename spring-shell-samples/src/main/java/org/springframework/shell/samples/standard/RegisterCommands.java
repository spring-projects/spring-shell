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

import java.util.function.Function;

import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class RegisterCommands extends AbstractShellComponent {

	private final static String GROUP = "Register Commands";
	private final PojoMethods pojoMethods = new PojoMethods();
	private final CommandRegistration registered1;
	private final CommandRegistration registered2;
	private final CommandRegistration registered3;

	public RegisterCommands() {
		registered1 = CommandRegistration.builder()
			.command("register registered1")
			.group(GROUP)
			.description("registered1 command")
			.withTarget()
				.method(pojoMethods, "registered1")
				.and()
			.build();
		registered2 = CommandRegistration.builder()
			.command("register registered2")
			.description("registered2 command")
			.group(GROUP)
			.withTarget()
				.method(pojoMethods, "registered2")
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.build();
		registered3 = CommandRegistration.builder()
			.command("register registered3")
			.description("registered3 command")
			.group(GROUP)
			.withTarget()
				.method(pojoMethods, "registered3")
				.and()
			.build();
	}

    @ShellMethod(key = "register add", value = "Register commands", group = GROUP)
    public String register() {
		getCommandCatalog().register(registered1, registered2, registered3);
		registerFunctionCommand("register registered4");
		return "Registered commands registered1, registered2, registered3, registered4";
    }

    @ShellMethod(key = "register remove", value = "Deregister commands", group = GROUP)
    public String deregister() {
		getCommandCatalog().unregister("register registered1", "register registered2", "register registered3",
				"register registered4");
		return "Deregistered commands registered1, registered2, registered3, registered4";
    }

	private void registerFunctionCommand(String command) {
		Function<CommandContext, String> function = ctx -> {
			String arg1 = ctx.getOptionValue("arg1");
			return String.format("hi, arg1 value is '%s'", arg1);
		};
		CommandRegistration registration = CommandRegistration.builder()
			.command(command)
			.description("registered4 command")
			.group(GROUP)
			.withTarget()
				.function(function)
				.and()
			.withOption()
				.longNames("arg1")
				.and()
			.build();
		getCommandCatalog().register(registration);
	}

	public static class PojoMethods {

		@ShellMethod
		public String registered1() {
			return "registered1";
		}

		@ShellMethod
		public String registered2(String arg1) {
			return "registered2" + arg1;
		}

		@ShellMethod
		public String registered3(@ShellOption(defaultValue = ShellOption.NULL) String arg1) {
			return "registered3" + arg1;
		}
	}
}
