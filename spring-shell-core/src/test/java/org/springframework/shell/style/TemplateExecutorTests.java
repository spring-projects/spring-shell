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

import java.util.HashMap;
import java.util.Map;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateExecutorTests {

	private ThemeResolver themeResolver;

	@BeforeEach
	public void setup() {
		ThemeRegistry themeRegistry = new ThemeRegistry();
		themeRegistry.register(new Theme() {
			@Override
			public String getName() {
				return "default";
			}

			@Override
			public ThemeSettings getSettings() {
				return ThemeSettings.defaults();
			}
		});
		themeResolver = new ThemeResolver(themeRegistry, "default");
	}

	@Test
	public void testSimple() {
		TemplateExecutor executor = new TemplateExecutor(themeResolver);
		String template = "<(\"foo\")>";
		AttributedString result = executor.render(template, null);
		AttributedString equalTo = new AttributedStringBuilder().append("foo").toAttributedString();
		assertThat(result).isEqualTo(equalTo);
	}

	@Test
	public void testWithExpression() {
		TemplateExecutor executor = new TemplateExecutor(themeResolver);
		String template = "<foo>";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("foo", "bar");
		AttributedString result = executor.render(template, attributes);
		AttributedString equalTo = new AttributedStringBuilder().append("bar").toAttributedString();
		assertThat(result).isEqualTo(equalTo);
	}

	@Test
	public void testWithTheme() {
		TemplateExecutor executor = new TemplateExecutor(themeResolver);
		String template = "<foo; format=\"style-title\">";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("foo", "bar");
		AttributedString result = executor.render(template, attributes);
		AttributedString equalTo = new AttributedStringBuilder()
				.append("bar", AttributedStyle.DEFAULT.bold())
				.toAttributedString();
		assertThat(result).isEqualTo(equalTo);
	}

	@Test
	public void testNullDontStyle() {
		TemplateExecutor executor = new TemplateExecutor(themeResolver);
		String template = "<foo; format=\"style-list-key\">";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("foo", "bar");
		AttributedString result = executor.render(template, attributes);
		AttributedString equalTo = new AttributedStringBuilder().append("bar").toAttributedString();
		assertThat(result).isEqualTo(equalTo);
	}
}
