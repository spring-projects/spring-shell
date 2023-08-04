/*
 * Copyright 2022-2023 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * Registry which stores {@link Theme}'s with its name.
 *
 * @author Janne Valkealahti
 */
public class ThemeRegistry {

	private final Map<String, Theme> themes = new HashMap<>();

	/**
	 * Gets a theme from a registry.
	 *
	 * @param name the theme name
	 * @return a theme
	 */
	public Theme get(String name) {
		return themes.get(name);
	}

	/**
	 * Register a theme.
	 *
	 * @param theme the theme
	 */
	public void register(Theme theme) {
		Assert.notNull(theme, "theme cannot be null");
		themes.put(theme.getName(), theme);
	}

	/**
	 * Gets all theme names registered.
	 *
	 * @return theme names
	 */
	public Set<String> getThemeNames() {
		return themes.keySet();
	}
}
