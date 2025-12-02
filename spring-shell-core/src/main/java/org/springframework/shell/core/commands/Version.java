/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.core.commands;

import java.io.PrintWriter;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;

/**
 * Command to print the current version of Spring Shell.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
public class Version implements Command {

	@Override
	public String getDescription() {
		return "Show version info";
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
		Package pkg = Version.class.getPackage();
		String version = "N/A";
		if (pkg != null && pkg.getImplementationVersion() != null) {
			version = pkg.getImplementationVersion();
		}
		try (PrintWriter printWriter = commandContext.outputWriter()) {
			printWriter.println("Version: " + version);
			printWriter.flush();
		}
		return ExitStatus.OK;
	}

}
