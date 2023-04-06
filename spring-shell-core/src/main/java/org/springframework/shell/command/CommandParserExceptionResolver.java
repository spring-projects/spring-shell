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
package org.springframework.shell.command;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.shell.command.execution.CommandExecution.CommandParserExceptionsException;

/**
 * Handles {@link CommandParserExceptionsException}.
 *
 * @author Janne Valkealahti
 */
public class CommandParserExceptionResolver implements CommandExceptionResolver {

	@Override
	public CommandHandlingResult resolve(Exception ex) {
		if (ex instanceof CommandParserExceptionsException cpee) {
			AttributedStringBuilder builder = new AttributedStringBuilder();
			cpee.getParserExceptions().stream().forEach(e -> {
				builder.append(new AttributedString(e.getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)));
				builder.append("\n");
			});
			String as = builder.toAttributedString().toAnsi();
			return CommandHandlingResult.of(as);
		}
		return null;
	}
}
