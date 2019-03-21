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

package org.springframework.shell.samples.standard;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;

/**
 * Example commands for the Shell 2 Standard resolver.
 *
 * @author Eric Bottard
 */
@ShellComponent()
public class Commands {

	@ShellMethod(value = "A command whose name looks the same as another one.", key = "help me out")
	public void helpMeOut() {
		System.out.println("You can go");
	}

	@ShellMethod("Change Password. Shows support for bean validation.")
	public String changePassword(@Size(min = 8) String password) {
		return "Password changed";
	}
	
	@ShellMethod(value = "Shows non trivial character encoding.")
	public String helloWorld() {
		return "こんにちは世界";
	}

	@ShellMethod("Shows support for boolean parameters, with arity=0.")
	public void shutdown(@ShellOption(arity = 0) boolean force) {
		System.out.println("You passed " + force);
	}

	@ShellMethod("Test completion of special values.")
	public void quote(@ShellOption(valueProvider = FunnyValuesProvider.class) String text) {
		System.out.println("You said " + text);
	}

	@ShellMethod("Add numbers.")
	public int add(int a, int b, int c) {
		return a + b + c;
	}

	@ShellMethod("Fails with an exception. Shows enum conversion.")
	public void fail(ElementType elementType) {
		throw new IllegalArgumentException("You said " + elementType);
	}

	@ShellMethod("Add array numbers.")
	public double addDoubles(@ShellOption(arity = 3) double[] numbers) {
		return Arrays.stream(numbers).sum();
	}
}

/**
 * A {@link org.springframework.shell.standard.ValueProvider} that emits values with special characters
 * (quotes, escapes, <em>etc.</em>)
 *
 * @author Eric Bottard
 */
@Component
class FunnyValuesProvider extends ValueProviderSupport {

	private final static String[] VALUES = new String[] {
		"hello world",
		"I'm quoting \"The Daily Mail\"",
		"10 \\ 3 = 3"
	};

	@Override
	public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext, String[] hints) {
		return Arrays.stream(VALUES).map(CompletionProposal::new).collect(Collectors.toList());
	}
}
