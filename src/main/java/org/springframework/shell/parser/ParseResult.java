/*
 * Copyright 2011-2017 the original author or authors.
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
package org.springframework.shell.parser;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.shell.core.Converter;
import org.springframework.shell.parser.argument.ArgumentResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import jline.console.ConsoleReader;

/**
 * Immutable representation of the outcome of parsing a given shell line.
 *
 * <p>
 * Note that contained objects (the instance and the arguments) may be mutable, as the shell infrastructure has no way
 * of restricting which methods can be the target of CLI commands and nor the arguments they will accept via the
 * {@link Converter} infrastructure.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class ParseResult {

	// Fields
	private final Method method;
	private final Object instance;
	private final List<ArgumentResolver> arguments; // May be empty if no arguments needed
	private final Object[] resolvedArguments;

	private boolean argumentsResolved;

	public ParseResult(final Method method, final Object instance, final List<ArgumentResolver> arguments) {
		Assert.notNull(method, "Method required");
		Assert.notNull(instance, "Instance required");
		int length = arguments == null ? 0 : arguments.size();
		Assert.isTrue(method.getParameterTypes().length == length,
				"Required " + method.getParameterTypes().length + " arguments, but received " + length);
		this.method = method;
		this.instance = instance;
		this.arguments = arguments;
		this.resolvedArguments = new Object[length];
		// if there are no arguments to resolve, we can mark arguments resolved as true
		this.argumentsResolved = (length == 0);
	}

	public Method getMethod() {
		return method;
	}

	public Object getInstance() {
		return instance;
	}

	public List<ArgumentResolver> getArguments() {
		return arguments;
	}

	public Object[] getResolvedArguments() {
		if (!argumentsResolved) {
			throw new IllegalStateException("Arguments haven't been resolved yet");
		}
		return resolvedArguments;
	}

	public void resolveArguments(ConsoleReader consoleReader) {
		if (argumentsResolved && this.resolvedArguments.length > 0) {
			throw new IllegalStateException("Arguments already resolved.");
		}

		for (int i = 0; i < resolvedArguments.length; i++) {
			resolvedArguments[i] = arguments.get(i).getArgumentValue(consoleReader);
		}

		argumentsResolved = true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + ((instance == null) ? 0 : instance.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParseResult other = (ParseResult) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (instance == null) {
			if (other.instance != null)
				return false;
		} else if (!instance.equals(other.instance))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

	@Override
	public String toString() {
		ToStringCreator tsc = new ToStringCreator(this);
		tsc.append("method", method);
		tsc.append("instance", instance);
		tsc.append("arguments", StringUtils.collectionToCommaDelimitedString(arguments));
		return tsc.toString();
	}
}
