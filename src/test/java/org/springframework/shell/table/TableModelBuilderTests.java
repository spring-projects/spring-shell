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

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for TableModelBuilder.
 *
 * @author Eric Bottard
 */
public class TableModelBuilderTests {

	@Test
	public void emptyModel() {
		TableModelBuilder<Number> builder = new TableModelBuilder<Number>();
		TableModel model = builder.build();
		Assert.assertThat(model.getColumnCount(), is(0));
		Assert.assertThat(model.getRowCount(), is(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFrozen() {
		TableModelBuilder<Number> builder = new TableModelBuilder<Number>();
		builder.addRow().addValue(5);
		builder.build();
		builder.addRow();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddingTooManyValues() {
		TableModelBuilder<Number> builder = new TableModelBuilder<Number>();
		builder.addRow().addValue(5);
		builder.addRow().addValue(1).addValue(2);
		builder.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddingLessValues() {
		TableModelBuilder<Number> builder = new TableModelBuilder<Number>();
		builder.addRow().addValue(1).addValue(2);
		builder.addRow().addValue(5);
		builder.addRow();
		builder.build();
	}

	@Test
	public void simpleBuild() {
		TableModelBuilder<Number> builder = new TableModelBuilder<Number>();
		builder
			.addRow()
			.addValue(7).addValue(2)
			.addRow()
			.addValue(3).addValue(5.5)
			.addRow()
			.addValue(1).addValue(4);

		TableModel model = builder.build();
		Assert.assertThat(model.getColumnCount(), is(2));
		Assert.assertThat(model.getRowCount(), is(3));
		Assert.assertThat(model.getValue(1, 1), is((Object)5.5));

	}
}