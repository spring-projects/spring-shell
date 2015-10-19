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

package org.springframework.shell.table;

import org.springframework.util.Assert;

/**
 * A TextWrapper that delegates to another but makes sure that the contract is not violated.
 *
 * @author Eric Bottard
 */
public class DebugTextWrapper implements TextWrapper {

	private final TextWrapper delegate;

	public DebugTextWrapper(TextWrapper delegate) {
		this.delegate = delegate;
	}

	@Override
	public String[] wrap(String[] original, int columnWidth) {
		String[] result = delegate.wrap(original, columnWidth);
		for (String s : result) {
			Assert.isTrue(s.length() == columnWidth, String.format("'%s' has the wrong length (%d), expected %d", s, s.length(), columnWidth));
		}
		return result;
	}
}
