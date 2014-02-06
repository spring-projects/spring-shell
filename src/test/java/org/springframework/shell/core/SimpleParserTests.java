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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;

/**
 * Tests for parsing and completion logic.
 * 
 * @author Eric Bottard
 */
public class SimpleParserTests {

	private SimpleParser parser = new SimpleParser();

	private int offset;

	private String buffer;

	private ArrayList<Completion> candidates = new ArrayList<Completion>();

	@Test
	public void testSimpleCommandNameCompletion() {
		parser.add(new MyCommands());

		buffer = "f";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("foo")))));
	}

	@Test
	public void testSimpleArgumentNameCompletion() {
		parser.add(new MyCommands());

		buffer = "bar --op";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 ")))));
	}

	@Test
	public void testSimpleArgumentValueCompletion() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abc", "def", "ghi")));

		buffer = "bar --option1 a";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 abc")))));
	}

	@Test
	public void testArgumentValueCompletionWhenQuoted() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abc", "def", "ghi")));

		buffer = "bar --option1 \"a";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"abc")))));
	}

	@Test
	public void testCompletionInMiddleOfBuffer() {
		parser.add(new MyCommands());

		buffer = "bar --optimum";
		offset = parser.completeAdvanced(buffer, "bar --opti".length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 ")))));

	}

	@Test
	public void testArgumentValueCompletionWhenAmbiguity() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abc", "def", "abd")));

		buffer = "bar --option1 a";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(startsWith("bar --option1 ab"))));
	}

	@Test
	public void testArgumentValueCompletionWhenAmbiguityUsingQuotes() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abc", "def", "abd")));

		buffer = "bar --option1 \"a";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"abc")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"abd")))));
	}

	@Test
	public void testArgumentValueCompletionUnderstandsEndQuote() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abc", "def", "abd")));

		buffer = "bar --option1 \"ab\"";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, is(empty()));
	}

	@Test
	public void testDashDashIntoOption() {
		parser.add(new MyCommands());

		buffer = "bar --";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 ")))));
	}

	@Test
	@Ignore("TODO")
	public void testArgumentValueWithEscapedQuotes() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("he said \"hello\" to me")));

		buffer = "bar --option1 \"he said \\\"he";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"he said \\\"hello\\\" to me")))));

	}

	@Test
	public void testNoCompletionsFound() {
		parser.add(new MyCommands());

		buffer = "notthere";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, empty());

	}

	@Test
	public void testCommandAmbiguity() {
		parser.add(new MyCommands());

		buffer = "b";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bing ")))));

	}

	@Test
	public void testUnfinishedCommandThatHasParams() {
		parser.add(new MyCommands());

		buffer = "ba";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar ")))));

	}

	@Test
	public void testDontMisinterpretDashDashInArgumentValue() {
		parser.add(new MyCommands());

		buffer = "bar --option1 \"I end with --";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, empty());

	}

	@Test
	public void testStopAtFirstWhenMandatory() {
		parser.add(new MyCommands());

		buffer = "testMandatory --";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("testMandatory --option1 ")))));
		assertThat(candidates, not(hasItem(completionThat(is(equalTo("testMandatory --option2 "))))));
		assertThat(candidates, not(hasItem(completionThat(is(equalTo("testMandatory --option3 "))))));

	}

	@Test
	public void testDontStopAtFirstWhenNotMandatory() {
		parser.add(new MyCommands());

		buffer = "testNotMandatory --";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("testNotMandatory --option1 ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("testNotMandatory --option2 ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("testNotMandatory --option3 ")))));

	}

	/**
	 * Return a matcher that asserts that a completion, when added to {@link #buffer} at the given {@link #offset},
	 * indeed matches the provided matcher.
	 */
	private Matcher<Completion> completionThat(final Matcher<String> matcher) {
		return new DiagnosingMatcher<Completion>() {

			public void describeTo(Description description) {
				description.appendText("a completion that ").appendDescriptionOf(matcher);
			}

			@Override
			protected boolean matches(Object item, Description mismatchDescription) {
				Completion completion = (Completion) item;
				StringBuilder sb = new StringBuilder(buffer);
				sb.setLength(offset);
				sb.append(completion.getValue());
				boolean match = matcher.matches(sb.toString());
				mismatchDescription.appendText("result was ")
						.appendValue(sb.insert(offset, '[').append(']').toString());
				return match;
			}
		};
	}

	public static class MyCommands implements CommandMarker {

		@CliCommand("foo")
		public void foo() {

		}

		@CliCommand("bar")
		public void bar(@CliOption(key = "option1")
		String option1) {

		}

		@CliCommand("bing")
		public void bang() {

		}

		@CliCommand("testMandatory")
		public void testMandatory(@CliOption(key = "option1", mandatory = true)
		String option1, @CliOption(key = "option2", mandatory = true)
		String option2, @CliOption(key = "option3")
		String option3) {

		}

		@CliCommand("testNotMandatory")
		public void testNotMandatory(@CliOption(key = "option1")
		String option1, @CliOption(key = "option2")
		String option2, @CliOption(key = "option3")
		String option3) {

		}
	}

	public static class StringCompletions implements Converter<String> {

		private final List<String> completions;

		public StringCompletions(List<String> completions) {
			this.completions = completions;
		}

		public boolean supports(Class<?> type, String optionContext) {
			return type == String.class;
		}

		public String convertFromText(String value, Class<?> targetType, String optionContext) {
			return value;
		}

		public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData,
				String optionContext, MethodTarget target) {
			for (String s : this.completions) {
				completions.add(new Completion(s));
			}
			return false;
		}

	}

}
