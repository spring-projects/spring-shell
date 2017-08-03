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

package org.springframework.shell.standard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;

/**
 * An example commands class.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
public class Remote {

	/**
	 * A command method that showcases<ul>
	 *     <li>default handling for booleans (force)</li>
	 *     <li>default parameter name discovery (name)</li>
	 *     <li>default value supplying (foo and bar)</li>
	 * </ul>
	 */
	@ShellMethod(help = "switch channels")
	public void zap(boolean force,
	                String name,
	                @ShellOption(defaultValue="defoolt") String foo,
	                @ShellOption(value = {"--bar", "--baz"}, defaultValue = "last") String bar) {
	}

	@ShellMethod(help = "bye bye")
	public void shutdown(@ShellOption Delay delay) {

	}
	
	@ShellMethod(help = "a different prefix", prefix = "-")
	public void prefixTest(@ShellOption String message) {

	}

	@ShellMethod(help = "add 3 numbers together")
	public void add(@ShellOption(arity = 3, valueProvider = NumberValueProvider.class) List<Integer> numbers) {

	}

	@ShellMethod(help = "add 3 numbers together (array)")
	public void addAsArray(@ShellOption(arity = 3, valueProvider = NumberValueProvider.class) int[] numbers) {

	}

	public enum Delay {
		small, medium, big;
	}


	public static class NumberValueProvider extends ValueProviderSupport {

		private final String[] values;

		public NumberValueProvider(String... values) {
			this.values = values;
		}

		@Override
		public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext, String[] hints) {
			String prefix = completionContext.currentWord() != null ? completionContext.currentWord() : "";
			return Stream.of(values)
					.filter(n -> n.startsWith(prefix))
					.map(CompletionProposal::new)
					.collect(Collectors.toList());
		}
	}
}
