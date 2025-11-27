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
package org.springframework.shell.core.command;

import org.springframework.shell.core.Input;

/**
 * Interface parsing arguments for a {@link Command}. A command is always identified by a
 * set of words like {@code command subcommand1 subcommand2} and remaining part of it are
 * options which this interface intercepts and translates into format we can understand.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public interface CommandParser {

	/**
	 * Parse raw input into a {@link ParsedInput}.
	 * @param input the raw input
	 * @return the parsed input
	 * @since 4.0.0
	 */
	ParsedInput parse(Input input);

}
