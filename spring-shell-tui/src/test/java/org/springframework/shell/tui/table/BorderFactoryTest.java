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

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.shell.tui.table.BorderStyle.fancy_double;

/**
 * Tests for convenience borders factory.
 *
 * @author Eric Bottard
 */
class BorderFactoryTest extends AbstractTestWithSample {

	@Test
	void testOutlineBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new TableBuilder(model).addOutlineBorder(fancy_double).build();
		String result = table.render(80);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	void testFullBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new TableBuilder(model).addFullBorder(fancy_double).build();
		String result = table.render(80);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	void testHeaderBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new TableBuilder(model).addHeaderBorder(fancy_double).build();
		String result = table.render(80);
		assertThat(result).isEqualTo(sample());
	}

	@Test
	void testHeaderAndVerticalsBorder() throws IOException {
		TableModel model = generate(3, 3);
		Table table = new TableBuilder(model).addHeaderAndVerticalsBorders(fancy_double).build();
		String result = table.render(80);
		assertThat(result).isEqualTo(sample());
	}

}
