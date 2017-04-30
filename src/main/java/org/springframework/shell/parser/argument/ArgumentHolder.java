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
package org.springframework.shell.parser.argument;

import org.springframework.shell.core.annotation.CliOption;

/**
 * Represents a holder for an argument that will be used for a method invocation.
 * 
 * @author Camilo Gonzalez
 * @since 1.2.1
 */
public interface ArgumentHolder {

	/**
	 * @return the expectedType the final argument value must have
	 */
	Class<?> getExpectedType();

	CliOption getCliOption();

	String getInputValue();

	String getSourcedFrom();
	
	ArgumentValue getArgumentValue();

	public static class ArgumentValue {
		private final Object value;

		private final boolean resolved;

		public ArgumentValue() {
			this.resolved = false;
			this.value = null;
		}

		public ArgumentValue(Object value) {
			this.resolved = true;
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public Object getValue() {
			if (!resolved) {
				throw new IllegalStateException("There is no resolved value for this argument");
			}
			return value;
		}

		/**
		 * @return the resolved
		 */
		public boolean isResolved() {
			return resolved;
		}

	}

}
