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

import java.util.ArrayList;
import java.util.List;

/**
 * A Text wrapper that wraps at "word" boundaries. The default delimiter is the space character.
 *
 * @author Eric Bottard
 */
public class DelimiterTextWrapper implements TextWrapper {

	private final char delimiter;

	public DelimiterTextWrapper() {
		this(' ');
	}

	public DelimiterTextWrapper(char delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String[] wrap(String[] original, int columnWidth) {
		List<String> result = new ArrayList<String>(original.length);
		for (String line : original) {
			while (line.length() > columnWidth) {
				int split = line.lastIndexOf(delimiter, columnWidth);
				String toAdd = split == -1 ? line.substring(0, columnWidth) : line.substring(0, split);
				result.add(String.format("%-" + columnWidth + "s", toAdd));
				line = line.substring(split == -1 ? columnWidth : split + 1);
			}
			if (columnWidth > 0) {
				result.add(String.format("%-" + columnWidth + "s", line)); // right pad if necessary
			}
		}
		return result.toArray(new String[result.size()]);
	}
}
