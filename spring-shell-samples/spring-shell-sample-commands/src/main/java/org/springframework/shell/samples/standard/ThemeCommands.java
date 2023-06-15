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
package org.springframework.shell.samples.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.style.FigureSettings;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.ThemeResolver;

@ShellComponent
public class ThemeCommands {

	List<String> colorGround = Arrays.asList("fg", "bg");
	List<String> colors = Arrays.asList("black", "red", "green", "yellow", "blue", "magenta", "cyan", "white");
	List<String> named = Arrays.asList("default", "bold", "faint", "italic", "underline", "blink", "inverse",
			"inverseneg", "conceal", "crossedout", "hidden");
	List<String> rgbRedHue = Arrays.asList("#ff0000", "#ff4000", "#ff8000", "#ffbf00", "#ffff00", "#bfff00", "#80ff00",
			"#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff", "#0040ff",
			"#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000");
	List<String> themeTags = Arrays.asList(StyleSettings.TAG_TITLE, StyleSettings.TAG_VALUE, StyleSettings.TAG_LIST_KEY,
			StyleSettings.TAG_LIST_VALUE, StyleSettings.TAG_LEVEL_INFO, StyleSettings.TAG_LEVEL_WARN,
			StyleSettings.TAG_LEVEL_ERROR, StyleSettings.TAG_ITEM_ENABLED, StyleSettings.TAG_ITEM_DISABLED,
			StyleSettings.TAG_ITEM_SELECTED, StyleSettings.TAG_ITEM_UNSELECTED, StyleSettings.TAG_ITEM_SELECTOR);

	@Autowired
	private ThemeResolver themeResolver;

	@ShellMethod(key = "theme showcase values", value = "Showcase colors and styles", group = "Styles")
	public AttributedString showcaseValues() {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		combinations1().stream()
				.forEach(spec -> {
					AttributedStyle style = themeResolver.resolveStyle(spec);
					AttributedString styledStr = new AttributedString(spec, style);
					builder.append(String.format("%-25s", spec));
					builder.append(" ");
					builder.append(styledStr);
					builder.append("\n");
				});
		return builder.toAttributedString();
	}

	@ShellMethod(key = "theme showcase rgb", value = "Showcase colors and styles with rgb", group = "Styles")
	public AttributedString showcaseRgb() {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		combinations2().stream()
				.forEach(spec -> {
					AttributedStyle style = themeResolver.resolveStyle(spec);
					AttributedString styledStr = new AttributedString(spec, style);
					builder.append(String.format("%-25s", spec));
					builder.append(" ");
					builder.append(styledStr);
					builder.append("\n");
				});
		return builder.toAttributedString();
	}

	@ShellMethod(key = "theme style list", value = "List styles", group = "Styles")
	public AttributedString styleList() {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		themeTags.stream()
				.forEach(tag -> {
					String resolvedStyle = themeResolver.resolveStyleTag(tag);
					AttributedStyle style = themeResolver.resolveStyle(resolvedStyle);
					AttributedString styledStr = new AttributedString(tag, style);
					builder.append(String.format("%-25s", tag));
					builder.append(" ");
					builder.append(styledStr);
					builder.append("\n");
				});
		return builder.toAttributedString();
	}

	@ShellMethod(key = "theme style resolve", value = "Resolve given style", group = "Styles")
	public AttributedString styleResolve(
		@ShellOption(value = "--spec", defaultValue = "default") String spec
	) {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		AttributedStyle style = themeResolver.resolveStyle(spec);
		AttributedString styledStr = new AttributedString(spec, style);
		builder.append(styledStr);
		builder.append("\n");
		return builder.toAttributedString();
	}

	@ShellMethod(key = "theme expression resolve", value = "Resolve given style expression", group = "Styles")
	public AttributedString expressionResolve(
		@ShellOption(value = "--expression", defaultValue = "hi @{bold from} expression") String expression
	) {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		AttributedString styledStr = themeResolver.evaluateExpression(expression);
		builder.append(styledStr);
		builder.append("\n");
		return builder.toAttributedString();
	}

	@ShellMethod(key = "theme figure list", value = "List figures", group = "Styles")
	public AttributedString figureList() {
		AttributedStringBuilder builder = new AttributedStringBuilder();
		Stream.of(FigureSettings.tags())
				.forEach(tag -> {
					builder.append(String.format("%-25s", tag));
					builder.append(" ");
					String resolveFigureTag = themeResolver.resolveFigureTag(tag);
					combinations3().stream().forEach(spec -> {
						AttributedStyle style = themeResolver.resolveStyle(spec);
						builder.append(" ");
						builder.append(new AttributedString(resolveFigureTag, style));
					});
					builder.append("\n");
				});
		return builder.toAttributedString();
	}

	private List<String> combinations1() {
		List<String> styles = new ArrayList<>();
		colorGround.stream().forEach(ground -> {
			colors.stream().forEach(color -> {
				named.stream().forEach(named -> {
					styles.add(String.format("%s,%s:%s", named, ground, color));
				});
			});
		});
		return styles;
	}

	private List<String> combinations2() {
		List<String> styles = new ArrayList<>();
		rgbRedHue.stream().forEach(rgb -> {
			styles.add(String.format("inverse,fg-rgb:%s", rgb));
		});
		return styles;
	}

	private List<String> combinations3() {
		List<String> styles = new ArrayList<>();
		Arrays.asList("fg").stream().forEach(ground -> {
			Arrays.asList("white").stream().forEach(color -> {
				named.stream().forEach(named -> {
					styles.add(String.format("%s,%s:%s", named, ground, color));
				});
			});
		});
		return styles;
	}
}
