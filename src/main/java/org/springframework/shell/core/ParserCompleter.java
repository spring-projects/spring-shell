/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.shell.core;

import java.util.ArrayList;
import java.util.List;

import jline.console.completer.Completer;

import org.springframework.util.Assert;

/**
 * An implementation of JLine's {@link Completer} interface that delegates to a {@link Parser}.
 * 
 * @author Ben Alex
 * @since 1.0
 */
public class ParserCompleter implements Completer {

	// Fields
	private final Parser parser;

	public ParserCompleter(final Parser parser) {
		Assert.notNull(parser, "Parser required");
		this.parser = parser;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int complete(final String buffer, final int cursor, final List candidates) {
		int result;
		try {
			JLineLogHandler.cancelRedrawProhibition();
			List<Completion> completions = new ArrayList<Completion>();
			result = parser.completeAdvanced(buffer, cursor, completions);
			for (Completion completion : completions) {
				candidates.add(completion.getValue());
			}
		}
		finally {
			JLineLogHandler.prohibitRedraw();
		}
		return result;
	}
}
