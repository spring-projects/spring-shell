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
 * Tests for BorderStyle rendering and combinations.
 *
 * @author Eric Bottard
 */
public class BorderStyleTests extends AbstractTestWithSample {

	@Test
	public void testOldSchool() throws IOException {
		Table table = new TableBuilder(generate(2, 2)).addFullBorder(BorderStyle.oldschool).build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testFancySimple() throws IOException {
		Table table = new TableBuilder(generate(2, 2)).addFullBorder(BorderStyle.fancy_light).build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testFancyHeavy() throws IOException {
		Table table = new TableBuilder(generate(2, 2)).addFullBorder(BorderStyle.fancy_heavy).build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testFancyDouble() throws IOException {
		Table table = new TableBuilder(generate(2, 2)).addFullBorder(BorderStyle.fancy_double).build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testAir() throws IOException {
		Table table = new TableBuilder(generate(2, 2)).addFullBorder(BorderStyle.air).build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedOldSchoolWithAir() throws IOException {
		Table table = new TableBuilder(generate(2, 2))
				.addFullBorder(BorderStyle.air)
				.addOutlineBorder(BorderStyle.oldschool)
				.build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedFancyLightAndHeavy() throws IOException {
		Table table = new TableBuilder(generate(2, 2))
				.addFullBorder(BorderStyle.fancy_heavy)
				.addOutlineBorder(BorderStyle.fancy_light)
				.build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedFancyHeavyAndLight() throws IOException {
		Table table = new TableBuilder(generate(2, 2))
				.addFullBorder(BorderStyle.fancy_light)
				.addOutlineBorder(BorderStyle.fancy_heavy)
				.build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedDoubleAndSingle() throws IOException {
		Table table = new TableBuilder(generate(2, 2))
				.addFullBorder(BorderStyle.fancy_light)
				.addOutlineBorder(BorderStyle.fancy_double)
				.build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedSingleAndDouble() throws IOException {
		Table table = new TableBuilder(generate(2, 2))
				.addFullBorder(BorderStyle.fancy_double)
				.addOutlineBorder(BorderStyle.fancy_light)
				.build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedLightInternalAndHeavy() throws IOException {
		Table table = new TableBuilder(generate(3, 3))
				.addFullBorder(BorderStyle.fancy_heavy)
				.paintBorder(BorderStyle.fancy_light, BorderSpecification.OUTLINE).fromRowColumn(1, 1).toRowColumn(2, 2)
				.build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testMixedHeavyInternalAndLight() throws IOException {
		Table table = new TableBuilder(generate(3, 3))
				.addFullBorder(BorderStyle.fancy_light)
				.paintBorder(BorderStyle.fancy_heavy, BorderSpecification.OUTLINE).fromRowColumn(1, 1).toRowColumn(2, 2)
				.build();
		assertThat(table.render(10), is(sample()));
	}

	@Test
	public void testHeavyOutlineAndHeader_LightVerticals_AirHorizontals() throws IOException {
		Table table = new TableBuilder(generate(4, 4))
				.addOutlineBorder(BorderStyle.fancy_heavy)
				.paintBorder(BorderStyle.fancy_light, BorderSpecification.INNER_VERTICAL).fromTopLeft().toBottomRight()
				.paintBorder(BorderStyle.air, BorderSpecification.INNER_HORIZONTAL).fromTopLeft().toBottomRight()
				.paintBorder(BorderStyle.fancy_heavy, BorderSpecification.OUTLINE).fromTopLeft().toRowColumn(1, 4)
				.build();
		assertThat(table.render(10), is(sample()));
	}
}
