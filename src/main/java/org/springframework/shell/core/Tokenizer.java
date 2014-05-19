/*
 * Copyright 2013 the original author or authors.
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

package org.springframework.shell.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Converts a particular buffer into a tokenized structure.
 * 
 * <p>
 * Properly treats double quotes (") as option delimiters.
 * 
 * <p>
 * Expects option names to be preceded by a double dash. We call this an "option marker".
 * 
 * <p>
 * Treats spaces as the default option tokenizer.
 * 
 * <p>
 * Any token without an option marker is considered the default. The default is returned in the Map as an element with
 * an empty string key (""). There can only be a single default.
 * 
 * @author Eric Bottard
 * @since 1.1
 */
public class Tokenizer {

	private static final char ESCAPE_CHAR = '\\';

	private final char[] buffer;

	private int pos = 0;

	private final Map<String, String> result = new LinkedHashMap<String, String>();

	/** Useful when trying to do auto complete. */
	private boolean allowUnbalancedLastQuotedValue;

	/**
	 * Used to indicate that the last value was indeed half enclosed in quotes. Useful so that parser can re-add it.
	 */
	private boolean openingQuotesHaveNotBeenClosed;

	private char lastValueDelimiter;

	private int lastValueStartOffset = -1;

	public Tokenizer(String text) {
		this(text, false);
	}

	public Tokenizer(String text, boolean allowUnbalancedLastQuotedValue) {
		this.buffer = text.toCharArray();
		this.allowUnbalancedLastQuotedValue = allowUnbalancedLastQuotedValue;
		tokenize();
	}

	private void eatWhiteSpace() {
		while (lookAhead(' ')) {
			pos++;
		}
	}

	public void tokenize() {
		while (pos < buffer.length) {
			eatWhiteSpace();
			if (pos < buffer.length) {
				eatKeyValuePair();
			}
		}
	}

	public Map<String, String> getTokens() {
		return result;
	}

	/**
	 * Return true if the remaining buffer matches the given String (return false if there is not enough input).
	 */
	private boolean lookAhead(char... toMatch) {
		for (int i = 0; i < toMatch.length; i++) {
			if (pos + i >= buffer.length || buffer[pos + i] != toMatch[i]) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Consume either {@code --key[=value]} or just {@code value}, eating extra spaces.
	 */
	private void eatKeyValuePair() {
		if (lookAhead('-', '-')) {
			pos += 2;
			eatKeyEqualsValue();
		}
		else {
			int offsetInCaseOfFailure = pos;
			String value = eatValue(true);
			store("", value, offsetInCaseOfFailure);
		}

	}

	/**
	 * Store a command key/value pair, reporting a failure if a mapping with the same key is already present.
	 * @param key the command key
	 * @param value the command value
	 * @param failureOffset the buffer offset at which the mapping we're trying to store was tokenized
	 */
	private void store(String key, String value, int failureOffset) {
		String alreadyThere = result.put(key, value);
		if (alreadyThere != null) {
			if ("".equals(key)) {
				String explanation = String.format(
						"You cannot specify '%s' as another value for the default ('') option in a single command.%n"
								+ "You already provided '%s' earlier.%n"
								+ "Did you forget to add quotes around the value of another option?", value,
						alreadyThere);
				throw new TokenizingException(failureOffset, buffer, explanation);
			}
			else {
				String explanation = String.format(
						"You cannot specify '%s' as another value for the '--%s' option in a single command.%n"
								+ "You already provided '%s' earlier.", value, key, alreadyThere);
				throw new TokenizingException(failureOffset, buffer, explanation);
			}
		}
	}

	/**
	 * Eat a value that may be enclosed in some delimiters.
	 * @param emptyKey if true, we're currently reading the value for the empty key
	 */
	private String eatValue(boolean emptyKey) {
		StringBuilder sb = new StringBuilder();
		char endDelimiter = ' ';
		if (buffer[pos] == '"' || buffer[pos] == '\'') {
			endDelimiter = buffer[pos];
			pos++;
		}
		// So that it can be retrieved later (if this is actually the last value)
		lastValueDelimiter = endDelimiter;
		lastValueStartOffset = pos;
		while (pos < buffer.length && buffer[pos] != endDelimiter) {
			if (buffer[pos] == ESCAPE_CHAR) {
				sb.append(processCharacterEscapeCodes(endDelimiter));
				continue;
			}
			if (lookAhead(ESCAPE_CHAR, endDelimiter)) {
				sb.append(endDelimiter);
				pos += 2;
				continue;
			}
			sb.append(buffer[pos]);
			pos++;
		}
		// If we're grabbing the key-less value, allow additional chunks, as long as
		// 1) we don't hit '--'
		// 2) we were not using a quote delimited value
		if (emptyKey && endDelimiter == ' ') {
			while (!lookAhead('-', '-') && pos < buffer.length) {
				sb.append(buffer[pos++]);
			}
			// Trim to the right
			while (Character.isWhitespace(sb.charAt(sb.length() - 1))) {
				sb.setLength(sb.length() - 1);
			}
			return sb.toString();
		}

		// When here, we either ran out of input, or encountered our delim, or both
		// Fail, unless we allow an unfinished quoted string to be reported
		if (endDelimiter != ' ' && // we're using quotes
				pos == buffer.length && // we ran of input
				(buffer[pos - 1] != endDelimiter || // quotes are not properly closed
				sb.length() == 0)) { // BUT it's ok if consumed nothing (pos-1 is *opening* quote then)
			if (allowUnbalancedLastQuotedValue) {
				openingQuotesHaveNotBeenClosed = true;
				return sb.toString();
			}
			else {
				throw new TokenizingException(pos, buffer, "Cannot have an unbalanced number of quotation marks");
			}
		}
		// Eat our delim
		pos++;
		return sb.toString();
	}

	/**
	 * When the escape character is encountered, consume and return the escaped sequence. Note that depending on which
	 * end delimiter is currently in use, not all combinations need to be escaped
	 * @param endDelimiter the current endDelimiter
	 */
	private char processCharacterEscapeCodes(char endDelimiter) {
		pos++;
		if (pos >= buffer.length) {
			throw new TokenizingException(buffer.length, buffer, "Ran out of input in escape sequence");
		}
		switch (buffer[pos]) {
		case ESCAPE_CHAR:
			pos++; // consume the second escape char
			return ESCAPE_CHAR;
		case 't':
			pos++;
			return '\t';
		case 'r':
			pos++;
			return '\r';
		case 'n':
			pos++;
			return '\n';
		case 'f':
			pos++;
			return '\f';
		case 'u':
			if (pos + 5 > buffer.length) {
				throw new TokenizingException(buffer.length, buffer, "Ran out of input in unicode escape sequence");
			}
			String hex = new String(buffer, pos + 1, 4);
			try {
				char code = (char) Integer.parseInt(hex, 16);
				pos += 5;
				return code;
			}
			catch (NumberFormatException e) {
				throw new TokenizingException(pos - 1, buffer, "Illegal unicode escape sequence: " + ESCAPE_CHAR + "u"
						+ hex);
			}

		default:
			if (buffer[pos] == endDelimiter) {
				pos++;
				return endDelimiter;
			}
			else {
				// Not an actual escape. Do not increment pos,
				// and return the \ we consumed at the very beginning
				return ESCAPE_CHAR;
			}
		}
	}

	/**
	 * Return the offset at which the last value seen started (NOT including any delimiter).
	 */
	public int getLastValueStartOffset() {
		Assert.isTrue(lastValueStartOffset >= 0, "lastValueStartOffset has not been set yet");
		return lastValueStartOffset;
	}

	/**
	 * Return the delimiter (space or quotes) that was (or is being) used for the last value.
	 */
	public char getLastValueDelimiter() {
		Assert.isTrue(lastValueDelimiter != 0, "lastValueDelimiter has not been set yet");
		return lastValueDelimiter;
	}

	/**
	 * Return whether the last value was meant to be enclosed in quotes, but the closing quote has not been typed yet.
	 */
	public boolean openingQuotesHaveNotBeenClosed() {
		return openingQuotesHaveNotBeenClosed;
	}

	/**
	 * Return true if we know for sure that the last value has been typed in full.
	 */
	public boolean lastValueIsComplete() {
		// If using quotes as delim and they're not closed, we know for sure.
		// Moreover if we're using space as the delimiter, we can't know.
		return !openingQuotesHaveNotBeenClosed && lastValueDelimiter != ' ';
	}

	/**
	 * Apply delimiter escaping to the given string, using the actual delimiter that was used for the last value.
	 */
	public String escape(String value) {
		String result = value.replace("" + lastValueDelimiter, "" + ESCAPE_CHAR + lastValueDelimiter);
		result = result.replace("\r", "\\r");
		result = result.replace("\n", "\\n");
		result = result.replace("\t", "\\t");
		result = result.replace("\f", "\\f");
		return result;
	}

	/**
	 * Consume a full {@code --key value} pair *unless*
	 * <ul>
	 * <li>we're at the very end,</li>
	 * <li>or the next token starts with {@code --}</li>
	 * </ul>
	 * in which case allow for just {@code --key}, using "" for the value.
	 */
	private void eatKeyEqualsValue() {
		// We already consumed '--'
		int offsetInCaseOfFailure = pos - 2;
		String key = eatKey();
		eatWhiteSpace();
		String value;
		// We're at the very end or it's a valueless option
		// Make last* fields consistent
		if (pos >= buffer.length || lookAhead('-', '-')) {
			lastValueDelimiter = ' ';
			lastValueStartOffset = pos;
			value = "";
		}
		else {
			value = eatValue(false);
		}
		// Don't store the ""="" that would result from having a pending " --" at the end
		if (key.equals("") && value.equals("")) {
			return;
		}
		store(key, value, offsetInCaseOfFailure);
	}

	private String eatKey() {
		int start = pos;
		while (pos < buffer.length && buffer[pos] != ' ') {
			pos++;
		}
		return new String(buffer, start, pos - start);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder().append(buffer).append('\n');
		for (int i = 0; i < lastValueStartOffset; i++) {
			result.append(' ');
		}
		result.append('^');
		return result.toString();
	}
}
