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

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

import org.springframework.shell.test.jediterm.terminal.model.StyleState;

/**
 * Executes terminal commands interpreted by {@link org.springframework.shell.test.jediterm.terminal.emulator.Emulator}, receives text
 *
 * @author jediterm authors
 */
public interface Terminal {
	void resize(int width, int height, RequestOrigin origin);

	void resize(int width, int height, RequestOrigin origin, CompletableFuture<?> promptUpdated);

	void beep();

	void backspace();

	void horizontalTab();

	void carriageReturn();

	void newLine();

	void mapCharsetToGL(int num);

	void mapCharsetToGR(int num);

	void designateCharacterSet(int tableNumber, char ch);

	void setAnsiConformanceLevel(int level);

	void writeDoubleByte(char[] bytes) throws UnsupportedEncodingException;

	void writeCharacters(String string);

	int distanceToLineEnd();

	void reverseIndex();

	void index();

	void nextLine();

	void fillScreen(char c);

	void saveCursor();

	void restoreCursor();

	void reset();

	void characterAttributes(TextStyle textStyle);

	void setScrollingRegion(int top, int bottom);

	void scrollUp(int count);

	void scrollDown(int count);

	void resetScrollRegions();

	void cursorHorizontalAbsolute(int x);

	void linePositionAbsolute(int y);

	void cursorPosition(int x, int y);

	void cursorUp(int countY);

	void cursorDown(int dY);

	void cursorForward(int dX);

	void cursorBackward(int dX);

	void cursorShape(CursorShape shape);

	void eraseInLine(int arg);

	void deleteCharacters(int count);

	int getTerminalWidth();

	int getTerminalHeight();

	void eraseInDisplay(int arg);

	void setModeEnabled(TerminalMode mode, boolean enabled);

	void disconnected();

	int getCursorX();

	int getCursorY();

	void singleShiftSelect(int num);

	void setWindowTitle(String name);

	void saveWindowTitleOnStack();

	void restoreWindowTitleFromStack();

	void clearScreen();

	void setCursorVisible(boolean visible);

	void useAlternateBuffer(boolean enabled);

	// byte[] getCodeForKey(int key, int modifiers);

	void setApplicationArrowKeys(boolean enabled);

	void setApplicationKeypad(boolean enabled);

	void setAutoNewLine(boolean enabled);

	StyleState getStyleState();

	void insertLines(int count);

	void deleteLines(int count);

	void setBlinkingCursor(boolean enabled);

	void eraseCharacters(int count);

	void insertBlankCharacters(int count);

	void clearTabStopAtCursor();

	void clearAllTabStops();

	void setTabStopAtCursor();

	void writeUnwrappedString(String string);

	void setTerminalOutput(TerminalOutputStream terminalOutput);

	void setAltSendsEscape(boolean enabled);

	void deviceStatusReport(String str);

	void deviceAttributes(byte[] response);

	void setBracketedPasteMode(boolean enabled);

	// TerminalColor getWindowForeground();

	// TerminalColor getWindowBackground();
}
