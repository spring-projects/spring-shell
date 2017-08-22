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

package org.springframework.shell.samples.legacy;

import java.lang.reflect.Method;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * A sample of legacy Shell 1 commands that can be run thanks to the legacy adapter.
 *
 * @author Eric Bottard
 */
@Component
public class LegacyCommands implements CommandMarker {

	public static final Method REGISTER_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "register", String.class, ArtifactType.class, String.class, boolean.class);
	public static final Method SUM_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "sum", int.class, int.class);

	private boolean available = true;

	@CliAvailabilityIndicator("register module")
	public boolean registerAvailable() {
		return available;
	}

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
			@CliOption(mandatory = false,
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

	@CliCommand(value = "sum", help = "adds two numbers. Will also toggle the 'register module' command availability")
	public int sum(
		@CliOption(key = "v1", unspecifiedDefaultValue = "38") int a,
		@CliOption(key = "v2", specifiedDefaultValue = "42") int b) {
		available = !available;
		return a + b;
	}

	@CliCommand(value = "sum2", help = "adds two numbers")
	public int sum2(
			@CliOption(key = "v1", unspecifiedDefaultValue = "38") int a,
			@CliOption(key = "v2", specifiedDefaultValue = "42", mandatory = true) int b,
			@CliOption(key = "v3", mandatory = false) int c) {
		return a + b + c;
	}

	@CliCommand(value = "legacy-echo", help = "Echoes a message")
	public String legacyEcho(@CliOption(key = "", mandatory = true) String message) {
		return message;
	}

	@CliCommand(value = "optional-echo", help = "Echoes an optional message")
	public String optionalEcho(@CliOption(key = "", mandatory = false) String message) {
		return message;
	}

}
