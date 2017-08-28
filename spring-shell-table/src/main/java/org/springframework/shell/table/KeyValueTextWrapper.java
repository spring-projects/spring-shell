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
 * A TextWrapper implementation tailored for key-value rendering (working in concert
 * with {@link KeyValueSizeConstraints}, {@link KeyValueHorizontalAligner}), that tries its
 * best to vertically align some delimiter character (default '=').
 */
public class KeyValueTextWrapper implements TextWrapper {

	private final String delimiter;

	public KeyValueTextWrapper() {
		this("=");
	}

	public KeyValueTextWrapper(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String[] wrap(String[] original, int columnWidth) {
		List<String> result = new ArrayList<String>();
		for (String line : original) {
			line = line.trim();
			while (line.length() > columnWidth) {
				int cut = line.lastIndexOf(delimiter, columnWidth);
				if (cut == -1) {
					cut = columnWidth;
				}
				else if (cut + delimiter.length() <= columnWidth) {
					cut = cut + delimiter.length();
				}
				result.add(rightPad(line.substring(0, cut), columnWidth));
				line = line.substring(cut).trim();
			}
			if (line.length() > 0) {
				result.add(rightPad(line, columnWidth));
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private String rightPad(String raw, int width) {
		StringBuilder result = new StringBuilder(raw);
		for (int i = raw.length(); i < width; i++) {
			result.append(' ');
		}
		return result.toString();
	}
}
