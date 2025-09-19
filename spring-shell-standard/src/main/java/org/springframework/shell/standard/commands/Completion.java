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

import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.completion.BashCompletions;
import org.springframework.shell.standard.completion.ZshCompletions;

/**
 * Command to create a shell completion files, i.e. for {@code bash}.
 *
 * @author Janne Valkealahti
 */
@ShellComponent
public class Completion extends AbstractShellComponent {

	/**
	 * Marker interface used in auto-config.
	 */
	public interface Command {
	}

	private ResourceLoader resourceLoader;
	private String rootCommand;

	public Completion(String rootCommand) {
		this.rootCommand = rootCommand;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@ShellMethod(key = "completion bash", value = "Generate bash completion script")
	public String bash() {
		BashCompletions bashCompletions = new BashCompletions(resourceLoader, getCommandCatalog());
		return bashCompletions.generate(rootCommand);
	}

	@ShellMethod(key = "completion zsh", value = "Generate zsh completion script")
	public String zsh() {
		ZshCompletions zshCompletions = new ZshCompletions(resourceLoader, getCommandCatalog());
		return zshCompletions.generate(rootCommand);
	}
}
