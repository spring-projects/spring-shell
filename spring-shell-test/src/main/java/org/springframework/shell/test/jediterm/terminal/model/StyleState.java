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
package org.springframework.shell.test.jediterm.terminal.model;

import org.springframework.shell.test.jediterm.terminal.TextStyle;

/**
 * @author jediterm authors
 */
public class StyleState {
	private TextStyle myCurrentStyle = TextStyle.EMPTY;
	private TextStyle myDefaultStyle = TextStyle.EMPTY;

	private TextStyle myMergedStyle = null;

	public StyleState() {
	}

	public TextStyle getCurrent() {
		return TextStyle.getCanonicalStyle(getMergedStyle());
	}


	private static TextStyle merge( TextStyle style,  TextStyle defaultStyle) {
		TextStyle.Builder builder = style.toBuilder();
		// if (style.getBackground() == null && defaultStyle.getBackground() != null) {
		//   builder.setBackground(defaultStyle.getBackground());
		// }
		// if (style.getForeground() == null && defaultStyle.getForeground() != null) {
		//   builder.setForeground(defaultStyle.getForeground());
		// }
		return builder.build();
	}

	public void reset() {
		myCurrentStyle = myDefaultStyle;
		myMergedStyle = null;
	}

	public void set(StyleState styleState) {
		setCurrent(styleState.getCurrent());
	}

	public void setDefaultStyle(TextStyle defaultStyle) {
		myDefaultStyle = defaultStyle;
		myMergedStyle = null;
	}

	// public TerminalColor getBackground() {
	//   return getBackground(null);
	// }

	// public TerminalColor getBackground(TerminalColor color) {
	//   return color != null ? color : myDefaultStyle.getBackground();
	// }

	// public TerminalColor getForeground() {
	//   return getForeground(null);
	// }

	// public TerminalColor getForeground(TerminalColor color) {
	//   return color != null ? color : myDefaultStyle.getForeground();
	// }

	public void setCurrent(TextStyle current) {
		myCurrentStyle = current;
		myMergedStyle = null;
	}

	private TextStyle getMergedStyle() {
		if (myMergedStyle == null) {
			myMergedStyle = merge(myCurrentStyle, myDefaultStyle);
		}
		return myMergedStyle;
	}
}
