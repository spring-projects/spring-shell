/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.samples.jcommander;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.Valid;

/**
 * A class with JCommander commands.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class JCommanderCommands {

	@ShellMethod("Bind parameters to JCommander POJO.")
	public String jcommander(@ShellOption(optOut = true) @Valid Args args) {
		return "You said " + args;
	}
}
