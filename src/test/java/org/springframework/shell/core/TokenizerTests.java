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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
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

	@Test(expected = TokenizingException.class)
	public void testTwoOptionsSameKey() {
		tokenize("--foo bar --foo buzz");
	}

	@Test
	public void testAllowTwoOptionsSameEmptyKeyIfNextToEachOther() {
		Map<String, String> result = tokenize("bar buzz");
		assertThat(result.keySet(), hasSize(1));
		assertThat(result.get(""), equalTo("bar buzz"));

		result = tokenize("--foo wizz bar buzz --woot cool");
		assertThat(result.keySet(), hasSize(3));
		assertThat(result.get(""), equalTo("bar buzz"));
		assertThat(result.get("foo"), equalTo("wizz"));
		assertThat(result.get("woot"), equalTo("cool"));
	}

	@Test(expected = TokenizingException.class)
	public void testDisallowTwoOptionsSameEmptyKeyIfNotNextToEachOther() {
		tokenize("bar --foo wizz buzz");
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

	@Test(expected = TokenizingException.class)
	public void testValueQuotationUnbalanced() {
		Map<String, String> result = tokenize("--foo \"bar fizz");
		assertEquals(singletonMap("foo", "bar fizz"), result);
	}

	@Test
	public void testValueQuotationEscaped() {
		Map<String, String> result = tokenize("--foo \"bar \\\"fizz\"");
		assertEquals(singletonMap("foo", "bar \"fizz"), result);
	}

	@Test
	public void testAllowKeyOnlyIfAtTheEnd() {
		Map<String, String> result = tokenize("--foo bar fizz --bozz ");
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("", "fizz");
		expected.put("foo", "bar");
		expected.put("bozz", "");
		assertEquals(expected, result);

	}

	@Test
	public void testValueQuotationAllowUnfinished() {
		Tokenizer tokenizer = new Tokenizer("--foo \"bar bazz\" --bizz \"unfinished bizness ", true);
		Map<String, String> result = tokenizer.getTokens();
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("foo", "bar bazz");
		expected.put("bizz", "unfinished bizness ");
		assertEquals(expected, result);
		assertTrue(tokenizer.openingQuotesHaveNotBeenClosed());
	}

	@Test
	public void testValueQuotationAllowUnfinishedEvenWithEmptyContent() {
		Tokenizer tokenizer = new Tokenizer("--foo \"bar bazz\" --bizz \"", true);
		Map<String, String> result = tokenizer.getTokens();
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("foo", "bar bazz");
		expected.put("bizz", "");
		assertEquals(expected, result);
		assertTrue(tokenizer.openingQuotesHaveNotBeenClosed());
	}

	@Test
	public void testPendingDashDash() {
		Map<String, String> result = tokenize("--foo bar --");
		assertEquals(singletonMap("foo", "bar"), result);
	}

	@Test
	public void testKeyWithDefaultValueAtEnd() {
		Map<String, String> result = tokenize("--foo bar --recursive");
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("foo", "bar");
		expected.put("recursive", "");
		assertEquals(expected, result);
	}

	@Test
	public void testKeyWithDefaultValueNotAtEnd() {
		Map<String, String> result = tokenize("--foo bar --recursive --wizz blow");
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("foo", "bar");
		expected.put("recursive", "");
		expected.put("wizz", "blow");
		assertEquals(expected, result);
	}

	@Test
	public void testValueThatStartsWithDashDashStillSupportedIfQuoted() {
		Map<String, String> result = tokenize("--foo bar --recursive \"--wizz\" blow");
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("foo", "bar");
		expected.put("recursive", "--wizz");
		expected.put("", "blow");
		assertEquals(expected, result);

	}

	@Test
	public void testEscapeSequences() {
		Map<String, String> result = tokenize("--foo \\t\\n\\r\\f");
		assertEquals(Collections.singletonMap("foo", "\t\n\r\f"), result);

		// Unicode and space-escape
		result = tokenize("--foo \\u65e5\\ \\u672c");
		assertEquals(Collections.singletonMap("foo", "\u65e5 \u672c"), result);

		// backslash escape
		result = tokenize("--foo \\\\u\\g");
		assertEquals(Collections.singletonMap("foo", "\\u\\g"), result);
	}
	
	public void testSingleAndDoubleQuotingBehavior() {
		Map<String, String> result = tokenize("--foo 'i have a double \" in me'");
		assertEquals(Collections.singletonMap("foo", "i have a double \" in me"), result);

		result = tokenize("--foo \"i have a double \\\" in me\"");
		assertEquals(Collections.singletonMap("foo", "i have a double \" in me"), result);

		result = tokenize("--foo \"i have a single ' in me\"");
		assertEquals(Collections.singletonMap("foo", "i have a single ' in me"), result);

		result = tokenize("--foo 'i have a single \\' in me'");
		assertEquals(Collections.singletonMap("foo", "i have a single ' in me"), result);

	}

	private Map<String, String> tokenize(String what) {
		return new Tokenizer(what).getTokens();
	}
}
