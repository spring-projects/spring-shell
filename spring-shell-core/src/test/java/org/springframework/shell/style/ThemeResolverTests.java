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

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThemeResolverTests {

	@Test
	public void test() {
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
		ThemeResolver themeResolver = new ThemeResolver(themeRegistry, "default");
		assertThat(themeResolver.resolveStyleTag(StyleSettings.TAG_TITLE)).isEqualTo("bold");
		assertThat(themeResolver.resolveStyle("bold")).isEqualTo(AttributedStyle.BOLD);
		assertThat(themeResolver.evaluateExpression("@{bold foo}"))
				.isEqualTo(new AttributedString("foo", AttributedStyle.BOLD));
	}
}
