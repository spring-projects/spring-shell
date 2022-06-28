/*
 * Copyright 2016-2022 the original author or authors.
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

package org.springframework.shell.standard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ResolvableType;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandOption;

/**
 * A {@link ValueProvider} that knows how to complete values for {@link Enum} typed parameters.
 * @author Eric Bottard
 */
public class EnumValueProvider implements ValueProvider {

	@Override
	public List<CompletionProposal> complete(CompletionContext completionContext) {
		List<CompletionProposal> result = new ArrayList<>();
		CommandOption commandOption = completionContext.getCommandOption();
		if (commandOption != null) {
			ResolvableType type = commandOption.getType();
			if (type != null) {
				Class<?> clazz = type.getRawClass();
				if (clazz != null) {
					Object[] enumConstants = clazz.getEnumConstants();
					if (enumConstants != null) {
						for (Object v : enumConstants) {
							Enum<?> e = (Enum<?>) v;
							String prefix = completionContext.currentWordUpToCursor();
							if (prefix == null) {
								prefix = "";
							}
							if (e.name().startsWith(prefix)) {
								result.add(new CompletionProposal(e.name()));
							}
						}
					}
				}
			}
		}
		return result;
	}
}
