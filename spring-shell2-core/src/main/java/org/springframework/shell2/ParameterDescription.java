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

import java.util.List;
import java.util.Optional;

import org.springframework.core.MethodParameter;

/**
 * Encapsulates information about a shell invokable method parameter, so that it can be documented.
 *
 * <p>Instances of this class are constructed by {@link ParameterResolver#describe(MethodParameter)}.</p>
 *
 * @author Eric Bottard
 */
public class ParameterDescription {

	/**
	 * The original method parameter this is describing.
	 */
	private final MethodParameter parameter;

	/**
	 * A string representation of the type of the parameter.
	 */
	private final String type;

	/**
	 * A string representation of the parameter, as it should appear in a parameter list.
	 * If not provided, this is derived from the parameter type.
	 */
	private String formal;

	/**
	 * A string representation of the default value (if the option is left out entirely) for the parameter, if any.
	 */
	private Optional<String> defaultValue = Optional.empty();

	/**
	 * A string representation of the default value for this parameter, if it can be used as a mere flag (<em>e.g.</em>
	 * {@literal --force} without a value, being an equivalent to {@literal --force true}).
	 * <p>{@literal Optional.empty()} (the default) means that this parameter cannot be used as a flag.</p>
	 */
	private Optional<String> defaultValueWhenFlag = Optional.empty();

	/**
	 * The list of 'keys' that can be used to specify this parameter, if any.
	 */
	private List<String> keys;

	/**
	 * Depending on the {@link ParameterResolver}, whether keys are mandatory to identify this parameter.
	 */
	private boolean mandatoryKey = true;

	/**
	 * A short description of this parameter.
	 */
	private String help = "";

	public ParameterDescription(MethodParameter parameter, String type) {
		this.parameter = parameter;
		this.type = type;
		this.formal = type;
	}

	public static ParameterDescription outOf(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return new ParameterDescription(parameter, Utils.unCamelify(type.getSimpleName()));
	}

	public ParameterDescription help(String help) {
		this.help = help;
		return this;
	}

	public boolean mandatoryKey() {
		return mandatoryKey;
	}

	public List<String> keys() {
		return keys;
	}

	public Optional<String> defaultValue() {
		return defaultValue;
	}

	public ParameterDescription defaultValue(String defaultValue) {
		this.defaultValue = Optional.of(defaultValue);
		return this;
	}

	public ParameterDescription whenFlag(String defaultValue) {
		this.defaultValueWhenFlag = Optional.of(defaultValue);
		return this;
	}

	public ParameterDescription keys(List<String> keys) {
		this.keys = keys;
		return this;
	}

	public ParameterDescription mandatoryKey(boolean mandatoryKey) {
		this.mandatoryKey = mandatoryKey;
		return this;
	}


	public String type() {
		return type;
	}

	public String formal() {
		return formal;
	}

	public String help() {
		return help;
	}

	public Optional<String> defaultValueWhenFlag() {
		return defaultValueWhenFlag;
	}

	public ParameterDescription formal(String formal) {
		this.formal = formal;
		return this;
	}

	@Override
	public String toString() {
		return String.format("%s %s", keys().iterator().next(), formal());
	}

	public MethodParameter parameter() {
		return parameter;
	}
}
