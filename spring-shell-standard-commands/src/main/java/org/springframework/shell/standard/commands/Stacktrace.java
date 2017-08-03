/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.standard.commands;

import org.jline.terminal.Terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.result.ThrowableResultHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * A command to display the full stacktrace when an error occurs.
 */
@ShellComponent
public class Stacktrace {

	@Autowired @Lazy
	private Terminal terminal;

	@Autowired
	private ThrowableResultHandler throwableResultHandler;


	@ShellMethod(value = ThrowableResultHandler.DETAILS_COMMAND_NAME, help = "Display the full stacktrace of the last error")
	public void stacktrace() {
		if (throwableResultHandler.getLastError() != null) {
			throwableResultHandler.getLastError().printStackTrace(terminal.writer());
		}
	}
}
