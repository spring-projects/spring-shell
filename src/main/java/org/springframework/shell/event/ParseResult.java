/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.shell.event;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.core.style.ToStringCreator;
import org.springframework.shell.core.Converter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Immutable representation of the outcome of parsing a given shell line.
 *
 * <p>
 * Note that contained objects (the instance and the arguments) may be mutable, as the shell infrastructure
 * has no way of restricting which methods can be the target of CLI commands and nor the arguments
 * they will accept via the {@link Converter} infrastructure.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class ParseResult {

	// Fields
	private final Method method;
	private final Object instance;
	private final Object[] arguments; // May be null if no arguments needed

	public ParseResult(final Method method, final Object instance, final Object[] arguments) {
		Assert.notNull(method, "Method required");
		Assert.notNull(instance, "Instance required");
		int length = arguments == null ? 0 : arguments.length;
		Assert.isTrue(method.getParameterTypes().length == length, "Required " + method.getParameterTypes().length + " arguments, but received " + length);
		this.method = method;
		this.instance = instance;
		this.arguments = arguments;
	}

	public Method getMethod() {
		return method;
	}

	public Object getInstance() {
		return instance;
	}

	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arguments);
		result = prime * result + ((instance == null) ? 0 : instance.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParseResult other = (ParseResult) obj;
		if (!Arrays.equals(arguments, other.arguments))
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
		tsc.append("arguments", StringUtils.arrayToCommaDelimitedString(arguments));
		return tsc.toString();
	}
}
