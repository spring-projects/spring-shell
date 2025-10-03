/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.table;

/**
 * This is used to specify where some components of a Table may be applied.
 *
 * <p>
 * Some commonly used matchers can be created <i>via</i> {@link CellMatchers}.
 * </p>
 *
 * @author Eric Bottard
 */
public interface CellMatcher {

	/**
	 * @return whether a given cell of the table should match.
	 * @param row the row being tested.
	 * @param column the column being tested
	 * @param model the data model of the table
	 */
	public boolean matches(int row, int column, TableModel model);

}
