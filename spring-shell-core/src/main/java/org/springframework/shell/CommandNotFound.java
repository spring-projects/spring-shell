/*
 * Copyright 2017-2023
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
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.shell.command.CommandRegistration;

/**
 * A result to be handled by the {@link ResultHandler} when no command could be mapped to user input
 */
public class CommandNotFound extends RuntimeException {

	private final List<String> words;
	private final Map<String, CommandRegistration> registrations;
	private final String text;

	public CommandNotFound(List<String> words) {
		this(words, null, null);
	}

	public CommandNotFound(List<String> words, Map<String, CommandRegistration> registrations, String text) {
		this.words = words;
		this.registrations = registrations;
		this.text = text;
	}

	@Override
	public String getMessage() {
		return String.format("No command found for '%s'", words.stream().collect(Collectors.joining(" ")));
	}

	/**
	 * Gets a {@code words} in this exception.
	 *
	 * @return a words
	 */
	public List<String> getWords(){
		return new ArrayList<>(words);
	}

	/**
	 * Gets command registrations known when this error was created.
	 *
	 * @return known command registrations
	 */
	public Map<String, CommandRegistration> getRegistrations() {
		return registrations;
	}

	/**
	 * Gets a raw text input.
	 *
	 * @return raw text input
	 */
	public String getText() {
		return text;
	}
}
