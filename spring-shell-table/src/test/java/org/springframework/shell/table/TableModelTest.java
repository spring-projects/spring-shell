/*
 * Copyright 2015-present the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for TableModel.
 *
 * @author Eric Bottard
 */
class TableModelTest {

	@Test
	void testTranspose() {
		TableModel model = new ArrayTableModel(new String[][] {{"a", "b", "c"}, {"d", "e", "f"}});

		assertThat(model.transpose().getColumnCount()).isEqualTo(2);
		assertThat(model.transpose().getRowCount()).isEqualTo(3);
		assertThat(model.transpose().getValue(2, 1)).isEqualTo("f");
	}
}
