/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.style;

/**
 * {@link ThemeSettings} is storing {@link Theme} related settings together and
 * is base class for further customizations.
 *
 * Settings in this base class are default styles.
 *
 * @author Janne Valkealahti
 */
public abstract class ThemeSettings {

	/**
	 * Represents some arbitrary {@code title}.
	 */
	public final static String TAG_TITLE = "title";

	/**
	 * Represents some arbitrary {@code value}.
	 */
	public final static String TAG_VALUE = "value";

	/**
	 * Styling for keys or names in a lists:
	 * <list key1> : list value1
	 * <list key2> : list value2
	 */
	public final static String TAG_LIST_KEY = "list-key";

	/**
	 * Styling for keys or names in a lists:
	 * list key1 : <list value1>
	 * list key2 : <list value2>
	 */
	public final static String TAG_LIST_VALUE = "list-value";

	public String title() {
		return "bold";
	}

	public String value() {
		return "fg:blue";
	}

	public String listKey() {
		return null;
	}

	public String listValue() {
		return "bold,fg:green";
	}

	/**
	 * Resolve a theme setting from a given tag.
	 *
	 * @param tag the tag
	 * @return a theme setting
	 */
	public String resolveTag(String tag) {
		switch (tag) {
			case TAG_TITLE:
				return title();
			case TAG_VALUE:
				return value();
			case TAG_LIST_KEY:
				return listKey();
			case TAG_LIST_VALUE:
				return listValue();
		}
		throw new IllegalArgumentException(String.format("Unknown tag '%s'", tag));
	}

	/**
	 * Creates an instance of a default settings.
	 *
	 * @return a default theme settings
	 */
	public static ThemeSettings themeSettings() {
		return new DefaultThemeSettings();
	}

	private static class DefaultThemeSettings extends ThemeSettings {
	}
}
