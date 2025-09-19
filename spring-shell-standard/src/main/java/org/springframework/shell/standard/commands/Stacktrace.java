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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.result.ThrowableResultHandler;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * A command to display the full stacktrace when an error occurs.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
@ShellComponent
public class Stacktrace extends AbstractShellComponent {

	/**
	 * Marker interface for beans providing {@literal stacktrace} functionality to the shell.
	 *
	 * <p>To override the stacktrace command, simply register your own bean implementing that interface
	 * and the standard implementation will back off.</p>
	 *
	 * <p>To disable the {@literal stacktrace} command entirely, set the {@literal spring.shell.command.stacktrace.enabled=false}
	 * property in the environment.</p>
	 *
	 * @author Eric Bottard
	 */
	public interface Command {}

	private ObjectProvider<ThrowableResultHandler> throwableResultHandler;

	public Stacktrace(ObjectProvider<ThrowableResultHandler> throwableResultHandler) {
		this.throwableResultHandler = throwableResultHandler;
	}

	@ShellMethod(key = ThrowableResultHandler.DETAILS_COMMAND_NAME,
			value = "Display the full stacktrace of the last error.",
			interactionMode = InteractionMode.INTERACTIVE)
	public void stacktrace() {
		ThrowableResultHandler handler = throwableResultHandler.getIfAvailable();
		if (handler != null) {
			if (handler.getLastError() != null) {
				handler.getLastError().printStackTrace(getTerminal().writer());
			}
		}
	}
}
