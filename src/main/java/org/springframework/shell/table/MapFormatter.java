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

import java.util.Map;

/**
 * A formatter suited for key-value pairs, that renders each mapping on a new line.
 *
 * @author Eric Bottard
 */
public class MapFormatter implements Formatter {

	private final String separator;

	public MapFormatter(String separator) {
		this.separator = separator;
	}

	@Override
	public String[] format(Object value) {
		Map<?, ?> map = (Map<?, ?>) value;
		String[] result = new String[map.size()];
		int i = 0;
		for (Map.Entry<?, ?> kv : map.entrySet()) {
			result[i++] = kv.getKey() + separator + kv.getValue();
		}
		return result;
	}
}
