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

import org.jline.utils.AttributedString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.shell.style.PartsText.PartText;

import static org.assertj.core.api.Assertions.assertThat;

class PartsTextRendererTests {

	private static Locale LOCALE = Locale.getDefault();
	private static PartsTextRenderer renderer;
	private static ThemeResolver themeResolver;

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
		themeResolver = new ThemeResolver(themeRegistry, "default");
		renderer = new PartsTextRenderer(themeResolver);
	}

	static PartsText of() {
		return PartsText.of(
			PartText.of("012", false),
			PartText.of("3456", true),
			PartText.of("789", false)
		);
	}

	static Stream<Arguments> test() {
		return Stream.of(
			Arguments.of(
				"width:10,prefix:2,textStyle:style-item-selector,matchStyle:style-level-warn",
				PartsText.of(
					PartText.of("01234567", false)
				),
				"01234567"),
			Arguments.of(
				"width:10,prefix:2,textStyle:style-item-selector,matchStyle:style-level-warn",
				PartsText.of(
					PartText.of("0123456789", false)
				),
				"012345.."),
			Arguments.of(
				"width:10,prefix:0,textStyle:style-item-selector,matchStyle:style-level-warn",
				PartsText.of(
					PartText.of("01234", false),
					PartText.of("56789", true)
				),
				"0123456789"),
			Arguments.of(
				"width:10,prefix:2,textStyle:style-item-selector,matchStyle:style-level-warn",
				PartsText.of(
					PartText.of("012", false),
					PartText.of("3456", true),
					PartText.of("789", false)
				),
				"012345..")
		);
	}

	@ParameterizedTest
	@MethodSource
	void test(String expression, PartsText text, String expected) {
		String rendered = renderer.toString(text, expression, LOCALE);
		AttributedString evaluated = themeResolver.evaluateExpression(rendered);
		String raw = AttributedString.stripAnsi(evaluated.toAnsi());
		assertThat(raw).isEqualTo(expected);
	}

}
