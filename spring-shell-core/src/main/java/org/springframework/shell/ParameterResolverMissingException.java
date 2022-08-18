/*
 * Copyright 2017 the original author or authors.
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

import org.springframework.core.MethodParameter;

/**
 * Thrown when no ParameterResolver is found for a parameter during a resolve
 * operation.
 *
 * @author Camilo Gonzalez
 */
public class ParameterResolverMissingException extends RuntimeException {
	public ParameterResolverMissingException(MethodParameter parameter) {
		super(String.format("No parameter resolver found for parameter with index %d (named '%s') of %s ", parameter.getParameterIndex(), parameter.getParameterName(), parameter.getMethod()));
	}
}
