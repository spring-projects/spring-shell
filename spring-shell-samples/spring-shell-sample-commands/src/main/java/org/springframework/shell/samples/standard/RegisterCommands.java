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

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.commands.AbstractCommand;

public class RegisterCommands extends AbstractCommand {

	private final static String GROUP = "Register Commands";

	private final PojoMethods pojoMethods = new PojoMethods();

	private final Command registered1;

	private final Command registered2;

	private final Command registered3;

	public RegisterCommands() {
		registered1 = Command.builder()
			.command("register registered1")
			.group(GROUP)
			.description("registered1 command")
			.withTarget(targetSpec -> targetSpec.method(pojoMethods, "registered1"))
			.build();
		registered2 = Command.builder()
			.command("register registered2")
			.description("registered2 command")
			.group(GROUP)
			.withTarget(targetSpec -> targetSpec.method(pojoMethods, "registered2"))
			.withOption(optionSpec -> optionSpec.longNames("arg1"))
			.build();
		registered3 = Command.builder()
			.command("register registered3")
			.description("registered3 command")
			.group(GROUP)
			.withTarget(targetSpec -> targetSpec.method(pojoMethods, "registered3"))
			.build();
	}

	@org.springframework.shell.core.command.annotation.Command(command = "register add",
			description = "Register commands", group = GROUP)
	public String register() {
		getCommandRegistry().register(registered1, registered2, registered3);
		registerFunctionCommand("register registered4");
		return "Registered commands registered1, registered2, registered3, registered4";
	}

	@org.springframework.shell.core.command.annotation.Command(command = "register remove",
			description = "Deregister commands", group = GROUP)
	public String deregister() {
		getCommandRegistry().unregister("register registered1", "register registered2", "register registered3",
				"register registered4");
		return "Deregistered commands registered1, registered2, registered3, registered4";
	}

	private void registerFunctionCommand(String command) {
		Function<CommandContext, String> function = ctx -> {
			String arg1 = ctx.getOptionValue("arg1");
			return String.format("hi, arg1 value is '%s'", arg1);
		};
		Command registration = Command.builder()
			.command(command)
			.description("registered4 command")
			.group(GROUP)
			.withTarget(targetSpec -> targetSpec.function(function))
			.withOption(optionSpec -> optionSpec.longNames("arg1"))
			.build();
		getCommandRegistry().register(registration);
	}

	public static class PojoMethods {

		@org.springframework.shell.core.command.annotation.Command
		public String registered1() {
			return "registered1";
		}

		@org.springframework.shell.core.command.annotation.Command
		public String registered2(String arg1) {
			return "registered2" + arg1;
		}

		@org.springframework.shell.core.command.annotation.Command
		public String registered3(@Option(defaultValue = Option.NULL) String arg1) {
			return "registered3" + arg1;
		}

	}

}
