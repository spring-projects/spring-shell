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
 * Base class defining a settings for themes.
 *
 * @author Janne Valkealahti
 */
public abstract class ThemeSettings {

	private StyleSettings styleSettings;
	private FigureSettings figureSettings;

	/**
	 * Creates theme settings with dump styles and figures.
	 */
	public ThemeSettings() {
		this(StyleSettings.dump(), FigureSettings.dump());
	}

	/**
	 * Creates theme settings with styles and figures.
	 *
	 * @param styleSettings style settings
	 * @param figureSettings figure settings
	 */
	public ThemeSettings(StyleSettings styleSettings, FigureSettings figureSettings) {
		this.styleSettings = styleSettings;
		this.figureSettings = figureSettings;
	}

	/**
	 * Gets a {@link StyleSettings}.
	 *
	 * @return style settings
	 */
	public StyleSettings styles() {
		return this.styleSettings;
	}

	/**
	 * Gets a {@link FigureSettings}.
	 *
	 * @return figure settings
	 */
	public FigureSettings figures() {
		return this.figureSettings;
	}

	/**
	 * Gets a default theme settings.
	 *
	 * @return default theme settings
	 */
	public static ThemeSettings defaults() {
		return new DefaultThemeSettings(StyleSettings.defaults(), FigureSettings.defaults());
	}

	/**
	 * Gets a dump theme settings.
	 *
	 * @return dump theme settings
	 */
	public static ThemeSettings dump() {
		return new DefaultThemeSettings();
	}

	private static class DefaultThemeSettings extends ThemeSettings {

		DefaultThemeSettings() {
			super();
		}

		DefaultThemeSettings(StyleSettings styleSettings, FigureSettings figureSettings) {
			super(styleSettings, figureSettings);
		}
	}
}
