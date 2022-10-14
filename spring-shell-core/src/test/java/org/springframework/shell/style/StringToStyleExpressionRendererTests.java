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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringToStyleExpressionRendererTests {

	private static Locale LOCALE = Locale.getDefault();
	private static StringToStyleExpressionRenderer renderer;

	@BeforeAll
	static void setup() {
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
		renderer = new StringToStyleExpressionRenderer(themeResolver);
	}

	@Test
	void emptyFormatReturnsValue() {
		String value = renderer.toString("fake", null, LOCALE);
		assertThat(value).isEqualTo("fake");
	}

	static Stream<Arguments> truncate() {
		return Stream.of(
			Arguments.of("0123456789", "truncate-width:6-prefix:2", "01.."),
			Arguments.of("0123456789", "truncate-width:6-prefix:0", "0123.."),
			Arguments.of("0123456789", "truncate-width:11-prefix:0", "0123456789")
		);
	}

	@ParameterizedTest
	@MethodSource
	void truncate(String value, String expression, String expected) {
		assertThat(renderer.toString(value, expression, LOCALE)).isEqualTo(expected);
	}
}
