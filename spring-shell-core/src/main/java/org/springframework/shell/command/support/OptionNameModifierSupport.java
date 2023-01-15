/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.command.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.shell.command.CommandRegistration.OptionNameModifier;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Support facilities for {@link OptionNameModifier} providing common naming
 * types.
 *
 * @author Janne Valkealahti
 */
public abstract class OptionNameModifierSupport {

	public static final OptionNameModifier NOOP = name -> name;
	public static final OptionNameModifier CAMELCASE = name -> toCamelCase(name);
	public static final OptionNameModifier SNAKECASE = name -> toSnakeCase(name);
	public static final OptionNameModifier KEBABCASE = name -> toKebabCase(name);
	public static final OptionNameModifier PASCALCASE = name -> toPascalCase(name);

	private static final Pattern PATTERN = Pattern
			.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+");

	/**
	 * Convert given name to {@code camelCase}.
	 *
	 * @param name the name to modify
	 * @return a modified name as camel case
	 */
	public static String toCamelCase(String name) {
		return toCapitalizeCase(name, false, ' ', '-', '_');
	}

	/**
	 * Convert given name to {@code snake_case}.
	 *
	 * @param name the name to modify
	 * @return a modified name as snake case
	 */
	public static String toSnakeCase(String name) {
		return matchJoin(name, "_");
	}

	/**
	 * Convert given name to {@code kebab-case}.
	 *
	 * @param name the name to modify
	 * @return a modified name as kebab case
	 */
	public static String toKebabCase(String name) {
		return matchJoin(name, "-");
	}

	/**
	 * Convert given name to {@code PascalCase}.
	 *
	 * @param name the name to modify
	 * @return a modified name as pascal case
	 */
	public static String toPascalCase(String name) {
		return toCapitalizeCase(name, true, ' ', '-', '_');
	}

	private static String matchJoin(String name, String delimiter) {
		Matcher matcher = PATTERN.matcher(name);
		List<String> matches = new ArrayList<>();
		while (matcher.find()) {
			String group = matcher.group();
			matches.add(group);
		}
		return matches.stream().map(x -> x.toLowerCase()).collect(Collectors.joining(delimiter));
	}

	private static String toCapitalizeCase(String name, final boolean capitalizeFirstLetter, final char... delimiters) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        String nameL = name.toLowerCase();

		final int strLen = nameL.length();
        final int[] newCodePoints = new int[strLen];
        final Set<Integer> delimiterSet = toDelimiterSet(delimiters);

		int outOffset = 0;
        boolean capitalizeNext = capitalizeFirstLetter;

		boolean delimiterFound = false;

		for (int index = 0; index < strLen;) {
            final int codePoint = nameL.codePointAt(index);

            if (delimiterSet.contains(codePoint)) {
                capitalizeNext = outOffset != 0;
                index += Character.charCount(codePoint);
				delimiterFound  = true;
            } else if (capitalizeNext || outOffset == 0 && capitalizeFirstLetter) {
                final int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }

		if (!delimiterFound) {
			if (capitalizeFirstLetter) {
				return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			}
			else {
				return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
			}
		}

        return new String(newCodePoints, 0, outOffset);
    }

    /**
	 * Converts an array of delimiters to a hash set of code points. Code point of
	 * space(32) is added as the default value. The generated hash set provides O(1)
	 * lookup time.
	 *
	 * @param delimiters set of characters to determine capitalization, null means
	 *                   whitespace
	 * @return Integers of code points
	 */
    private static Set<Integer> toDelimiterSet(final char[] delimiters) {
        final Set<Integer> delimiterHashSet = new HashSet<>();
        delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
		if (ObjectUtils.isEmpty(delimiters)) {
            return delimiterHashSet;
		}

        for (int index = 0; index < delimiters.length; index++) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }
}
