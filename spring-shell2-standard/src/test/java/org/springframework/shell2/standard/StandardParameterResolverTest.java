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

package org.springframework.shell2.standard;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell2.CompletionContext;
import org.springframework.shell2.CompletionProposal;
import org.springframework.shell2.ParameterMissingResolutionException;
import org.springframework.shell2.UnfinishedParameterResolutionException;
import org.springframework.shell2.Utils;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * Unit tests for DefaultParameterResolver.
 * @author Eric Bottard
 * @author Florent Biville
 */
public class StandardParameterResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private StandardParameterResolver resolver = new StandardParameterResolver(new DefaultConversionService());

	// Tests for resolution

	@Test
	public void testParses() throws Exception {
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		assertThat(resolver.resolve(
				Utils.createMethodParameter(method, 0),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo(true);
		assertThat(resolver.resolve(
				Utils.createMethodParameter(method, 1),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo("--foo");
		assertThat(resolver.resolve(
				Utils.createMethodParameter(method, 2),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo("y");
		assertThat(resolver.resolve(
				Utils.createMethodParameter(method, 3),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo("last");

	}
	
	@Test
	public void testParsesWithMethodPrefix() throws Exception {
		Method method = findMethod(Remote.class, "prefixTest", String.class);

		assertThat(resolver.resolve(
				Utils.createMethodParameter(method, 0),
				asList("-message abc".split(" "))
		)).isEqualTo("abc");
	}

	@Test
	public void testParameterSpecifiedTwiceViaDifferentAliases() throws Exception {
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Named parameter has been specified multiple times via '--bar, --baz'");

		resolver.resolve(
				Utils.createMethodParameter(method, 0),
				asList("--force --name --foo y --bar x --baz z".split(" "))
		);
	}

	@Test
	public void testParameterSpecifiedTwiceViaSameKey() throws Exception {
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter for '--baz' has already been specified");

		resolver.resolve(
				Utils.createMethodParameter(method, 0),
				asList("--force --name --foo y --baz x --baz z".split(" "))
		);
	}

	@Test
	public void testTooMuchInput() throws Exception {
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("the following could not be mapped to parameters: 'leftover'");

		resolver.resolve(
				Utils.createMethodParameter(method, 0),
				asList("--foo hello --name bar --force --bar well leftover".split(" "))
		);
	}

	@Test
	public void testIncompleteCommandResolution() throws Exception {
		Method method = findMethod(Remote.class, "shutdown", Remote.Delay.class);

		thrown.expect(UnfinishedParameterResolutionException.class);
		thrown.expectMessage("Error trying to resolve '--delay delay' using [--delay]");

		resolver.resolve(
				Utils.createMethodParameter(method, 0),
				asList("--delay".split(" "))
		);
	}

	@Test
	public void testIncompleteCommandResolutionBigArity() throws Exception {
		Method method = findMethod(Remote.class, "add", List.class);

		thrown.expect(UnfinishedParameterResolutionException.class);
		thrown.expectMessage("Error trying to resolve '--numbers list list list' using [--numbers 1 2]");

		resolver.resolve(
				Utils.createMethodParameter(method, 0),
				asList("--numbers 1 2".split(" "))
		);
	}

	@Test
	public void testUnresolvableArg() throws Exception {
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		thrown.expect(ParameterMissingResolutionException.class);
		thrown.expectMessage("Parameter '--name string' should be specified");

		resolver.resolve(
				Utils.createMethodParameter(method, 1),
				asList("--foo hello --force --bar well".split(" "))
		);
	}

	// Tests for completion

	@Test
	public void testParameterKeyNotYetSetAppearsInProposals() {
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
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);
		List<String> completions = resolver.complete(
				Utils.createMethodParameter(method, 2), // trying to complete --foo
				contextFor("--name ") // but input is currently focused on --name
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).isEmpty();
	}

	@Test
	public void testNotTheRightTimeToCompleteThatParameter() {
		Method method = findMethod(Remote.class, "shutdown", Remote.Delay.class);
		List<String> completions = resolver.complete(
				Utils.createMethodParameter(method, 0),
				contextFor("--delay 323")
		).stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(completions).isEmpty();
	}

	@Test
	public void testValueCompletionWithNonDefaultArity() {
		
		resolver.setValueProviders(singletonList(new Remote.NumberValueProvider("12", "42", "7")));
		
		Method[] methods = {
			findMethod(org.springframework.shell2.standard.Remote.class, "add", List.class),
			findMethod(org.springframework.shell2.standard.Remote.class, "addAsArray", int[].class),
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
