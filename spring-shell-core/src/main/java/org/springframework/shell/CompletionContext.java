/*
 * Copyright 2016 the original author or authors.
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

import org.springframework.shell.command.CommandOption;
import org.springframework.shell.command.CommandRegistration;

/**
 * Represents the buffer context in which completion was triggered.
 *
 * @author Eric Bottard
 */
public class CompletionContext {

	private final List<String> words;

	private final int wordIndex;

	private final int position;

	private final CommandOption commandOption;

	private final CommandRegistration commandRegistration;

	/**
	 *
	 * @param words words in the buffer, excluding words for the command name
	 * @param wordIndex the index of the word the cursor is in
	 * @param position the position inside the current word where the cursor is
	 */
	public CompletionContext(List<String> words, int wordIndex, int position, CommandRegistration commandRegistration,  CommandOption commandOption) {
		this.words = words;
		this.wordIndex = wordIndex;
		this.position = position;
		this.commandRegistration = commandRegistration;
		this.commandOption = commandOption;
	}

	public List<String> getWords() {
		return words;
	}

	public int getWordIndex() {
		return wordIndex;
	}

	public int getPosition() {
		return position;
	}

	public CommandOption getCommandOption() {
		return commandOption;
	}

	public CommandRegistration getCommandRegistration() {
		return commandRegistration;
	}

	public String upToCursor() {
		String start = words.subList(0, wordIndex).stream().collect(Collectors.joining(" "));
		if (wordIndex < words.size()) {
			if (!start.isEmpty()) {
				start += " ";
			}
			start += currentWord().substring(0, position);
		}
		return start;
	}

	/**
	 * Return the whole word the cursor is in, or {@code null} if the cursor is past the last word.
	 */
	public String currentWord() {
		return wordIndex >= 0 && wordIndex < words.size() ? words.get(wordIndex) : null;
	}

	public String currentWordUpToCursor() {
		String currentWord = currentWord();
		return currentWord != null ? currentWord.substring(0, getPosition()) : null;
	}

	/**
	 * Return a copy of this context, as if the first {@literal nbWords} were not present
	 */
	public CompletionContext drop(int nbWords) {
		return new CompletionContext(new ArrayList<String>(words.subList(nbWords, words.size())), wordIndex - nbWords,
				position, commandRegistration, commandOption);
	}

	/**
	 * Return a copy of this context with given command option.
	 */
	public CompletionContext commandOption(CommandOption commandOption) {
		return new CompletionContext(words, wordIndex, position, commandRegistration, commandOption);
	}

	/**
	 * Return a copy of this context with given command registration.
	 */
	public CompletionContext commandRegistration(CommandRegistration commandRegistration) {
		return new CompletionContext(words, wordIndex, position, commandRegistration, commandOption);
	}
}
