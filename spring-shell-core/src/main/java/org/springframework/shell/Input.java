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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the input buffer to the shell.
 *
 * @author Eric Bottard
 */
public interface Input {

	Input EMPTY = () -> "";

	/**
	 * Return the input as entered by the user.
	 */
	String rawText();

	/**
	 * Return the input as a list of parsed "words", having split the raw input according
	 * to parsing rules (for example, handling quoted portions of the readInput as a single
	 * "word")
	 */
	default List<String> words() {return "".equals(rawText()) ? Collections.emptyList() : Arrays.asList(rawText().split(" "));}
}
