/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.shell.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.shell.core.SimpleParser;

/**
 * Unit test of {@link SimpleParser}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class SimpleParserTest {

	// Fixture
	private SimpleParser simpleParser;

	@Before
	public void setUp() {
		this.simpleParser = new SimpleParser();
	}

	@Test
	public void testNormaliseEmptyString() {
		assertNormalised("", "");
	}

	@Test
	public void testNormaliseSpaces() {
		assertNormalised("    ", "");
	}

	@Test
	public void testNormaliseSingleWord() {
		assertNormalised("hint", "hint");
	}

	@Test
	public void testNormaliseMultipleWords() {
		assertNormalised(" security   setup ", "security setup");
	}

	/**
	 * Asserts that normalising the given input produces the given output
	 *
	 * @param input can't be <code>null</code>
	 * @param output
	 */
	private void assertNormalised(final String input, final String output) {
		Assert.assertEquals(output, simpleParser.normalise(input));
	}
}
