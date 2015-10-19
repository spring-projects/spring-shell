/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.table;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides support for different styles of borders, using simple or fancy ascii art.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Box-drawing_character">https://en.wikipedia.org/wiki/Box-drawing_character</a>
 *
 * @author Eric Bottard
 */
public enum BorderStyle {

	/**
	 * A simplistic style, using characters that ought to always be available in all systems (pipe and minus).
	 */
	oldschool('|', '-'),

	/**
	 * A border style that uses dedicated light box drawing characters from the unicode set.
	 */
	fancy_light('│', '─'),

	/**
	 * A border style that uses dedicated fat box drawing characters from the unicode set.
	 */
	fancy_heavy('┃', '━'),

	/**
	 * A border style that uses dedicated double-light box drawing characters from the unicode set.
	 */
	fancy_double('║', '═'),

	/**
	 * A border style that uses space characters, giving some space between columns.
	 */
	air(' ', ' '),

	/**
	 * A border style that uses dedicated double dash light box drawing characters from the unicode set.
	 */
	fancy_light_double_dash('╎', '╌'),

	/**
	 * A border style that uses dedicated double dash light box drawing characters from the unicode set.
	 */
	fancy_light_triple_dash('┆', '┄'),

	/**
	 * A border style that uses dedicated double dash light box drawing characters from the unicode set.
	 */
	fancy_light_quadruple_dash('┊', '┈'),

	/**
	 * A border style that uses dedicated double dash heavy box drawing characters from the unicode set.
	 */
	fancy_heavy_double_dash('╏', '╍'),

	/**
	 * A border style that uses dedicated double dash heavy box drawing characters from the unicode set.
	 */
	fancy_heavy_triple_dash('┇', '┅'),

	/**
	 * A border style that uses dedicated double dash heavy box drawing characters from the unicode set.
	 */
	fancy_heavy_quadruple_dash('┋', '┉'),

	;

	private char vertical;

	private char horizontal;

	public static final char NONE = '\u0000';

	private static Map<Long, Character> CORNERS = new HashMap<Long, Character>();

	private static Map<Character, Character> EQUIVALENTS = new HashMap<Character, Character>();

	public char verticalGlyph() {
		return vertical;
	}

	public char horizontalGlyph() {
		return horizontal;
	}

	static {
		registerCorners("─│┌┐└┘├┤┬┴┼");
		registerCorners("━┃┏┓┗┛┣┫┳┻╋");

		// double dashes
		registerCorners("╌╎┌┐└┘├┤┬┴┼");
		registerCorners("╍╏┏┓┗┛┣┫┳┻╋");

		// triple dashes
		registerCorners("┈┆┌┐└┘├┤┬┴┼");
		registerCorners("┅┇┏┓┗┛┣┫┳┻╋");

		// quad dashes
		registerCorners("┈┊┌┐└┘├┤┬┴┼");
		registerCorners("┉┋┏┓┗┛┣┫┳┻╋");

		// double lines
		registerCorners("═║╔╗╚╝╠╣╦╩╬");
		// oldschool
		registerCorners("-|+++++++++");
		// air style
		registerCorners("           ");

		// Register some mixed-style combinations
		// light + heavy
		registerCorner('│', '│', '━', NONE, '┥');
		registerCorner('│', '│', NONE, '━', '┝');
		registerCorner('┃', NONE, '─', '─', '┸');
		registerCorner(NONE, '┃', '─', '─', '┰');
		// heavy + light
		registerCorner('┃', '┃', '─', NONE, '┨');
		registerCorner('┃', '┃', NONE, '─', '┠');
		registerCorner('│', NONE, '━', '━', '┷');
		registerCorner(NONE, '│', '━', '━', '┯');
		// double + single
		registerCorner('║', '║', '─', NONE, '╢');
		registerCorner('║', '║', NONE, '─', '╟');
		registerCorner('│', NONE, '═', '═', '╧');
		registerCorner(NONE, '│', '═', '═', '╤');
		// single + double
		registerCorner('│', '│', '═', NONE, '╡');
		registerCorner('│', '│', NONE, '═', '╞');
		registerCorner('║', NONE, '─', '─', '╨');
		registerCorner(NONE, '║', '─', '─', '╥');
		// heavy + light, 90°
		registerCorner('┃', '│', '━', '─', '╃');
		registerCorner('│', '┃', '─', '━', '╆');
		registerCorner('┃', '│', '─', '━', '╄');
		registerCorner('│', '┃', '━', '─', '╅');
		// light crossing (heavy or double)
		registerCorner('│', '│', '━', '━', '┿');
		registerCorner('│', '│', '═', '═', '╪');
		registerCorner('┃', '┃', '─', '─', '╂');
		registerCorner('║', '║', '─', '─', '╫');

		// Dashed variants crossing others behave like regular corners
		registerSameCorners(fancy_light_double_dash, fancy_light);
		registerSameCorners(fancy_light_triple_dash, fancy_light);
		registerSameCorners(fancy_light_quadruple_dash, fancy_light);
		registerSameCorners(fancy_heavy_double_dash, fancy_heavy);
		registerSameCorners(fancy_heavy_triple_dash, fancy_heavy);
		registerSameCorners(fancy_heavy_quadruple_dash, fancy_heavy);


		// Air-style glyphs are easy to combine with others. Register some combinations
		registerMixedWithAirCombinations(oldschool.vertical, oldschool.horizontal);
		registerMixedWithAirCombinations(fancy_light.vertical, fancy_light.horizontal);
		registerMixedWithAirCombinations(fancy_double.vertical, fancy_double.horizontal);
		registerMixedWithAirCombinations(fancy_heavy.vertical, fancy_heavy.horizontal);

		registerMixedWithAirCombinations(fancy_light_double_dash.vertical, fancy_light_double_dash.horizontal);
		registerMixedWithAirCombinations(fancy_light_triple_dash.vertical, fancy_light_triple_dash.horizontal);
		registerMixedWithAirCombinations(fancy_light_quadruple_dash.vertical, fancy_light_quadruple_dash.horizontal);
		registerMixedWithAirCombinations(fancy_heavy_double_dash.vertical, fancy_heavy_double_dash.horizontal);
		registerMixedWithAirCombinations(fancy_heavy_triple_dash.vertical, fancy_heavy_triple_dash.horizontal);
		registerMixedWithAirCombinations(fancy_heavy_quadruple_dash.vertical, fancy_heavy_quadruple_dash.horizontal);
	}

	/**
	 * Register the fact that for corner purposes, style1 behaves like style2.
	 */
	private static void registerSameCorners(BorderStyle style1, BorderStyle style2) {
		EQUIVALENTS.put(style1.horizontal, style2.horizontal);
		EQUIVALENTS.put(style1.vertical, style2.vertical);
	}

	private static void registerMixedWithAirCombinations(char vertical, char horizontal) {
		registerCorner(vertical, vertical, ' ', NONE, vertical);
		registerCorner(vertical, vertical, NONE, ' ', vertical);
		registerCorner(vertical, vertical, ' ', ' ', vertical);
		registerCorner(' ', NONE, horizontal, horizontal, horizontal);
		registerCorner(NONE, ' ', horizontal, horizontal, horizontal);
		registerCorner(' ', ' ', horizontal, horizontal, horizontal);

	}

	/**
	 * Register corner glyphs for a given set, not taking care of mixed style intersections.
	 */
	private static void registerCorners(String list) {
		char horizontal = list.charAt(0);
		char vertical = list.charAt(1);
		registerCorner(NONE, vertical, NONE, horizontal, list.charAt(2));
		registerCorner(NONE, vertical, horizontal, NONE, list.charAt(3));
		registerCorner(vertical, NONE, NONE, horizontal, list.charAt(4));
		registerCorner(vertical, NONE, horizontal, NONE, list.charAt(5));
		registerCorner(vertical, vertical, NONE, horizontal, list.charAt(6));
		registerCorner(vertical, vertical, horizontal, NONE, list.charAt(7));
		registerCorner(NONE, vertical, horizontal, horizontal, list.charAt(8));
		registerCorner(vertical, NONE, horizontal, horizontal, list.charAt(9));
		registerCorner(vertical, vertical, horizontal, horizontal, list.charAt(10));

	}

	private static void registerCorner(char above, char below, char left, char right, char corner) {
		long key = key(above, below, left, right);
		CORNERS.put(key, corner);
	}

	public static char intersection(char above, char below, char left, char right) {
		above = EQUIVALENTS.get(above) != null ? EQUIVALENTS.get(above) : above;
		below = EQUIVALENTS.get(below) != null ? EQUIVALENTS.get(below) : below;
		left = EQUIVALENTS.get(left) != null ? EQUIVALENTS.get(left) : left;
		right = EQUIVALENTS.get(right) != null ? EQUIVALENTS.get(right) : right;
		Character character = CORNERS.get(key(above, below, left, right));
		return character != null ? character : NONE;
	}

	private static long key(char above, char below, char left, char right) {
		return (long) above << 48 | (long) below << 32 | (long) left << 16 | (long) right;
	}

	BorderStyle(char vertical, char horizontal) {
		this.vertical = vertical;
		this.horizontal = horizontal;
	}

}
