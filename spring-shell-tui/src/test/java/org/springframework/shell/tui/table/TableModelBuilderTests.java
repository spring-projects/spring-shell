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

package org.springframework.shell.tui.table;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for TableModelBuilder.
 *
 * @author Eric Bottard
 */
class TableModelBuilderTests {

	@Test
	void emptyModel() {
		TableModelBuilder<Number> builder = new TableModelBuilder<>();
		TableModel model = builder.build();
		assertThat(model.getColumnCount()).isZero();
		assertThat(model.getColumnCount()).isZero();
	}

	@Test
	void testFrozen() {
		assertThatThrownBy(() -> {
			TableModelBuilder<Number> builder = new TableModelBuilder<>();
			builder.addRow().addValue(5);
			builder.build();
			builder.addRow();
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testAddingTooManyValues() {
		assertThatThrownBy(() -> {
			TableModelBuilder<Number> builder = new TableModelBuilder<>();
			builder.addRow().addValue(5);
			builder.addRow().addValue(1).addValue(2);
			builder.build();
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testAddingLessValues() {
		assertThatThrownBy(() -> {
			TableModelBuilder<Number> builder = new TableModelBuilder<>();
			builder.addRow().addValue(1).addValue(2);
			builder.addRow().addValue(5);
			builder.addRow();
			builder.build();
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void simpleBuild() {
		TableModelBuilder<Number> builder = new TableModelBuilder<>();
		builder
			.addRow()
			.addValue(7).addValue(2)
			.addRow()
			.addValue(3).addValue(5.5)
			.addRow()
			.addValue(1).addValue(4);

		TableModel model = builder.build();
		assertThat(model.getColumnCount()).isEqualTo(2);
		assertThat(model.getRowCount()).isEqualTo(3);
		assertThat(model.getValue(1, 1)).isEqualTo(5.5);
	}
}
