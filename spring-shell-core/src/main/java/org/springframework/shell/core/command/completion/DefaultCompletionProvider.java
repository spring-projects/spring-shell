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
package org.springframework.shell.core.command.completion;

import org.jspecify.annotations.Nullable;
import org.springframework.shell.core.command.CommandOption;

import java.util.Collections;
import java.util.List;

/**
 * A default implementation of the {@link CompletionProvider} interface that provides
 * completion proposals for enum-type command options and handles prefix generation for
 * command options based on the current input context.
 *
 * @author David Pilar
 * @since 4.0.1
 */
public class DefaultCompletionProvider implements CompletionProvider {

	@Override
	public List<CompletionProposal> apply(CompletionContext context) {
		if (context.getCommandOption() == null || !context.getCommandOption().type().isEnum()) {
			return Collections.emptyList();
		}
		return new EnumCompletionProvider(context.getCommandOption().type(),
				getPrefix(context.currentWord(), context.getCommandOption()))
			.apply(context);
	}

	private String getPrefix(@Nullable String currentWord, CommandOption option) {
		String prefix = "";
		if (currentWord == null) {
			return prefix;
		}

		if (option.longName() != null && currentWord.startsWith("--" + option.longName() + "=")) {
			prefix = "--" + option.longName();
		}
		else if (option.shortName() != ' ' && currentWord.startsWith("-" + option.shortName() + "=")) {
			prefix = "-" + option.shortName();
		}
		return prefix;
	}

}
