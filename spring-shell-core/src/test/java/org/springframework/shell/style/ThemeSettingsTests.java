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
import org.jline.style.StyleResolver;
import org.jline.style.StyleSource;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThemeSettingsTests {

	@Test
	public void test() {
		ThemeSettings themeSettings = ThemeSettings.themeSettings();
		String resolveTag = themeSettings.resolveTag(ThemeSettings.TAG_TITLE);
		assertThat(resolveTag).isEqualTo("bold");

		StyleSource styleSource = new MemoryStyleSource();
		StyleResolver styleResolver = new StyleResolver(styleSource, "default");
		AttributedStyle style = styleResolver.resolve(resolveTag);
		assertThat(style).isEqualTo(AttributedStyle.BOLD);
	}

}
