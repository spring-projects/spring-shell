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
 */
public class Tokenizer {

	private static final char ESCAPE_CHAR = '\\';

	private final char[] buffer;

	private int pos = 0;

	private final Map<String, String> result = new LinkedHashMap<String, String>();

	/** Useful when trying to do auto complete. */
	private boolean allowUnbalancedLastQuotedValue;

	public Tokenizer(String text) {
		this(text, false);
	}

	public Tokenizer(String text, boolean allowUnbalancedLastQuotedValue) {
		this.buffer = text.toCharArray();
		this.allowUnbalancedLastQuotedValue = allowUnbalancedLastQuotedValue;
		tokenize();
	}

	private void eatWhiteSpace() {
		while (pos < buffer.length && buffer[pos] == ' ') {
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
	 * Consume either {@code --key=value} or just {@code value}, eating extra spaces.
	 */
	private void eatKeyValuePair() {
		if (buffer[pos] == '-' && pos + 1 < buffer.length && buffer[pos + 1] == '-') {
			pos += 2;
			eatKeyEqualsValue();
		}
		else {
			String value = eatValue();
			store("", value);
		}

	}

	private void store(String key, String value) {
		if (result.put(key, value) != null) {
			throw new IllegalArgumentException("You cannot specify option '" + key
					+ "' more than once in a single command");
		}
	}

	private String eatValue() {
		StringBuilder sb = new StringBuilder();
		char endDelimiter = ' ';
		if (buffer[pos] == '"') {
			endDelimiter = '"';
			pos++;
		}
		while (pos < buffer.length && buffer[pos] != endDelimiter) {
			if (buffer[pos] == ESCAPE_CHAR && pos + 1 < buffer.length && buffer[pos + 1] == endDelimiter) {
				sb.append(endDelimiter);
				pos += 2;
				continue;
			}
			sb.append(buffer[pos]);
			pos++;
		}
		// When here, we either ran out of input, or encountered our delim, or both
		if (endDelimiter == '"' && pos == buffer.length && buffer[pos - 1] != '"') {
			if (allowUnbalancedLastQuotedValue) {
				return sb.toString();
			}
			else {
				throw new IllegalArgumentException("Cannot have an unbalanced number of quotation marks");
			}
		}
		// Eat our delim
		pos++;
		return sb.toString();
	}

	/**
	 * Consume a full @code{--key value} pair *unless* we're at the very end, in which case allow for @code {--key},
	 * using "" for the value.
	 */
	private void eatKeyEqualsValue() {
		String key = eatKey();
		eatWhiteSpace();
		String value;
		if (pos < buffer.length) {
			value = eatValue();
		}
		else {
			value = "";
		}
		store(key, value);
	}

	private String eatKey() {
		int start = pos;
		while (pos < buffer.length && buffer[pos] != ' ') {
			pos++;
		}
		return new String(buffer, start, pos - start);
	}
}
