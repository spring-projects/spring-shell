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

/**
 * Base class defining a settings for figures.
 *
 * @author Janne Valkealahti
 */
public abstract class FigureSettings {

	/**
	 * Figure meant to be used as ok like a checkmark.
	 */
	public final static String TAG_TICK = "tick";

	/**
	 * Figure indicating something about on a level of info.
	 */
	public final static String TAG_INFO = "info";

	/**
	 * Figure indicating something about on a level of warning.
	 */
	public final static String TAG_WARNING = "warning";

	/**
	 * Figure indicating something about on a level of error.
	 */
	public final static String TAG_ERROR = "error";

	/**
	 * Figure used as a checkbox for off position.
	 */
	public final static String TAG_CHECKBOX_OFF = "checkboxOff";

	/**
	 * Figure used as a checkbox for on position.
	 */
	public final static String TAG_CHECKBOX_ON = "checkboxOn";

	/**
	 * Figure for left arrow.
	 */
	public final static String TAG_LEFTWARDS_ARROR = "leftwardsArrow";

	/**
	 * Figure for up arrow.
	 */
	public final static String TAG_UPWARDS_ARROR = "upwardsArrow";

	/**
	 * Figure for right arrow.
	 */
	public final static String TAG_RIGHTWARDS_ARROR = "righwardsArror";

	/**
	 * Figure for down arrow.
	 */
	public final static String TAG_DOWNWARDS_ARROR = "downwardsArror";

	/**
	 * Figure used as indicator pointing left.
	 */
	public final static String TAG_LEFT_POINTING_QUOTATION = "leftPointingQuotation";

	/**
	 * Figure used as indicator pointing left.
	 */
	public final static String TAG_RIGHT_POINTING_QUOTATION = "rightPointingQuotation";

	/**
	 * Figure meant for question.
	 */
	public final static String TAG_QUESTION_MARK = "questionMark";

	public String tick() {
		return "✔"; // U+2714 Heavy Check Mark Emoji, tick, checkmark
	}

	public String info() {
		return "ℹ"; // U+2139 Information Source Emoji
	}

	public String warning() {
		return "⚠"; // U+26A0 Warning Sign Emoji, danger, аlert
	}

	public String error() {
		return "✖"; // U+2716 Heavy Multiplication X Emoji, cross
	}

	public String checkboxOff() {
		return "☐"; // U+2610 Ballot Box
	}

	public String checkboxOn() {
		return "☒"; // U+2612 Ballot Box with X
	}

	public String leftwardsArrow() {
		return "←"; // U+2190 Leftwards Arrow
	}

	public String upwardsArrow() {
		return "↑"; // U+2191 Upwards Arrow
	}

	public String righwardsArror() {
		return "→"; // U+2192 Rightwards Arrow
	}

	public String downwardsArror() {
		return "↓"; // U+2193 Downwards Arrow
	}

	public String leftPointingQuotation() {
		return "❮"; // U+276E Heavy Left-Pointing Angle Quotation Mark Ornament
	}

	public String rightPointingQuotation() {
		return "❯"; // U+276F Heavy Right-Pointing Angle Quotation Mark Ornament
	}

	public String questionMark() {
		return "?"; // U+003F Question Mark
	}

	public String resolveTag(String tag) {
		switch (tag) {
			case TAG_TICK:
				return tick();
			case TAG_INFO:
				return info();
			case TAG_WARNING:
				return warning();
			case TAG_ERROR:
				return error();
			case TAG_CHECKBOX_OFF:
				return checkboxOff();
			case TAG_CHECKBOX_ON:
				return checkboxOn();
			case TAG_LEFTWARDS_ARROR:
				return leftwardsArrow();
			case TAG_UPWARDS_ARROR:
				return upwardsArrow();
			case TAG_RIGHTWARDS_ARROR:
				return righwardsArror();
			case TAG_DOWNWARDS_ARROR:
				return downwardsArror();
			case TAG_LEFT_POINTING_QUOTATION:
				return leftPointingQuotation();
			case TAG_RIGHT_POINTING_QUOTATION:
				return rightPointingQuotation();
			case TAG_QUESTION_MARK:
				return questionMark();
		}
		throw new IllegalArgumentException(String.format("Unknown tag '%s'", tag));
	}

	public static FigureSettings defaults() {
		return new DefaultFigureSettings();
	}

	public static FigureSettings dump() {
		return new DumpFigureSettings();
	}

	public static String[] tags() {
		return new String[] {
				TAG_TICK,
				TAG_INFO,
				TAG_WARNING,
				TAG_ERROR,
				TAG_CHECKBOX_OFF,
				TAG_CHECKBOX_ON,
				TAG_LEFTWARDS_ARROR,
				TAG_UPWARDS_ARROR,
				TAG_RIGHTWARDS_ARROR,
				TAG_DOWNWARDS_ARROR,
				TAG_LEFT_POINTING_QUOTATION,
				TAG_RIGHT_POINTING_QUOTATION,
				TAG_QUESTION_MARK
		};
	}

	private static class DefaultFigureSettings extends FigureSettings {
	}

	private static class DumpFigureSettings extends FigureSettings {

		@Override
		public String tick() {
			return "v";
		}

		@Override
		public String info() {
			return "i";
		}

		@Override
		public String warning() {
			return "!";
		}

		@Override
		public String error() {
			return "x";
		}

		public String checkboxOff() {
			return "[ ]";
		}

		public String checkboxOn() {
			return "[x]";
		}

		public String leftwardsArrow() {
			return "<";
		}

		public String upwardsArrow() {
			return "^";
		}

		public String righwardsArror() {
			return ">";
		}

		public String downwardsArror() {
			return "v";
		}

		public String leftPointingQuotation() {
			return "<";
		}

		public String rightPointingQuotation() {
			return ">";
		}
	}
}
