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
			PartText current = parts.get(i);
			PartText next = i + 1 < parts.size() ? parts.get(i + 1) : null;

			boolean doBreak = false;
			String text = current.getText();
			int currentLen = len + text.length();
			int nextLen = next != null ? currentLen + next.getText().length() : -1;

			if (currentLen > max - dots && nextLen > 0) {
				int l = max - len;
				int diff = l - text.length();
				if (diff == 1) {
					text = text.substring(0, text.length() - 1) + "..";
				}
				else if (diff == 0) {
					text = String.format(locale, "%1." + (text.length() - 2) + "s.." , text);
				}
				else {
					text = String.format(locale, "%1." + (l - dots) + "s.." , text);
				}
				doBreak = true;
			}
			else if (currentLen == max - dots) {
				text = text + "..";
				doBreak = true;
			}
			else if (currentLen > max) {
				int l = max - len - dots;
				if (l == 0) {
					text = "..";
				}
				else {
					text = String.format(locale, "%1." + l + "s.." , text);
				}
				doBreak = true;
			}

			String tag = current.isMatch() ? values.matchStyle : values.textStyle;
			buf.append(String.format("@{%s %s}", themeResolver.resolveStyleTag(tag), text));
			len += text.length();

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
