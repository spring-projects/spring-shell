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
package org.springframework.shell.docs;

import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.style.FigureSettings;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeSettings;

@SuppressWarnings("unused")
public class ThemingSnippets {

	// tag::custom-style-class[]
	static class MyStyleSettings extends StyleSettings {

		@Override
		public String highlight() {
			return super.highlight();
		}
	}
	// end::custom-style-class[]

	// tag::custom-figure-class[]
	static class MyFigureSettings extends FigureSettings {

		@Override
		public String error() {
			return super.error();
		}
	}
	// end::custom-figure-class[]

	// tag::custom-theme-class[]
	static class MyThemeSettings extends ThemeSettings {

		@Override
		public StyleSettings styles() {
			return new MyStyleSettings();
		}

		@Override
		public FigureSettings figures() {
			return new MyFigureSettings();
		}
	}
	// end::custom-theme-class[]

	// tag::custom-theme-config[]
	@Configuration
	static class CustomThemeConfig {

		@Bean
		Theme myTheme() {
			return new Theme() {
				@Override
				public String getName() {
					return "mytheme";
				}

				@Override
				public ThemeSettings getSettings() {
					return new MyThemeSettings();
				}
			};
		}
	}
	// end::custom-theme-config[]

	class Dump1 {

		// tag::using-theme-resolver[]
		@Autowired
		private ThemeResolver resolver;

		void resolve() {
			String resolvedStyle = resolver.resolveStyleTag(StyleSettings.TAG_TITLE);
			// bold,fg:bright-white

			AttributedStyle style = resolver.resolveStyle(resolvedStyle);
			// jline attributed style from expression above

			String resolvedFigure = resolver.resolveFigureTag(FigureSettings.TAG_ERROR);
			// character i.e. U+2716 Heavy Multiplication X Emoji, cross
		}
		// end::using-theme-resolver[]
	}

}
