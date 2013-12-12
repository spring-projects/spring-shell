/*
 * Copyright 2013 the original author or authors.
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

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for Tokenizer.
 * 
 * @author Eric Bottard
 */
public class TokenizerTests {

	@Test
	public void testEmpty() {
		Map<String, String> result = tokenize("");
		assertEquals(emptyMap(), result);
	}

	@Test
	public void testBlank() {
		Map<String, String> result = tokenize("  ");
		assertEquals(emptyMap(), result);
	}

	@Test
	public void testDefaultKey() {
		Map<String, String> result = tokenize("foo");
		assertEquals(singletonMap("", "foo"), result);
	}

	@Test
	public void testOneOption() {
		Map<String, String> result = tokenize("--foo bar");
		assertEquals(singletonMap("foo", "bar"), result);
	}

	@Test
	public void testTwoOptions() {
		Map<String, String> result = tokenize("--foo bar --fizz buzz");
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("foo", "bar");
		expected.put("fizz", "buzz");
		assertEquals(expected, result);
	}

	@Test
	public void testTwoOptionsOneWithDefault() {
		Map<String, String> result = tokenize("bar --fizz buzz");
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("", "bar");
		expected.put("fizz", "buzz");
		assertEquals(expected, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTwoOptionsSameKey() {
		tokenize("--foo bar --foo buzz");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTwoOptionsSameEmptyKey() {
		tokenize("bar buzz");
	}

	@Test
	public void testValueQuotation() {
		Map<String, String> result = tokenize("--foo \"bar fizz\"");
		assertEquals(singletonMap("foo", "bar fizz"), result);
	}

	@Test
	public void testExtraSpaces() {
		Map<String, String> result = tokenize("  --foo   \"bar fizz\"    --bozz  bizzz    \"the   default\"");
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("", "the   default");
		expected.put("foo", "bar fizz");
		expected.put("bozz", "bizzz");
		assertEquals(expected, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValueQuotationUnbalanced() {
		Map<String, String> result = tokenize("--foo \"bar fizz");
		assertEquals(singletonMap("foo", "bar fizz"), result);
	}

	@Test
	public void testValueQuotationEscaped() {
		Map<String, String> result = tokenize("--foo \"bar \\\"fizz\"");
		assertEquals(singletonMap("foo", "bar \"fizz"), result);
	}

	private Map<String, String> tokenize(String what) {
		return new Tokenizer(what).getTokens();
	}
}
