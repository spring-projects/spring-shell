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

package org.springframework.shell;

/**
 * Thrown by a {@link ParameterResolver} when a parameter that should have been set has been left out altogether.
 *
 * @author Eric Bottard
 */
public class ParameterMissingResolutionException extends RuntimeException {

	private final ParameterDescription parameterDescription;

	public ParameterMissingResolutionException(ParameterDescription parameterDescription) {
		this.parameterDescription = parameterDescription;
	}

	public ParameterDescription getParameterDescription() {
		return parameterDescription;
	}

	@Override
	public String getMessage() {
		return String.format("Parameter '%s' should be specified", parameterDescription);
	}
}
