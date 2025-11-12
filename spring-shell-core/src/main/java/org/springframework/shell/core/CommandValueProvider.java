/*
 * Copyright 2017-present the original author or authors.
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

package org.springframework.shell.core;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.completion.CompletionContext;
import org.springframework.shell.core.completion.CompletionProposal;

/**
 * A {@link ValueProvider} that can be used to auto-complete names of shell commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
public class CommandValueProvider implements ValueProvider {

	private final CommandRegistry commandRegistry;

	public CommandValueProvider(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Override
	public List<CompletionProposal> complete(CompletionContext completionContext) {
		return commandRegistry.getCommands()
			.stream()
			.map(command -> new CompletionProposal(command.getName()))
			.collect(Collectors.toList());
	}

}
