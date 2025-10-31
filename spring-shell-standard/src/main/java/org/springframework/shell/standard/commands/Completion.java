/*
 * Copyright 2022-2023 the original author or authors.
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

import org.springframework.shell.standard.AbstractCommand;
import org.springframework.shell.standard.completion.BashCompletions;
import org.springframework.shell.standard.completion.ZshCompletions;

/**
 * Command to create a shell completion files, i.e. for {@code bash}.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public class Completion extends AbstractCommand {

	/**
	 * Marker interface used in auto-config.
	 */
	public interface Command {
	}

	private String rootCommand;

	public Completion(String rootCommand) {
		this.rootCommand = rootCommand;
	}

	@org.springframework.shell.core.command.annotation.Command(command = "completion bash", description = "Generate bash completion script")
	public String bash() {
		BashCompletions bashCompletions = new BashCompletions(getResourceLoader(), getCommandCatalog());
		return bashCompletions.generate(rootCommand);
	}

	@org.springframework.shell.core.command.annotation.Command(command = "completion zsh", description = "Generate zsh completion script")
	public String zsh() {
		ZshCompletions zshCompletions = new ZshCompletions(getResourceLoader(), getCommandCatalog());
		return zshCompletions.generate(rootCommand);
	}
}
