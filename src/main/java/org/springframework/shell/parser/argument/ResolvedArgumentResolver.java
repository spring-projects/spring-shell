/*
 * Copyright 2011-2017 the original author or authors.
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
package org.springframework.shell.parser.argument;

import jline.console.ConsoleReader;

/**
 * Represents an {@link ArgumentResolver} that has already been resolved and doesn't need further processing.
 * 
 * @author Camilo Gonzalez
 * @since 1.2.1
 */
public class ResolvedArgumentResolver implements ArgumentResolver {

	private final Object resolvedValue;

	public ResolvedArgumentResolver(Object resolvedValue) {
		this.resolvedValue = resolvedValue;
	}

	@Override
	public Object getArgumentValue(ConsoleReader consoleReader) {
		return resolvedValue;
	}

}