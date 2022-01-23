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
 * Contract representing a theme with its name and settings.
 *
 * {@link Theme} is a concept where you can request a {@code style} by using a theme {@code tag}.
 * At this point an actual style is not known as it's going to get resolved from an
 * enable {@code theme}.
 *
 * @author Janne Valkealahti
 */
public interface Theme {

	/**
	 * Gets a theme name.
	 *
	 * @return a theme name.
	 */
	String getName();

	/**
	 * Gets a theme settings.
	 *
	 * @return a theme settings
	 */
	ThemeSettings getSettings();

	/**
	 * Create a {@link Theme}.
	 *
	 * @param name the theme name
	 * @param themeSettings the theme settings
	 * @return a theme
	 */
	public static Theme of(String name, ThemeSettings themeSettings) {
		return new Theme() {

			@Override
			public String getName() {
				return name;
			}

			@Override
			public ThemeSettings getSettings() {
				return themeSettings;
			}
		};
	}
}
