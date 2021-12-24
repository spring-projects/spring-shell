/*
 * Copyright 2015 the original author or authors.
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
import org.jline.utils.AttributedCharSequence;

import org.springframework.shell.ResultHandler;

/**
 * A {@link ResultHandler} that knows how to render JLine's {@link AttributedCharSequence}.
 *
 * @author Eric Bottard
 */
public class AttributedCharSequenceResultHandler extends TerminalAwareResultHandler<AttributedCharSequence> {

	public AttributedCharSequenceResultHandler(Terminal terminal) {
		super(terminal);
	}

	@Override
	protected void doHandleResult(AttributedCharSequence result) {
		terminal.writer().println(result.toAnsi(terminal));
		terminal.writer().flush();
	}
}
