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

package org.springframework.shell2.commands;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell2.standard.ShellComponent;
import org.springframework.shell2.standard.ShellMethod;

/**
 * ANSI console related commands.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class Console {

	@Autowired
	private Terminal terminal;

	@ShellMethod(help = "Clear the shell screen.")
	public void clear() {
		terminal.puts(InfoCmp.Capability.clear_screen);
	}
}
