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

package org.springframework.shell2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;

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
			methodParameter = new MethodParameter((Constructor) executable, i);
		} else {
			throw new IllegalArgumentException("Unsupported Executable: " + executable);
		}
		methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
		return methodParameter;
	}

	public static Stream<MethodParameter> createMethodParameters(Executable executable) {
		return IntStream.range(0, executable.getParameterCount())
			.mapToObj(i -> createMethodParameter(executable, i));
	}

}
