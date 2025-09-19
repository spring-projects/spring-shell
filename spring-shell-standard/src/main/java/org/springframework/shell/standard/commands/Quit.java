/*
 * Copyright 2017-2022 the original author or authors.
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

package org.springframework.shell.standard.commands;

import org.springframework.shell.ExitRequest;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * A command that terminates the running shell.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class Quit {

	/**
	 * Marker interface for beans providing {@literal quit} functionality to the shell.
	 *
	 * <p>To override the quit command, simply register your own bean implementing that interface
	 * and the standard implementation will back off.</p>
	 *
	 * <p>To disable the {@literal quit} command entirely, set the {@literal spring.shell.command.quit.enabled=false}
	 * property in the environment.</p>
	 *
	 * @author Eric Bottard
	 */
	public interface Command {}

	@ShellMethod(value = "Exit the shell.", key = {"quit", "exit"}, interactionMode = InteractionMode.INTERACTIVE)
	public void quit() {
		throw new ExitRequest();
	}
}
