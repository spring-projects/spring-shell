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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for ArrayTableModel.
 *
 * @author Eric Bottard
 */
public class ArrayTableModelTest {

	@Test
	public void testValid() {
		TableModel model = new ArrayTableModel(new String[][] {{"a", "b"}, {"c", "d"}});
		assertThat(model.getColumnCount(), equalTo(2));
		assertThat(model.getRowCount(), equalTo(2));
		assertThat(model.getValue(0, 1), equalTo((Object) "b"));
	}

	@Test
	public void testEmpty() {
		TableModel model = new ArrayTableModel(new String[][] {});
		assertThat(model.getColumnCount(), equalTo(0));
		assertThat(model.getRowCount(), equalTo(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidDimensions() {
		new ArrayTableModel(new String[][] {{"a", "b"}, {"c", "d", "e"}});
	}

}