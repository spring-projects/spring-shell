/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell2.result;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import org.springframework.shell2.CommandNotFound;
import org.springframework.shell2.ResultHandler;
import org.springframework.stereotype.Component;

/**
 * Used when no command can be matched for user input.
 *
 * Simply prints an error message, without printing the exception class.
 *
 * @author Eric Bottard
 */
@Component
public class CommandNotFoundResultHandler extends TerminalAwareResultHandler implements ResultHandler<CommandNotFound> {

	@Override
	public void handleResult(CommandNotFound result) {
		terminal.writer().println(new AttributedString(result.getMessage(),
			AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi());

	}
}
