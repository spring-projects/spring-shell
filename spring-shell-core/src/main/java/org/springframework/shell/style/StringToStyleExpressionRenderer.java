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

import java.util.Locale;

import org.jline.style.StyleExpression;
import org.stringtemplate.v4.AttributeRenderer;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@code ST} {@link AttributeRenderer} which knows to use format string to
 * render strings into {@code jline} {@link StyleExpression} based on theming
 * settings.
 *
 * @author Janne Valkealahti
 */
public class StringToStyleExpressionRenderer implements AttributeRenderer<String> {

	private final ThemeResolver themeResolver;

	public StringToStyleExpressionRenderer(ThemeResolver themeResolver) {
		Assert.notNull(themeResolver, "themeResolver must be set");
		this.themeResolver = themeResolver;
	}

	@Override
	public String toString(String value, String formatString, Locale locale) {
		if (!StringUtils.hasText(formatString)) {
			return value;
		}
		else {
			return String.format("@{%s %s}", themeResolver.resolveTag(formatString), value);
		}
	}
}
