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
package org.springframework.shell.core.command.metadata;

import org.jspecify.annotations.Nullable;

import org.springframework.core.ResolvableType;
import org.springframework.shell.core.completion.CompletionResolver;

/**
 * Represents an option of a command.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public record CommandOption(String[] longNames, String[] longNamesModified, Character[] shortNames,
		@Nullable String description, @Nullable ResolvableType type, boolean required, @Nullable String defaultValue,
		int position, int arityMin, int arityMax, @Nullable String label, @Nullable CompletionResolver completion) {

	/**
	 * Creates a {@link CommandOption} with common defaults.
	 * @param longNames long option names
	 * @param shortNames short option names
	 * @param description option description
	 * @param type option type
	 * @return a command option with defaults applied
	 */
	public static CommandOption of(String[] longNames, Character[] shortNames, String description,
			ResolvableType type) {
		return new CommandOption(longNames, new String[0], shortNames, description, type, false, null, -1, -1, -1, null,
				null);
	}

}
