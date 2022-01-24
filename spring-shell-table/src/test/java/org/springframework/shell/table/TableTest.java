/*
 * Copyright 2015-2022 the original author or authors.
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
import static org.springframework.shell.table.SimpleHorizontalAligner.*;
import static org.springframework.shell.table.SimpleVerticalAligner.*;

import java.io.IOException;


/**
 * Tests for Table rendering.
 *
 * @author Eric Bottard
 */
public class TableTest extends AbstractTestWithSample {

	@Test
	public void testEmptyModel() {
		TableModel model = new ArrayTableModel(new Object[0][0]);
		Table table = new TableBuilder(model).build();
		String result = table.render(80);
		assertThat(result).isEqualTo("");
	}

	@Test
	public void testPreformattedModel() {
		TableModel model = generate(2, 2);
		Table table = new TableBuilder(model).build();
		String result = table.render(80);
		assertThat(result).isEqualTo("ab\ncd\n");
	}

	@Test
	public void testExpandingColumns() throws IOException {
		TableModel model = new ArrayTableModel(new String[][] {{"a", "b"}, {"ccc", "d"}});
		Table table = new TableBuilder(model).build();
		String result = table.render(80);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	public void testRightAlignment() throws IOException {
		TableModel model = new ArrayTableModel(new String[][] {{"a\na\na", "bbb"}, {"ccc", "d"}});
		Table table = new TableBuilder(model).on(CellMatchers.column(1)).addAligner(right).build();
		String result = table.render(80);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	public void testVerticalAlignment() throws IOException {
		TableModel model = new ArrayTableModel(new String[][] {{"a\na\na", "bbb"}, {"ccc", "d"}});
		Table table = new TableBuilder(model).on(CellMatchers.row(0)).addAligner(middle).build();
		String result = table.render(80);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	public void testAutoWrapping() throws IOException {
		TableModel model = new ArrayTableModel(new String[][] {{"this is a long line", "bbb"}, {"ccc", "d"}});
		Table table = new TableBuilder(model).build();
		String result = table.render(10);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	public void testOverflow() throws IOException {
		TableModel model = new ArrayTableModel(new String[][] {{"this is a long line", "bbb"}, {"ccc", "d"}});
		Table table = new TableBuilder(model).build();
		String result = table.render(3);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	public void testEmptyCellsVerticalAligner() throws IOException {
		TableModel model = new ArrayTableModel(new String[][] {{"a", "b"}, {null, null}});
		Table table = new TableBuilder(model).on(CellMatchers.table()).addAligner(SimpleVerticalAligner.middle).build();
		table.render(3);
	}
}
