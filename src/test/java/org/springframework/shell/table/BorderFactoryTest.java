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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.shell.table.BorderStyle.fancy_double;

import java.io.IOException;

import org.junit.Test;

/**
 * Tests for BorderFactory.
 *
 * @author Eric Bottard
 */
public class BorderFactoryTest extends AbstractTestWithSample {

	@Test
	public void testOutlineBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new Table(model);
		BorderFactory.outline(table, fancy_double);
		String result = table.render(80);
		assertThat(result, equalTo(sample()));
	}

	@Test
	public void testFullBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new Table(model);
		BorderFactory.full(table, fancy_double);
		String result = table.render(80);
		assertThat(result, equalTo(sample()));
	}

	@Test
	public void testHeaderBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new Table(model);
		BorderFactory.header(table, fancy_double);
		String result = table.render(80);
		assertThat(result, equalTo(sample()));
	}

	@Test
	public void testHeaderAndVerticalsBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new Table(model);
		BorderFactory.headerAndVerticals(table, fancy_double);
		String result = table.render(80);
		assertThat(result, equalTo(sample()));
	}
}