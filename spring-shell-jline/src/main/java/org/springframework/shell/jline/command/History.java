/*
 * Copyright 2018-present the original author or authors.
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

package org.springframework.shell.jline.command;

import java.io.FileWriter;
import java.io.PrintWriter;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.Script;

/**
 * A command that displays all previously run commands, optionally dumping them to a file
 * readable by {@link Script}.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine
 */
public class History implements Command {

	private final org.jline.reader.History jLineHistory;

	public History(org.jline.reader.History jLineHistory) {
		this.jLineHistory = jLineHistory;
	}

	@Override
	public String getName() {
		return "history";
	}

	@Override
	public String getDescription() {
		return "Display or save the history of previously run commands";
	}

	@Override
	public String getGroup() {
		return "Built-In Commands";
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
		PrintWriter outputWriter = commandContext.outputWriter();
		CommandOption fileOption = commandContext.getOptionByName("file");
		if (fileOption == null) {
			jLineHistory.forEach(e -> outputWriter.println(e.line()));
		}
		else {
			String fileName = fileOption.value();
			if (fileName == null || fileName.isEmpty()) {
				throw new IllegalArgumentException("File name must be provided");
			}
			try (FileWriter w = new FileWriter(fileName)) {
				for (org.jline.reader.History.Entry entry : jLineHistory) {
					w.append(entry.line()).append(System.lineSeparator());
				}
			}
			outputWriter.println(String.format("Wrote %d entries to %s", jLineHistory.size(), fileName));
		}
		outputWriter.flush();
		return ExitStatus.OK;
	}

}
