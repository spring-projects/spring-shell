/*
 * Copyright 2025-present the original author or authors.
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
package org.springframework.shell.core;

/**
 * Interface for reading input from the user.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public interface InputReader {

	/**
	 * Read a line of input from the user.
	 * @return the input line
	 * @throws Exception if an error occurs while reading input
	 */
	default String readInput() throws Exception {
		return readInput("");
	}

	/**
	 * Read a line of input from the user with a prompt.
	 * @param prompt the prompt to display to the user
	 * @return the input line
	 * @throws Exception if an error occurs while reading input
	 */
	default String readInput(String prompt) throws Exception {
		throw new UnsupportedOperationException("readInput with prompt not implemented");
	}

	/**
	 * Read a password from the user.
	 * @return the password as a character array
	 * @throws Exception if an error occurs while reading the password
	 */
	default char[] readPassword() throws Exception {
		return readPassword("");
	}

	/**
	 * Read a password from the user with a prompt.
	 * @param prompt the prompt to display to the user
	 * @return the password as a character array
	 * @throws Exception if an error occurs while reading the password
	 */
	default char[] readPassword(String prompt) throws Exception {
		throw new UnsupportedOperationException("readPassword with prompt not implemented");
	}

}
