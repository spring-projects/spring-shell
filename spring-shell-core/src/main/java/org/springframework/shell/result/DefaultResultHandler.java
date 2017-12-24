/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.shell.result;

import org.springframework.shell.ResultHandler;
import org.springframework.stereotype.Component;

/**
 * A simple {@link ResultHandler} that deals with Objects (hence comes as a last resort)
 * and prints the {@link Object#toString()} value of results to standard out.
 *
 * @author Eric Bottard
 */
public class DefaultResultHandler extends TerminalAwareResultHandler<Object> {

	@Override
	protected void doHandleResult(Object result) {
		if (!(result instanceof Throwable)) {
			terminal.writer().println(String.valueOf(result));
		}
		else { // Assumes throwables will have been handled elsewhere, eg ThrowableResultHandler or
				// non-interactive mode
			if (result instanceof RuntimeException) {
				throw (RuntimeException) result;
			}
			else if (result instanceof Error) {
				throw (Error) result;
			}
			else {
				throw new RuntimeException((Throwable) result);
			}
		}
	}
}
