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
package org.springframework.shell.component.view.control;

import java.util.Optional;

import org.jline.utils.AttributedStyle;

import org.springframework.lang.Nullable;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeResolver.ResolvedValues;

/**
 * Base implementation of a {@link Control}.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractControl implements Control {

	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private ThemeResolver themeResolver;
	private String themeName;

	@Override
	public void setRect(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(x, y, width, height);
	}

	/**
	 * Sets a {@link ThemeResolver}.
	 *
	 * @param themeResolver the theme resolver
	 */
	public void setThemeResolver(@Nullable ThemeResolver themeResolver) {
		this.themeResolver = themeResolver;
	}

	/**
	 * Gets a {@link ThemeResolver}.
	 *
	 * @return a theme resolver
	 */
	@Nullable
	protected ThemeResolver getThemeResolver() {
		return themeResolver;
	}

	/**
	 * Sets a theme name to use.
	 *
	 * @param themeName the theme name
	 */
	public void setThemeName(@Nullable String themeName) {
		this.themeName = themeName;
	}

	/**
	 * Gets a theme name.
	 *
	 * @return a theme name
	 */
	@Nullable
	protected String getThemeName() {
		return themeName;
	}

	private Optional<ResolvedValues> getThemeResolvedValues(String tag) {
		ThemeResolver themeResolver = getThemeResolver();
		if (themeResolver != null) {
			String styleTag = themeResolver.resolveStyleTag(tag, getThemeName());
			AttributedStyle attributedStyle = themeResolver.resolveStyle(styleTag);
			return Optional.of(themeResolver.resolveValues(attributedStyle));
		}
		return Optional.empty();
	}

	/**
	 * Resolve style using existing {@link ThemeResolver} and {@code theme name}.
	 * Use {@code defaultStyle} if resolving cannot happen.
	 *
	 * @param tag the style tag to use
	 * @param defaultStyle the default style to use
	 * @return resolved style
	 */
	protected int resolveThemeStyle(String tag, int defaultStyle) {
		return getThemeResolvedValues(tag).map(ResolvedValues::style).orElse(defaultStyle);
	}

	/**
	 * Resolve foreground color using existing {@link ThemeResolver} and {@code theme name}.
	 * {@code defaultColor} is used if it's value is not negative. {@code fallbackColor} is
	 * used if theme resolver cannot be used.
	 *
	 * @param tag the style tag to use
	 * @param defaultColor the default foreground color to use
	 * @param fallbackColor the fallback foreground color to use
	 * @return resolved foreground color
	 */
	protected int resolveThemeForeground(String tag, int defaultColor, int fallbackColor) {
		if (defaultColor > -1) {
			return defaultColor;
		}
		return getThemeResolvedValues(tag).map(ResolvedValues::foreground).orElse(fallbackColor);
	}

	/**
	 * Resolve background color using existing {@link ThemeResolver} and {@code theme name}.
	 * {@code defaultColor} is used if it's value is not negative. {@code fallbackColor} is
	 * used if theme resolver cannot be used.
	 *
	 * @param tag the style tag to use
	 * @param defaultColor the default background color to use
	 * @param fallbackColor the fallback background color to use
	 * @return resolved background color
	 */
	protected int resolveThemeBackground(String tag, int defaultColor, int fallbackColor) {
		if (defaultColor > -1) {
			return defaultColor;
		}
		return getThemeResolvedValues(tag).map(ResolvedValues::background).orElse(fallbackColor);
	}

	/**
	 * Resolve {@link Spinner} using existing {@link ThemeResolver} and {@code theme name}.
	 * {@code defaultSpinner} is used if it's not {@code null}. {@code fallbackSpinner} is
	 * used if theme resolver cannot be used.
	 *
	 * @param tag the spinner tag to use
	 * @param defaultSpinner the default spinner to use
	 * @param fallbackSpinner the fallback spinner to use
	 * @return resolved spinner
	 */
	protected Spinner resolveThemeSpinner(String tag, Spinner defaultSpinner, Spinner fallbackSpinner) {
		if (defaultSpinner != null) {
			return defaultSpinner;
		}
		Spinner spinner = null;
		ThemeResolver themeResolver = getThemeResolver();
		if (themeResolver != null) {
			spinner = getThemeResolver().resolveSpinnerTag(tag);
		}
		if (spinner == null) {
			spinner = fallbackSpinner;
		}
		return spinner;
	}

}
