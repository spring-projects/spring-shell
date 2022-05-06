/*
 * Copyright 2017-2022 the original author or authors.
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
package org.springframework.shell.result;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import org.springframework.shell.ParameterValidationException;

/**
 * Displays validation errors on the terminal.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
public class ParameterValidationExceptionResultHandler
		extends TerminalAwareResultHandler<ParameterValidationException> {

	public ParameterValidationExceptionResultHandler(Terminal terminal) {
		super(terminal);
	}

	@Override
	protected void doHandleResult(ParameterValidationException result) {
		terminal.writer().println(new AttributedString("The following constraints were not met:",
				AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi());
		result.getConstraintViolations().stream()
				.forEach(v -> {
					terminal.writer().println(new AttributedString(v.toString(),
						AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi(terminal));
				});
	}

}
