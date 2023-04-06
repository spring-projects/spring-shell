/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.standard.completion;

import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.command.catalog.CommandCatalog;

/**
 * Completion script generator for a {@code bash}.
 *
 * @author Janne Valkealahti
 */
public class BashCompletions extends AbstractCompletions {

	public BashCompletions(ResourceLoader resourceLoader, CommandCatalog commandCatalog) {
		super(resourceLoader, commandCatalog);
	}

	public String generate(String rootCommand) {
		CommandModel model = generateCommandModel();
		return builder()
				.attribute("name", rootCommand)
				.attribute("model", model)
				.group("classpath:completion/bash.stg")
				.appendGroup("main")
				.build();
	}
}
