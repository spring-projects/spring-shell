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

import org.junit.Test;

/**
 * Tests for BorderStyle rendiring and combinations.
 *
 * @author Eric Bottard
 */
public class BorderStyleTests extends AbstractTestWithSample {

	@Test
	public void testOldSchool() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.oldschool);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testFancySimple() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.fancy_light);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testFancyHeavy() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.fancy_heavy);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testFancyDouble() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.fancy_double);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testAir() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.air);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedOldSchoolWithAir() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.air);
		BorderFactory.outline(table, BorderStyle.oldschool);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedFancyLightAndHeavy() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.fancy_heavy);
		BorderFactory.outline(table, BorderStyle.fancy_light);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedFancyHeavyAndLight() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.fancy_light);
		BorderFactory.outline(table, BorderStyle.fancy_heavy);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedDoubleAndSingle() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.fancy_light);
		BorderFactory.outline(table, BorderStyle.fancy_double);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedSingleAndDouble() throws IOException {
		Table table = new Table(generate(2, 2));
		BorderFactory.full(table, BorderStyle.fancy_double);
		BorderFactory.outline(table, BorderStyle.fancy_light);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedLightInternalAndHeavy() throws IOException {
		Table table = new Table(generate(3, 3));
		BorderFactory.full(table, BorderStyle.fancy_heavy);
		table.addBorder(1, 1, 2, 2, BorderSpecification.OUTLINE, BorderStyle.fancy_light);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedHeavyInternalAndLight() throws IOException {
		Table table = new Table(generate(3, 3));
		BorderFactory.full(table, BorderStyle.fancy_light);
		table.addBorder(1, 1, 2, 2, BorderSpecification.OUTLINE, BorderStyle.fancy_heavy);
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testHeavyOutlineAndHeader_LightVerticals_AirHorizontals() throws IOException {
		Table table = new Table(generate(4, 4));
		BorderFactory.outline(table, BorderStyle.fancy_heavy);
		table.addBorder(0, 0, 4, 4, BorderSpecification.INNER_VERTICAL, BorderStyle.fancy_light);
		table.addBorder(0, 0, 4, 4, BorderSpecification.INNER_HORIZONTAL, BorderStyle.air);
		table.addBorder(0, 0, 1, 4, BorderSpecification.OUTLINE, BorderStyle.fancy_heavy);
		assertThat(table.render(10), is(sample()));
	}
}
