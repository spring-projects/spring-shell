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
package org.springframework.shell.test.jediterm.terminal;

/**
 * @author jediterm authors
 */
public interface TerminalDisplay {

	int getRowCount();

	int getColumnCount();

	void beep();

	void scrollArea(final int scrollRegionTop, final int scrollRegionSize, int dy);

	String getWindowTitle();

	void setWindowTitle(String name);

	boolean ambiguousCharsAreDoubleWidth();

	default void setBracketedPasteMode(boolean enabled) {
	}

	// default TerminalColor getWindowForeground() {
	// return null;
	// }

	// default TerminalColor getWindowBackground() {
	// return null;
	// }

}
