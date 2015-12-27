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

import org.springframework.shell2.ExitRequest;
import org.springframework.shell2.standard.ShellComponent;
import org.springframework.shell2.standard.ShellMethod;

/**
 * A command that terminates the running shell.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class Quit {

	@ShellMethod(help = "Exit the shell.", value = {"quit", "exit"})
	public void quit() {
		throw new ExitRequest();
	}
}
