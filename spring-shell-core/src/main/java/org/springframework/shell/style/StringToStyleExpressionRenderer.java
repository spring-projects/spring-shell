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
import java.util.stream.Stream;

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
	private final static String TRUNCATE = "truncate-";

	public StringToStyleExpressionRenderer(ThemeResolver themeResolver) {
		Assert.notNull(themeResolver, "themeResolver must be set");
		this.themeResolver = themeResolver;
	}

	@Override
	public String toString(String value, String formatString, Locale locale) {
		if (!StringUtils.hasText(formatString)) {
			return value;
		}
		else if (formatString.startsWith("style-")) {
			return String.format("@{%s %s}", themeResolver.resolveStyleTag(formatString), value);
		}
		else if (formatString.startsWith(TRUNCATE)) {
			String f = formatString.substring(TRUNCATE.length());
			TruncateValues config = mapValues(f);
			if (value.length() + config.prefix > config.width) {
				return String.format(locale, "%1." + (config.width - config.prefix - 2) + "s.." , value);
			}
			else {
				return value;
			}
		}
		else {
			return String.format(locale, formatString, value);
		}
	}

	private static class TruncateValues {
		Integer width;
		Integer prefix;

		public void setWidth(Integer width) {
			this.width = width;
		}
		public void setPrefix(Integer prefix) {
			this.prefix = prefix;
		}
	}

	private static TruncateValues mapValues(String expression) {
		TruncateValues values = new TruncateValues();
		Stream.of(expression.split("-"))
			.map(String::trim)
			.forEach(v -> {
				String[] split = v.split(":", 2);
				if (split.length == 2) {
					if ("width".equals(split[0])) {
						values.setWidth(Integer.parseInt(split[1]));
					}
					else if ("prefix".equals(split[0])) {
						values.setPrefix(Integer.parseInt(split[1]));
					}
				}
			});
		return values;
	}
}
