/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.shell;

/**
 * Thrown during parameter resolution, when a parameter has been identified, but could not be correctly resolved.
 *
 * @author Eric Bottard
 */
public class UnfinishedParameterResolutionException extends RuntimeException {

	private final ParameterDescription parameterDescription;

	private final CharSequence input;

	public UnfinishedParameterResolutionException(ParameterDescription parameterDescription, CharSequence input) {
		this.parameterDescription = parameterDescription;
		this.input = input;
	}

	public ParameterDescription getParameterDescription() {
		return parameterDescription;
	}

	public CharSequence getInput() {
		return input;
	}

	@Override
	public String getMessage() {
		return String.format("Error trying to resolve '%s' using [%s]", parameterDescription, input);
	}
}
