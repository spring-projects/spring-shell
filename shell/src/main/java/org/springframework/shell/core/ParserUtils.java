/*
 * Copyright 2011-2012 the original author or authors.
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
 * Utilities for parsing.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class ParserUtils {

	private ParserUtils() {}

	/**
	 * Converts a particular buffer into a tokenized structure.
	 *
	 * <p>
	 * Properly treats double quotes (") as option delimiters.
	 *
	 * <p>
	 * Expects option names to be preceded by a single or double dash. We call this an "option marker".
	 *
	 * <p>
	 * Treats spaces as the default option tokenizer.
	 *
	 * <p>
	 * Any token without an option marker is considered the default. The default is returned in the Map as an element with an empty string key (""). There can only be a single default.
	 *
	 * @param remainingBuffer to tokenize
	 * @return a Map where keys are the option names (minus any dashes) and values are the option values (any double-quotes are removed)
	 */
	public static Map<String, String> tokenize(final String remainingBuffer) {
		Assert.notNull(remainingBuffer, "Remaining buffer cannot be null, although it can be empty");
		Map<String, String> result = new LinkedHashMap<String, String>();
		StringBuilder currentOption = new StringBuilder();
		StringBuilder currentValue = new StringBuilder();
		boolean inQuotes = false;

		// Verify correct number of double quotes are present
		int count = 0;
		for (char c : remainingBuffer.toCharArray()) {
			if ('"' == c) {
				count++;
			}
		}
		Assert.isTrue(count % 2 == 0, "Cannot have an unbalanced number of quotation marks");

		if ("".equals(remainingBuffer.trim())) {
			// They've not specified anything, so exit now
			return result;
		}

		String[] split = remainingBuffer.split(" ");
		for (int i = 0; i < split.length; i++) {
			String currentToken = split[i];

			if (currentToken.startsWith("\"") && currentToken.endsWith("\"") && currentToken.length() > 1) {
				String tokenLessDelimiters = currentToken.substring(1, currentToken.length() - 1);
				currentValue.append(tokenLessDelimiters);

				// Store this token
				store(result, currentOption, currentValue);
				currentOption = new StringBuilder();
				currentValue = new StringBuilder();
				continue;
			}

			if (inQuotes) {
				// We're only interested in this token series ending
				if (currentToken.endsWith("\"")) {
					String tokenLessDelimiters = currentToken.substring(0, currentToken.length() - 1);
					currentValue.append(" ").append(tokenLessDelimiters);
					inQuotes = false;

					// Store this now-ended token series
					store(result, currentOption, currentValue);
					currentOption = new StringBuilder();
					currentValue = new StringBuilder();
				} else {
					// The current token series has not ended
					currentValue.append(" ").append(currentToken);
				}
				continue;
			}

			if (currentToken.startsWith("\"")) {
				// We're about to start a new delimited token
				String tokenLessDelimiters = currentToken.substring(1);
				currentValue.append(tokenLessDelimiters);
				inQuotes = true;
				continue;
			}

			if (currentToken.trim().equals("")) {
				// It's simply empty, so ignore it (ROO-23)
				continue;
			}

			if (currentToken.startsWith("--")) {
				// We're about to start a new option marker
				// First strip all of the - or -- or however many there are
				int lastIndex = currentToken.lastIndexOf("-");
				String tokenLessDelimiters = currentToken.substring(lastIndex + 1);
				currentOption.append(tokenLessDelimiters);

				// Store this token if it's the last one, or the next token starts with a "-"
				if (i + 1 == split.length) {
					// We're at the end of the tokens, so store this one and stop processing
					store(result, currentOption, currentValue);
					break;
				}

				if (split[i + 1].startsWith("-")) {
					// A new token is being started next iteration, so store this one now
					store(result, currentOption, currentValue);
					currentOption = new StringBuilder();
					currentValue = new StringBuilder();
				}

				continue;
			}

			// We must be in a standard token

			// If the standard token has no option name, we allow it to contain unquoted spaces
			if (currentOption.length() == 0) {
				if (currentValue.length() > 0) {
					// Existing content, so add a space first
					currentValue.append(" ");
				}
				currentValue.append(currentToken);

				// Store this token if it's the last one, or the next token starts with a "-"
				if (i + 1 == split.length) {
					// We're at the end of the tokens, so store this one and stop processing
					store(result, currentOption, currentValue);
					break;
				}

				if (split[i + 1].startsWith("--")) {
					// A new token is being started next iteration, so store this one now
					store(result, currentOption, currentValue);
					currentOption = new StringBuilder();
					currentValue = new StringBuilder();
				}

				continue;
			}

			// This is an ordinary token, so store it now
			currentValue.append(currentToken);
			store(result, currentOption, currentValue);
			currentOption = new StringBuilder();
			currentValue = new StringBuilder();
		}

		// Strip out an empty default option, if it was returned (ROO-379)
		if (result.containsKey("") && result.get("").trim().equals("")) {
			result.remove("");
		}

		return result;
	}

	private static void store(final Map<String, String> results, final StringBuilder currentOption, final StringBuilder currentValue) {
		if (currentOption.length() > 0) {
			// There is an option marker
			String option = currentOption.toString();
			Assert.isTrue(!results.containsKey(option), "You cannot specify option '" + option + "' more than once in a single command");
			results.put(option, currentValue.toString());
		} else {
			// There was no option marker, so verify this isn't the first
			Assert.isTrue(!results.containsKey(""), "You cannot add more than one default option ('" + currentValue.toString() + "') in a single command");
			results.put("", currentValue.toString());
		}
	}
}
