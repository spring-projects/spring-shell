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

import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class RegisterCommands extends AbstractShellComponent {

	private final PojoMethods pojoMethods = new PojoMethods();

    @ShellMethod(key = "register add", value = "Register commands", group = "Register Commands")
    public String register() {
		MethodTarget target1 = MethodTarget.of("dynamic1", pojoMethods, "Dynamic1 command", "Register Commands");
		MethodTarget target2 = MethodTarget.of("dynamic2", pojoMethods, "Dynamic2 command", "Register Commands");
		MethodTarget target3 = MethodTarget.of("dynamic3", pojoMethods, "Dynamic3 command", "Register Commands");
		getCommandRegistry().addCommand("register dynamic1", target1);
		getCommandRegistry().addCommand("register dynamic2", target2);
		getCommandRegistry().addCommand("register dynamic3", target3);
		return "Registered commands dynamic1, dynamic2, dynamic3";
    }

    @ShellMethod(key = "register remove", value = "Deregister commands", group = "Register Commands")
    public String deregister() {
		getCommandRegistry().removeCommand("register dynamic1");
		getCommandRegistry().removeCommand("register dynamic2");
		getCommandRegistry().removeCommand("register dynamic3");
		return "Deregistered commands dynamic1, dynamic2, dynamic3";
    }

	public static class PojoMethods {

		@ShellMethod
		public String dynamic1() {
			return "dynamic1";
		}

		@ShellMethod
		public String dynamic2(String arg1) {
			return "dynamic2" + arg1;
		}

		@ShellMethod
		public String dynamic3(@ShellOption(defaultValue = ShellOption.NULL) String arg1) {
			return "dynamic3" + arg1;
		}
	}
}
