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

/**
 * A cell sizing strategy that forces a fixed width, expressed in number of characters.
 *
 * @author Eric Bottard
 */
public class AbsoluteWidthSizeConstraints implements SizeConstraints {

	private final int width;

	public AbsoluteWidthSizeConstraints(int width) {
		this.width = width;
	}


	@Override
	public Extent width(String[] raw, int previous, int tableWidth) {
		return new Extent(width, width);
	}
}
