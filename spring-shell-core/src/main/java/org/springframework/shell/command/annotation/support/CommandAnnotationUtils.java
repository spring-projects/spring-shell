/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.command.annotation.support;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.lang.Nullable;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.context.InteractionMode;
import org.springframework.util.StringUtils;

/**
 * Utilities to merge {@link Command} annotations using opinionated logic. In
 * this class {@code left} is meant for annotation on a class level and
 * {@code right} annotation on a method level. Class level is meant to provide
 * defaults and every field may have its own logic.
 *
 * @author Janne Valkealahti
 */
class CommandAnnotationUtils {

	private final static String COMMAND = "command";
	private final static String ALIAS = "alias";
	private final static String HIDDEN = "hidden";
	private final static String GROUP = "group";
	private final static String DESCRIPTION = "description";
	private final static String INTERACTION_MODE = "interactionMode";

	/**
	 * Deduce {@link Command#hidden()} from annotations.
	 *
	 * @param left the left side annotation
	 * @param right the right side annotation
	 * @return deduced boolean for hidden field
	 */
	static boolean deduceHidden(MergedAnnotation<?> left, MergedAnnotation<?> right) {
		Boolean def = right.getDefaultValue(HIDDEN, Boolean.class).orElse(null);

		boolean l = left.getBoolean(HIDDEN);
		boolean r = right.getBoolean(HIDDEN);

		if (def != null) {
			if (def != r) {
				l = r;
			}
		}
		else {
			l = r;
		}

		return l;
	}

	/**
	 * Deduce {@link Command#command()} from annotations. Command array is supposed
	 * to contain commands without leading or trailing white spaces, so strip, split
	 * and assume that class level defines prefix for array.
	 *
	 * @param left  the left side annotation
	 * @param right the right side annotation
	 * @return deduced boolean for command field
	 */
	static String[] deduceCommand(MergedAnnotation<?> left, MergedAnnotation<?> right) {
		return deduceStringArray(COMMAND, left, right);
	}

	/**
	 * Deduce {@link Command#alias()} from annotations. Alias array is supposed
	 * to contain commands without leading or trailing white spaces, so strip, split
	 * and assume that class level defines prefix for array.
	 *
	 * @param left  the left side annotation
	 * @param right the right side annotation
	 * @return deduced arrays for alias field
	 */
	static String[][] deduceAlias(MergedAnnotation<?> left, MergedAnnotation<?> right) {
		return deduceStringArrayLeftPrefixes(ALIAS, left, right);
	}

	/**
	 * Deduce {@link Command#group()} from annotations. Right side overrides if it
	 * has value.
	 *
	 * @param left  the left side annotation
	 * @param right the right side annotation
	 * @return deduced String for group field
	 */
	static String deduceGroup(MergedAnnotation<?> left, MergedAnnotation<?> right) {
		return deduceStringRightOverrides(GROUP, left, right);
	}

	/**
	 * Deduce {@link Command#description()} from annotations. Right side overrides if it
	 * has value.
	 *
	 * @param left  the left side annotation
	 * @param right the right side annotation
	 * @return deduced String for description field
	 */
	static String deduceDescription(MergedAnnotation<?> left, MergedAnnotation<?> right) {
		return deduceStringRightOverrides(DESCRIPTION, left, right);
	}

	/**
	 * Deduce {@link Command#interactionMode()} from annotations. Right side overrides if.
	 * Returns {@code null} if nothing defined.
	 *
	 * @param left  the left side annotation
	 * @param right the right side annotation
	 * @return deduced InteractionMode for interaction mode field
	 */
	static @Nullable InteractionMode deduceInteractionMode(MergedAnnotation<?> left, MergedAnnotation<?> right) {
		InteractionMode mode = null;
		InteractionMode[] l = left.getEnumArray(INTERACTION_MODE, InteractionMode.class);
		for (InteractionMode m : l) {
			if (InteractionMode.ALL == m) {
				mode = m;
				break;
			}
			else {
				mode = m;
			}
		}
		InteractionMode[] r = right.getEnumArray(INTERACTION_MODE, InteractionMode.class);
		for (InteractionMode m : r) {
			if (InteractionMode.ALL == m) {
				mode = m;
				break;
			}
			else {
				mode = m;
				break;
			}
		}
		return mode;
	}

	private static String[][] deduceStringArrayLeftPrefixes(String field, MergedAnnotation<?> left, MergedAnnotation<?> right) {
		List<String> prefix = Stream.of(left.getStringArray(field))
			.flatMap(command -> Stream.of(command.split(" ")))
			.filter(command -> StringUtils.hasText(command))
			.map(command -> command.strip())
			.collect(Collectors.toList());

		return Stream.of(right.getStringArray(field))
			.map(command -> command.strip())
			.map(command -> Stream.concat(
					prefix.stream(),
					Stream.of(command).filter(c -> StringUtils.hasText(c)))
				.collect(Collectors.toList()))
			.map(arr -> arr.toArray(String[]::new))
			.toArray(String[][]::new);
	}

	private static String[] deduceStringArray(String field, MergedAnnotation<?> left, MergedAnnotation<?> right) {
		return Stream.of(left.getStringArray(field), right.getStringArray(field))
				.flatMap(commands -> Stream.of(commands))
				.flatMap(command -> Stream.of(command.split(" ")))
				.filter(command -> StringUtils.hasText(command))
				.map(command -> command.strip())
				.toArray(String[]::new);
	}

	private static String deduceStringRightOverrides(String field, MergedAnnotation<?> left, MergedAnnotation<?> right) {
		String r = right.getString(field);
		if (StringUtils.hasText(r)) {
			return r;
		}
		return left.getString(field);
	}

}
