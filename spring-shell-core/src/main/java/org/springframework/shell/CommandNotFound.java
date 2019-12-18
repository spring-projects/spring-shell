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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A result to be handled by the {@link ResultHandler} when no command could be mapped to user input
 */
public class CommandNotFound extends RuntimeException {

	private final List<String> words;

	public CommandNotFound(List<String> words) {
		this.words = words;
	}

	@Override
	public String getMessage() {
		return String.format("No command found for '%s'", words.stream().collect(Collectors.joining(" ")));
	}

	/**
	 * return a copy of the words inputted by the user to avoid mutation
	 * @return
	 */
	public List<String> getWords(){
		return new ArrayList<>(words);
	}
}
