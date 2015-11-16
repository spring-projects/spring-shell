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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import org.springframework.shell.converters.StringConverter;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.event.ParseResult;

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
	public void testSHL170_ValueMadeOfSpacesOK() {
		parser.add(new MyCommands());
		parser.add(new StringConverter());

		// A value made of space(s) is ok
		ParseResult result = parser.parse("bar --option1 ' '");
		assertThat(result.getMethod().getName(), equalTo("bar"));
		assertThat(result.getArguments(), arrayContaining((Object) " "));

		// Ok too with a literal TAB
		result = parser.parse("bar --option1 '\t'");
		assertThat(result.getMethod().getName(), equalTo("bar"));
		assertThat(result.getArguments(), arrayContaining((Object)"\t"));

		// Also Ok when Shell itself does the escaping
		result = parser.parse("bar --option1 '\\t'");
		assertThat(result.getMethod().getName(), equalTo("bar"));
		assertThat(result.getArguments(), arrayContaining((Object)"\t"));

	}

	@Test
	public void testSimpleCommandNameCompletion() {
		parser.add(new MyCommands());

		buffer = "f";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("foo ")))));
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
	public void testArgumentValueCompletionAlreadyGiven() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abc", "def", "ghi")));

		buffer = "bar --option1 def";
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
	public void testWithDefaultKey() {
		parser.add(new MyCommands());

		buffer = "testDefaultKey thevalue --optio";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("testDefaultKey thevalue --option2 ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("testDefaultKey thevalue --option3 ")))));
		assertThat(candidates, not(hasItem(completionThat(containsString("option1")))));

		// Let's do it again with default key in the middle
		candidates.clear();
		buffer = "testDefaultKey --option3 foo thevalue --optio";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("testDefaultKey --option3 foo thevalue --option2 ")))));
		assertThat(candidates, not(hasItem(completionThat(endsWith("option3 ")))));

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

	@Test
	public void testAtEndOfKeyOption() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abd", "def")));

		buffer = "fileMore";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("fileMore --option ")))));

		// Do it again with a trailing space
		buffer = "fileMore ";
		candidates.clear();
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("fileMore --option ")))));

	}

	@Test
	public void testAtEndOfKeyOptionWithEvenLongerKeyOption() {
		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abd", "def")));

		buffer = "file";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("file ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("fileMore ")))));

		// Do it again with a trailing space
		buffer = "file ";
		candidates.clear();
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("file --option ")))));
		assertThat(candidates, not(hasItem(completionThat(startsWith("fileMore")))));
	}

	@Test
	public void testPrefixMatching() {
		assertThat(SimpleParser.isMatch("hello", "hello", true), is(equalTo("")));
		assertThat(SimpleParser.isMatch("hello there", "hello", true), is(equalTo("there")));
		assertThat(SimpleParser.isMatch("hello there", "hello there", true), is(equalTo("")));
		assertThat(SimpleParser.isMatch("hell", "hello", true), is(equalTo("")));

		assertThat(SimpleParser.isMatch("hello", "hello there", true), is(nullValue()));
		assertThat(SimpleParser.isMatch("hello", "hello there", false), is(equalTo("")));

		assertThat(SimpleParser.isMatch("hi", "hello", true), is(nullValue()));

		assertThat(SimpleParser.isMatch("hello", "hellothere", false), is(equalTo("")));
		assertThat(SimpleParser.isMatch("hello", "hellothere", true), is(equalTo("")));
		// TODO
		// assertThat(SimpleParser.isMatch("hello ", "hellothere", true), is(nullValue()));
	}

	@Test
	public void testValueCompletionsThatCanContinue() {

		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abd", "def"), false));

		// With space as delimiter
		buffer = "bar --option1 ";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 abd")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 def")))));

		// With quotes as delimiter
		buffer = "bar --option1 \"";
		candidates.clear();
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"abd")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"def")))));
	}

	@Test
	public void testValueCompletionsThatCannotContinue() {

		parser.add(new MyCommands());
		parser.add(new StringCompletions(Arrays.asList("abd", "def"), true));

		// With space as delimiter
		buffer = "bar --option1 ";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 abd ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 def ")))));

		// With quotes as delimiter
		buffer = "bar --option1 \"";
		candidates.clear();
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"abd\" ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 \"def\" ")))));
	}

	@Test
	public void testSuccessiveInvocationsOfCompletion() {

		parser.add(new MyCommands());
		parser.add(new ExpertCompletions());

		buffer = "bar --option1 ";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 one ")))));
		assertThat(candidates, not(hasItem(completionThat(is(equalTo("bar --option1 two "))))));
		assertThat(candidates, not(hasItem(completionThat(is(equalTo("bar --option1 three "))))));

		candidates.clear();
		// Simulate <TAB> twice
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 one ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 two ")))));
		assertThat(candidates, not(hasItem(completionThat(is(equalTo("bar --option1 three "))))));

		candidates.clear();
		// Simulate <TAB> three times
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 one ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 two ")))));
		assertThat(candidates, hasItem(completionThat(is(equalTo("bar --option1 three ")))));

		// And now for something completely different
		buffer = "testMandatory --option2 ";
		candidates.clear();
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, hasItem(completionThat(is(equalTo("testMandatory --option2 one ")))));
		assertThat(candidates, not(hasItem(completionThat(is(equalTo("testMandatory --option2 two "))))));
		assertThat(candidates, not(hasItem(completionThat(is(equalTo("testMandatory --option2 three "))))));

	}

	/**
	 * @see https://jira.spring.io/browse/SHL-113
	 */
	@Test
	public void testFalseAmbiguity() {
		parser.add(new SamePrefixCommands());
		ParseResult result = parser.parse("foo");
		assertThat(result.getMethod().getName(), equalTo("foo"));
	}

	@Test
	public void testRealAmbiguity() {
		parser.add(new SamePrefixCommands());
		ParseResult result = parser.parse("fo");
		assertThat(result, nullValue(ParseResult.class));
	}

	@Test
	public void testCommandPrefixCollisionFalseAmbiguity() {
		parser.add(new SamePrefixCommands());
		buffer = "foo ";
		offset = parser.completeAdvanced(buffer, buffer.length(), candidates);

		assertThat(candidates, not(hasItem(completionThat(startsWith("fooBar ")))));
	}

	@Test
	public void testMixedCaseOptions_SHL_98() {
		parser.add(new MixedCaseOptions());
		parser.add(new StringConverter());
		ParseResult result = parser.parse("foo --upPer bar");
		assertThat(result.getMethod().getName(), equalTo("foo"));

		result = parser.parse("foo --upper fizz");
		assertThat(result, nullValue());
	}

	/**
	 * Return a matcher that asserts that a completion, when added to {@link #buffer} at the given {@link #offset},
	 * indeed matches the provided matcher.
	 */
	private Matcher<Completion> completionThat(final Matcher<String> matcher) {
		return new DiagnosingMatcher<Completion>() {

			@Override
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

		@CliCommand("testDefaultKey")
		public void testDefaultKey(@CliOption(key = "")
		String option1, @CliOption(key = "option2")
		String option2, @CliOption(key = "option3")
		String option3) {

		}

		@CliCommand("file")
		public void file(@CliOption(key = "option", mandatory = true)
		String option) {

		}

		@CliCommand("fileMore")
		public void fileMore(@CliOption(key = "option", mandatory = true)
		String option) {

		}
	}

	public static class SamePrefixCommands implements CommandMarker {
		@CliCommand(value = "foo")
		public String foo(@CliOption(key = "")
		String arg) {
			return "foo " + arg;
		}

		@CliCommand(value = "fooBar")
		public String fooBar(@CliOption(key = "")
		String arg) {
			return "fooBar " + arg;
		}
	}

	public static class MixedCaseOptions implements CommandMarker {
		@CliCommand(value = "foo")
		public String foo(@CliOption(key = "upPer")
		String arg) {
			return "foo " + arg;
		}


	}



	public static class StringCompletions implements Converter<String> {

		private final List<String> completions;

		private final boolean canContinue;

		public StringCompletions(List<String> completions) {
			this(completions, false);
		}

		public StringCompletions(List<String> completions, boolean canContinue) {
			this.completions = completions;
			this.canContinue = canContinue;
		}

		@Override
		public boolean supports(Class<?> type, String optionContext) {
			return type == String.class;
		}

		@Override
		public String convertFromText(String value, Class<?> targetType, String optionContext) {
			return value;
		}

		@Override
		public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData,
				String optionContext, MethodTarget target) {
			for (String s : this.completions) {
				completions.add(new Completion(s));
			}
			return canContinue;
		}

	}

	public static class ExpertCompletions implements Converter<String> {

		private static final Pattern NUMBER_OF_COMPLETIONS_CAPTURE = Pattern.compile(".*completion-count-(\\d+).*");

		private static final String[] results = new String[] { "one", "two", "three" };

		@Override
		public boolean supports(Class<?> type, String optionContext) {
			return true;
		}

		@Override
		public String convertFromText(String value, Class<?> targetType, String optionContext) {
			return value;
		}

		@Override
		public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData,
				String optionContext, MethodTarget target) {
			java.util.regex.Matcher m = NUMBER_OF_COMPLETIONS_CAPTURE.matcher(optionContext);
			Assert.assertTrue(m.matches());
			int invocations = Integer.parseInt(m.group(1));
			for (int i = 0; i < invocations; i++) {
				completions.add(new Completion(results[i]));
			}
			return true;
		}

	}

}
