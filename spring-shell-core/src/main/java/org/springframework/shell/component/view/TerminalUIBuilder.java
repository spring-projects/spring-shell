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
package org.springframework.shell.component.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jline.terminal.Terminal;

import org.springframework.shell.style.ThemeResolver;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Builder that can be used to configure and create a {@link TerminalUI}.
 *
 * @author Janne Valkealahti
 */
public class TerminalUIBuilder {

	private final Terminal terminal;
	private final Set<TerminalUICustomizer> customizers;
	private final ThemeResolver themeResolver;
	private final String themeName;

	/**
	 * Create a new {@link TerminalUIBuilder} instance.
	 *
	 * @param terminal the terminal
	 * @param customizers any {@link TerminalUICustomizer TerminalUICustomizers}
	 *                    that should be applied when the {@link TerminalUI} is
	 *                    built
	 */
	public TerminalUIBuilder(Terminal terminal, TerminalUICustomizer... customizers) {
		this.terminal = terminal;
		this.customizers = copiedSetOf(customizers);
		this.themeResolver = null;
		this.themeName = null;
	}

	/**
	 * Create a new {@link TerminalUIBuilder} instance.
	 *
	 * @param terminal the terminal
	 * @param customizers any {@link TerminalUICustomizer TerminalUICustomizers}
	 *                    that should be applied when the {@link TerminalUI} is
	 *                    built
	 * @param themeResolver the theme resolver
	 * @param themeName the theme name
	 */
	public TerminalUIBuilder(Terminal terminal, Set<TerminalUICustomizer> customizers, ThemeResolver themeResolver,
			String themeName) {
		this.terminal = terminal;
		this.customizers = customizers;
		this.themeResolver = themeResolver;
		this.themeName = themeName;
	}

	/**
	 * Sets a {@link ThemeResolver} for {@link TerminalUI} to build.
	 *
	 * @param themeResolver the theme resolver
	 * @return a new builder instance
	 */
	public TerminalUIBuilder themeResolver(ThemeResolver themeResolver) {
		return new TerminalUIBuilder(terminal, customizers, themeResolver, themeName);
	}

	/**
	 * Sets a {@code theme name} for {@link TerminalUI} to build.
	 *
	 * @param themeName the theme name
	 * @return a new builder instance
	 */
	public TerminalUIBuilder themeName(String themeName) {
		return new TerminalUIBuilder(terminal, customizers, themeResolver, themeName);
	}

	/**
	 * Set the {@link TerminalUICustomizer TerminalUICustomizer} that should be
	 * applied to the {@link TerminalUI}. Customizers are applied in the order that they
	 * were added after builder configuration has been applied. Setting this value will
	 * replace any previously configured customizers.
	 *
	 * @param customizers the customizers to set
	 * @return a new builder instance
	 */
	public TerminalUIBuilder customizers(Collection<? extends TerminalUICustomizer> customizers) {
		Assert.notNull(customizers, "Customizers must not be null");
		return new TerminalUIBuilder(terminal, copiedSetOf(customizers), themeResolver, themeName);
	}

	/**
	 * Build a new {@link TerminalUI} instance and configure it using this builder.
	 *
	 * @return a configured {@link TerminalUI} instance.
	 */
	public TerminalUI build() {
		return configure(new TerminalUI(terminal));
	}

	/**
	 * Configure the provided {@link TerminalUI} instance using this builder.
	 *
	 * @param <T> the type of terminal ui
	 * @param terminalUI the {@link TerminalUI} to configure
	 * @return the terminal ui instance
	 */
	public <T extends TerminalUI> T configure(T terminalUI) {
		if (themeResolver != null) {
			terminalUI.setThemeResolver(themeResolver);
		}
		if (StringUtils.hasText(themeName)) {
			terminalUI.setThemeName(themeName);
		}
		if (!CollectionUtils.isEmpty(customizers)) {
			for (TerminalUICustomizer customizer : customizers) {
				customizer.customize(terminalUI);
			}
		}
		return terminalUI;
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> copiedSetOf(T... items) {
		return copiedSetOf(Arrays.asList(items));
	}

	private <T> Set<T> copiedSetOf(Collection<? extends T> collection) {
		return Collections.unmodifiableSet(new LinkedHashSet<>(collection));
	}

}
