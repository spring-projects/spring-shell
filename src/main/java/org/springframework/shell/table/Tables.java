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
 * Utility class used to create and configure typical Tables.
 *
 * @author Eric Bottard
 */
public class Tables {

	/**
	 * Install all the necessary formatters, aligners, etc for key-value rendering of Maps.
	 */
	public static TableBuilder configureKeyValueRendering(TableBuilder builder, String delimiter) {
		return builder.on(CellMatchers.ofType(Map.class))
				.addFormatter(new MapFormatter(delimiter))
				.addAligner(new KeyValueHorizontalAligner(delimiter.trim()))
				.addSizer(new KeyValueSizeConstraints(delimiter))
				.addWrapper(new KeyValueTextWrapper(delimiter)).and();
	}
}
