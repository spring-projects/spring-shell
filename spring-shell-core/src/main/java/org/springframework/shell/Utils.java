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

package org.springframework.shell;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.shell.command.CommandRegistration;

/**
 * Some text utilities.
 *
 * @author Eric Bottard
 */
public class Utils {

	/**
	 * Turn CamelCaseText into gnu-style-lowercase.
	 */
	public static String unCamelify(CharSequence original) {
		StringBuilder result = new StringBuilder(original.length());
		boolean wasLowercase = false;
		for (int i = 0; i < original.length(); i++) {
			char ch = original.charAt(i);
			if (Character.isUpperCase(ch) && wasLowercase) {
				result.append('-');
			}
			wasLowercase = Character.isLowerCase(ch);
			result.append(Character.toLowerCase(ch));
		}
		return result.toString();
	}

	/**
	 * Convert from JDK {@link Parameter} to Spring {@link MethodParameter}.
	 */
	public static MethodParameter createMethodParameter(Parameter parameter) {
		Parameter[] parameters = parameter.getDeclaringExecutable().getParameters();
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].equals(parameter)) {
				return createMethodParameter(parameter.getDeclaringExecutable(), i);
			}
		}
		throw new AssertionError("Can't happen");
	}
	/**
	 * Return a properly initialized MethodParameter for the given executable and index.
	 */
	public static MethodParameter createMethodParameter(Executable executable, int i) {
		MethodParameter methodParameter;
		if (executable instanceof Method) {
			methodParameter = new MethodParameter((Method) executable, i);
		} else if (executable instanceof Constructor){
			methodParameter = new MethodParameter((Constructor<?>) executable, i);
		} else {
			throw new IllegalArgumentException("Unsupported Executable: " + executable);
		}
		methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
		return methodParameter;
	}

	/**
	 * Return MethodParameters for each parameter of the given method/constructor.
	 */
	public static Stream<MethodParameter> createMethodParameters(Executable executable) {
		return IntStream.range(0, executable.getParameterCount())
			.mapToObj(i -> createMethodParameter(executable, i));
	}

	/**
	 * Sanitize the buffer input given the customizations applied to the JLine
	 * parser (<em>e.g.</em> support for
	 * line continuations, <em>etc.</em>)
	 */
	public static List<String> sanitizeInput(List<String> words) {
		words = words.stream()
			.map(s -> s.replaceAll("^\\n+|\\n+$", "")) // CR at beginning/end of line introduced by backslash continuation
			.map(s -> s.replaceAll("\\n+", " ")) // CR in middle of word introduced by return inside a quoted string
			.collect(Collectors.toList());
		return words;
	}

	private final static ValidatorFactory DEFAULT_VALIDATOR_FACTORY;
	private final static Validator DEFAULT_VALIDATOR;

	static {
		DEFAULT_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
		DEFAULT_VALIDATOR = DEFAULT_VALIDATOR_FACTORY.getValidator();
	}

	/**
	 * Gets a default shared validation factory.
	 *
	 * @return default validation factory
	 */
	public static ValidatorFactory defaultValidatorFactory() {
		return DEFAULT_VALIDATOR_FACTORY;
	}

	/**
	 * Gets a default shared validator.
	 *
	 * @return default validator
	 */
	public static Validator defaultValidator() {
		return DEFAULT_VALIDATOR;
	}

	/**
	 * Split array into list of lists by predicate
	 *
	 * @param array the array
	 * @param predicate the predicate
	 * @return the list of lists
	 */
	public static <T> List<List<T>> split(T[] array, Predicate<T> predicate) {
		List<T> list = Arrays.asList(array);
		boolean[] boundaries = new boolean[array.length];
		List<List<T>> split = new ArrayList<>();

		for (int i = 0; i < array.length; i++) {
			boundaries[i] = predicate.test(array[i]);
		}

		int tail = 0;
		for (int i = 0; i < boundaries.length; i++) {
			if (boundaries[i]) {
				if (tail < i) {
					split.add(list.subList(tail, i));
				}
				tail = i;
			}
		}

		if (tail < array.length) {
			split.add(list.subList(tail, array.length));
		}

		return split;
	}

	/**
	 * Takes a map of command registrations and removes hidden commands from it.
	 *
	 * @param registrations a command registrations
	 * @return same map with removed hidden commands
	 */
	public static Map<String, CommandRegistration> removeHiddenCommands(Map<String, CommandRegistration> registrations) {
		Iterator<Map.Entry<String, CommandRegistration>> iter = registrations.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, CommandRegistration> entry = iter.next();
			if (entry.getValue().isHidden()) {
				iter.remove();
			}
		}
		return registrations;
	}
}
