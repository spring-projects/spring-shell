/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell2.legacy;

import java.lang.reflect.Method;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 09/12/15.
 */
public class LegacyCommands implements CommandMarker {

	public static final Method REGISTER_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "register", String.class, ArtifactType.class, String.class, boolean.class);
	public static final Method SUM_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "sum", int.class, int.class);
	public static final Method LEGACY_ECHO_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "legacyEcho", String.class);	
	public static final Method SOME_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "someMethod", String.class, boolean.class);

	@CliCommand(value = "register module", help = "Register a new module")
	public String register(
			@CliOption(mandatory = true,
					key = {"", "name"},
					help = "the name for the registered module")
			String name,
			@CliOption(mandatory = true,
					key = {"type"},
					help = "the type for the registered module")
			ArtifactType type,
			@CliOption(mandatory = true,
					key = {"coordinates", "coords"},
					help = "coordinates to the module archive")
			String coordinates,
			@CliOption(key = "force",
					help = "force update if module already exists (only if not in use)",
					specifiedDefaultValue = "true",
					unspecifiedDefaultValue = "false")
			boolean force) {
		return String.format(("Successfully registered module '%s:%s'"), type, name);
	}

	@CliCommand(value = "sum", help = "adds two numbers")
	public int sum(@CliOption(key = "v1", unspecifiedDefaultValue = "38") int a, @CliOption(key = "v2", specifiedDefaultValue = "42") int b) {
		return a + b;
	}
	
	@CliCommand(value = "legacy-echo", help = "Echoes a message")
	public String legacyEcho(@CliOption(key = "", mandatory = true) String message) {
		return message;
	}

	@CliCommand(value = "someMethod", help = "Method used for testing purposes")
	public String someMethod(
			@CliOption(key = "key", mandatory = false, help = "The optional parameter") String parameter,
			@CliOption(key = "option", help = "an option", specifiedDefaultValue = "true", unspecifiedDefaultValue = "false", mandatory = true) boolean option) {
		return parameter + ", " + option;
	}
	
}
