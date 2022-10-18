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

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.stringtemplate.v4.AttributeRenderer;

import org.springframework.shell.style.PartsText.PartText;
import org.springframework.util.Assert;

public class PartsTextRenderer implements AttributeRenderer<PartsText> {

	private final ThemeResolver themeResolver;

	public PartsTextRenderer(ThemeResolver themeResolver) {
		Assert.notNull(themeResolver, "themeResolver must be set");
		this.themeResolver = themeResolver;
	}

	@Override
	public String toString(PartsText value, String formatString, Locale locale) {
		StringBuilder buf = new StringBuilder();
		Values values = mapValues(formatString);

		int len = 0;
		int dots = 2;
		int prefix = values.prefix;
		int width = values.width;
		int max = width - prefix;
		List<PartText> parts = value.getParts();
		for (int i = 0; i < parts.size(); i++) {
			PartText pt = parts.get(i);
			String text;
			boolean doBreak = false;

			int newLen = len + pt.getText().length();

			// if current would take over max length
			if (newLen > max) {
				int l = max - len - dots;
				text = String.format(locale, "%1." + l + "s.." , pt.getText());
				doBreak = true;
			}
			// if next would take over max length
			else if (i + 1 < parts.size() && newLen + parts.get(i + 1).getText().length() > max) {
				int l = max - len - dots;
				text = String.format(locale, "%1." + l + "s.." , pt.getText());
				doBreak = true;
			}
			// we're fine as is
			else {
				text = pt.getText();
			}
			String tag = pt.isMatch() ? values.matchStyle : values.textStyle;
			buf.append(String.format("@{%s %s}", themeResolver.resolveStyleTag(tag), text));
			len += pt.getText().length();
			if (doBreak) {
				break;
			}
		}

		return buf.toString();
	}

	private static class Values {
		Integer width;
		Integer prefix;
		String textStyle;
		String matchStyle;

		public void setWidth(Integer width) {
			this.width = width;
		}
		public void setPrefix(Integer prefix) {
			this.prefix = prefix;
		}
		public void setTextStyle(String textStyle) {
			this.textStyle = textStyle;
		}
		public void setMatchStyle(String matchStyle) {
			this.matchStyle = matchStyle;
		}
	}

	private static Values mapValues(String expression) {
		Values values = new Values();
		Stream.of(expression.split(","))
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
					else if ("textStyle".equals(split[0])) {
						values.setTextStyle(split[1]);
					}
					else if ("matchStyle".equals(split[0])) {
						values.setMatchStyle(split[1]);
					}
				}
			});
		return values;
	}
}
