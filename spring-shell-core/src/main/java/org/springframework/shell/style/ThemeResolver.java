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

import org.jline.style.MemoryStyleSource;
import org.jline.style.StyleExpression;
import org.jline.style.StyleResolver;
import org.jline.style.StyleSource;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

/**
 * Service which helps to do various things with styles.
 *
 * @author Janne Valkealahti
 */
public class ThemeResolver {

	private StyleSource styleSource = new MemoryStyleSource();
	private StyleResolver styleResolver = new StyleResolver(styleSource, "default");
	private StyleExpression styleExpression = new StyleExpression(styleResolver);
	private final Theme theme;

	public ThemeResolver(ThemeRegistry themeRegistry, String themeName) {
		this.theme = themeRegistry.get(themeName);
	}

	/**
	 * Evaluate expression.
	 *
	 * @param expression the expression
	 * @return evaluated attributed string
	 */
	public AttributedString evaluateExpression(String expression) {
		return styleExpression.evaluate(expression);
	}

	/**
	 * Resolve style from a tag with activated theme.
	 *
	 * @param tag the tag
	 * @return a style
	 */
	public String resolveStyleTag(String tag) {
		return theme.getSettings().styles().resolveTag(tag);
	}

	/**
	 * Resolve figure from a tag with activated theme.
	 *
	 * @param tag the tag
	 * @return a style
	 */
	public String resolveFigureTag(String tag) {
		return theme.getSettings().figures().resolveTag(tag);
	}

	/**
	 * Resolve {@link AttributedStyle} from a {@code spec}.
	 *
	 * @param spec the spec
	 * @return resolved attributed style
	 */
	public AttributedStyle resolveStyle(String spec) {
		return styleResolver.resolve(spec);
	}
}
