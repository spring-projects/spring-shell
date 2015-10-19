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
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests related to rendering Maps.
 *
 * @author Eric Bottard
 */
public class KeyValueRenderingTests extends AbstractTestWithSample {

	@Test
	public void testRenderConstrained() throws IOException {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("a", "b");
		values.put("long-key", "c");
		values.put("d", "long-value");
		TableModel model = new ArrayTableModel(new Object[][] {{"Thing", "Properties"}, {"Something", values}});
		TableBuilder tableBuilder = new TableBuilder(model)
				.addHeaderAndVerticalsBorders(BorderStyle.fancy_light);
		Tables.configureKeyValueRendering(tableBuilder, " = ");
		Table table = tableBuilder.build();
		String result = table.render(10);
		assertThat(result, is(sample()));

	}

	@Test
	public void testRenderUnconstrained() throws IOException {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("a", "b");
		values.put("long-key", "c");
		values.put("d", "long-value");
		TableModel model = new ArrayTableModel(new Object[][] {{"Thing", "Properties"}, {"Something", values}});
		TableBuilder tableBuilder = new TableBuilder(model)
				.addHeaderAndVerticalsBorders(BorderStyle.fancy_light);
		Tables.configureKeyValueRendering(tableBuilder, " = ");
		Table table = tableBuilder.build();
		String result = table.render(80);
		assertThat(result, is(sample()));

	}
}
