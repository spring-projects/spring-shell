/*
 * Copyright 2015-2022 the original author or authors.
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
import java.util.Iterator;
import java.util.List;

import jakarta.validation.constraints.Size;

import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;


/**
 * Example commands for the Shell 2 Standard resolver.
 *
 * @author Eric Bottard
 */
@Command()
public class Commands {

	@Command(description = "A command whose name looks the same as another one.", command = "help me out")
	public void helpMeOut() {
		System.out.println("You can go");
	}

	@Command(description = "Change Password. Shows support for bean validation.")
	public String changePassword(@Size(min = 8) String password) {
		return "Password changed";
	}

	@Command(description = "Shows non trivial character encoding.")
	public String helloWorld() {
		return "こんにちは世界";
	}

	@Command(description = "Shows support for boolean parameters, with arity=0.")
	public void shutdown(@Option(arity = CommandRegistration.OptionArity.ZERO) boolean force) {
		System.out.println("You passed " + force);
	}

	@Command(description = "Add numbers.")
	public int add(int a, int b, int c) {
		return a + b + c;
	}

	@Command(description = "Concat strings.")
	public String concat(String a, String b, String c) {
		return a + b + c;
	}

	@Command(description = "Fails with an exception. Shows enum conversion.")
	public void fail(ElementType elementType) {
		throw new IllegalArgumentException("You said " + elementType);
	}

	@Command(description = "Add array numbers.")
	public double addDoubles(
			@Option(arity = CommandRegistration.OptionArity.ONE_OR_MORE) // FIXME what if it's a number? like 3 in this case?
			double[] numbers) {
		return Arrays.stream(numbers).sum();
	}

	@Command(description = "Get iterables.")
	public Iterable<String> iterables() {
		List<String> list = Arrays.asList("first", "second");
		Iterable<String> iterable = new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return list.iterator();
			}
		};
		return iterable;
	}
}
