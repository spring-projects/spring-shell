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

import java.util.Arrays;

import org.springframework.util.Assert;

/**
 * A decorator Aligner that checks the Aligner invariants contract, useful for debugging.
 *
 * @author Eric Bottard
 */
public class DebugAligner implements Aligner {

	private final Aligner delegate;

	public DebugAligner(Aligner delegate) {
		this.delegate = delegate;
	}

	@Override
	public String[] align(String[] text, int cellWidth, int cellHeight) {
		String[] result = delegate.align(text, cellWidth, cellHeight);
		Assert.isTrue(result.length == cellHeight, String.format("%s had the wrong number of lines (%d), expected %d",
				Arrays.asList(result), result.length, cellHeight));
		for (String s : result) {
			Assert.isTrue(s.length() == cellWidth, String.format("'%s' had wrong length (%d), expected %d", s, s.length(),
					cellWidth));
		}
		return result;
	}
}
