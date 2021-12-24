/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.shell.standard;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.junit.jupiter.api.Test;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.ParameterMissingResolutionException;
import org.springframework.shell.UnfinishedParameterResolutionException;
import org.springframework.shell.Utils;
import org.springframework.shell.ValueResult;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.shell.ValueResultAsserts.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * Unit tests for DefaultParameterResolver.
 * @author Eric Bottard
 * @author Florent Biville
 */
public class StandardParameterResolverTest {

	// private StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(), Collections.emptySet());

	// Tests for resolution

	@Test
	public void testParses() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		List<String> words = asList("--force --name --foo y".split(" "));
		ValueResult result0 = resolver.resolve(Utils.createMethodParameter(method, 0), words);
		assertThat(result0).hasValue(true).usesWords(0).notUsesWordsForValue();
		assertThat(result0.wordsUsed(words)).containsExactly("--force");

		ValueResult result1 = resolver.resolve(Utils.createMethodParameter(method, 1), words);
		assertThat(result1).hasValue("--foo").usesWords(1, 2).usesWordsForValue(2);
		assertThat(result1.wordsUsed(words)).containsExactly("--name", "--foo");
		assertThat(result1.wordsUsedForValue(words)).containsExactly("--foo");

		ValueResult result2 = resolver.resolve(Utils.createMethodParameter(method, 2), words);
		assertThat(result2).hasValue("y").usesWords(3).usesWordsForValue(3);
		assertThat(result2.wordsUsed(words)).containsExactly("y");

		ValueResult result3 = resolver.resolve(Utils.createMethodParameter(method, 3), words);
		assertThat(result3).hasValue("last").notUsesWords().notUsesWordsForValue();
	}

	@Test
	public void testParsesWithMethodPrefix() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "prefixTest", String.class);

		ValueResult result = resolver.resolve(Utils.createMethodParameter(method, 0),
				asList("-message abc".split(" ")));
		assertThat(result).hasValue("abc").usesWords(0, 1).usesWordsForValue(1);
	}

	@Test
	public void testParameterSpecifiedTwiceViaDifferentAliases() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		assertThatThrownBy(() -> {
			resolver.resolve(
					Utils.createMethodParameter(method, 0),
					asList("--force --name --foo y --bar x --baz z".split(" ")));
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Named parameter has been specified multiple times via '--bar, --baz'");
	}

	@Test
	public void testParameterSpecifiedTwiceViaSameKey() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		assertThatThrownBy(() -> {
			resolver.resolve(
					Utils.createMethodParameter(method, 0),
					asList("--force --name --foo y --baz x --baz z".split(" ")));
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Parameter for '--baz' has already been specified");
	}

	@Test
	public void testTooMuchInput() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		assertThatThrownBy(() -> {
			resolver.resolve(
					Utils.createMethodParameter(method, 0),
					asList("--foo hello --name bar --force --bar well leftover".split(" ")));
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("the following could not be mapped to parameters: 'leftover'");
	}

	@Test
	public void testIncompleteCommandResolution() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "shutdown", Remote.Delay.class);

		assertThatThrownBy(() -> {
			resolver.resolve(
					Utils.createMethodParameter(method, 0),
					asList("--delay".split(" ")));
		}).isInstanceOf(UnfinishedParameterResolutionException.class)
				.hasMessageContaining("Error trying to resolve '--delay delay' using [--delay]");
	}

	@Test
	public void testIncompleteCommandResolutionBigArity() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "add", List.class);

		assertThatThrownBy(() -> {
			resolver.resolve(
					Utils.createMethodParameter(method, 0),
					asList("--numbers 1 2".split(" ")));
		}).isInstanceOf(UnfinishedParameterResolutionException.class)
				.hasMessageContaining("Error trying to resolve '--numbers list list list' using [--numbers 1 2]");
	}

	@Test
	public void testUnresolvableArg() throws Exception {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		assertThatThrownBy(() -> {
			resolver.resolve(
					Utils.createMethodParameter(method, 1),
					asList("--foo hello --force --bar well".split(" ")));
		}).isInstanceOf(ParameterMissingResolutionException.class)
				.hasMessageContaining("Parameter '--name string' should be specified");
	}

	// Tests for completion

	@Test
	public void testParameterKeyNotYetSetAppearsInProposals() {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);
		List<String> completions = resolver.complete(
				Utils.createMethodParameter(method, 1),
				contextFor("")
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).contains("--name");
		completions = resolver.complete(
				Utils.createMethodParameter(method, 1),
				contextFor("--force ")
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).contains("--name");
	}

	@Test
	public void testParameterKeyNotFullySpecified() {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);
		List<String> completions = resolver.complete(
				Utils.createMethodParameter(method, 1),
				contextFor("--na")
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).contains("--name");
		completions = resolver.complete(
				Utils.createMethodParameter(method, 1),
				contextFor("--force --na")
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).contains("--name");
	}

	@Test
	public void testNoMoreAvailableParameters() {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);
		List<String> completions = resolver.complete(
				Utils.createMethodParameter(method, 2), // trying to complete --foo
				contextFor("--name ") // but input is currently focused on --name
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).isEmpty();
	}

	@Test
	public void testNotTheRightTimeToCompleteThatParameter() {
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				Collections.emptySet());
		Method method = findMethod(Remote.class, "shutdown", Remote.Delay.class);
		List<String> completions = resolver.complete(
				Utils.createMethodParameter(method, 0),
				contextFor("--delay 323")
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).isEmpty();
	}

	@Test
	public void testValueCompletionWithNonDefaultArity() {
		Set<ValueProvider> valueProviders = new HashSet<>();
		valueProviders.add(new Remote.NumberValueProvider("12", "42", "7"));
		StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService(),
				valueProviders);

		Method[] methods = {
			findMethod(org.springframework.shell.standard.Remote.class, "add", List.class),
			findMethod(org.springframework.shell.standard.Remote.class, "addAsArray", int[].class),
		};
		for (Method method : methods) {
			List<String> completions = resolver
					.complete(Utils.createMethodParameter(method, 0), contextFor("--numbers ")).stream()
					.map(CompletionProposal::value).collect(Collectors.toList());
			assertThat(completions).contains("12", "42", "7");

			completions = resolver.complete(Utils.createMethodParameter(method, 0), contextFor("--numbers 42 "))
					.stream().map(CompletionProposal::value).collect(Collectors.toList());
			assertThat(completions).contains("12", "7");

			completions = resolver.complete(Utils.createMethodParameter(method, 0), contextFor("--numbers 42 34 "))
					.stream().map(CompletionProposal::value).collect(Collectors.toList());
			assertThat(completions).contains("12", "7");

			completions = resolver.complete(Utils.createMethodParameter(method, 0), contextFor("--numbers 42 34 66 "))
					.stream().map(CompletionProposal::value).collect(Collectors.toList());
			assertThat(completions).isEmpty(); // All 3 have already been set
		}
	}

	private CompletionContext contextFor(String input) {
		DefaultParser defaultParser = new DefaultParser();
		ParsedLine parsed = defaultParser.parse(input, input.length());
		List<String> words = parsed.words().stream().filter(w -> w.length() > 0).collect(Collectors.toList());

		return new CompletionContext(words, parsed.wordIndex(), parsed.wordCursor());
	}
}
