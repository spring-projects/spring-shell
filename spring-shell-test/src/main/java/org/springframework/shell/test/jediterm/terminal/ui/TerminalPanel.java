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
package org.springframework.shell.test.jediterm.terminal.ui;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.shell.test.jediterm.terminal.TerminalDisplay;
import org.springframework.shell.test.jediterm.terminal.TerminalOutputStream;
import org.springframework.shell.test.jediterm.terminal.TerminalStarter;
import org.springframework.shell.test.jediterm.terminal.model.LinesBuffer;
import org.springframework.shell.test.jediterm.terminal.model.StyleState;
import org.springframework.shell.test.jediterm.terminal.model.TerminalLine;
import org.springframework.shell.test.jediterm.terminal.model.TerminalTextBuffer;

/**
 * @author jediterm authors
 */
public class TerminalPanel implements TerminalDisplay {

	public static final double SCROLL_SPEED = 0.05;

	protected int myCharSizeWidth = 0;
	protected int myCharSizeHeight = 0;
	protected int myTermSizeWidth = 80;
	protected int myTermSizeHeight = 24;
	private TerminalStarter myTerminalStarter = null;
	// private TerminalSelection mySelection = null;
	private TerminalPanelListener myTerminalPanelListener;
	private final TerminalTextBuffer myTerminalTextBuffer;
	protected int myClientScrollOrigin;
	private String myWindowTitle = "Terminal";
	private AtomicInteger scrollDy = new AtomicInteger(0);
	private TerminalCoordinates myCoordsAccessor;

	public TerminalPanel(TerminalTextBuffer terminalTextBuffer, StyleState styleState) {
		myTerminalTextBuffer = terminalTextBuffer;
		myTermSizeWidth = terminalTextBuffer.getWidth();
		myTermSizeHeight = terminalTextBuffer.getHeight();
	}

	public TerminalPanelListener getTerminalPanelListener() {
		return myTerminalPanelListener;
	}

	public void setCoordAccessor(TerminalCoordinates coordAccessor) {
		myCoordsAccessor = coordAccessor;
	}

	public void setTerminalStarter(final TerminalStarter terminalStarter) {
		myTerminalStarter = terminalStarter;
	}

	public void setTerminalPanelListener(final TerminalPanelListener terminalPanelListener) {
		myTerminalPanelListener = terminalPanelListener;
	}

	public int getPixelWidth() {
		return myCharSizeWidth * myTermSizeWidth + getInsetX();
	}

	public int getPixelHeight() {
		return myCharSizeHeight * myTermSizeHeight;
	}

	public int getColumnCount() {
		return myTermSizeWidth;
	}

	public int getRowCount() {
		return myTermSizeHeight;
	}

	public String getWindowTitle() {
		return myWindowTitle;
	}

	protected int getInsetX() {
		return 4;
	}

	public enum TerminalCursorState {
		SHOWING, HIDDEN, NO_FOCUS;
	}


	// Called in a background thread with myTerminalTextBuffer.lock() acquired
	public void scrollArea(final int scrollRegionTop, final int scrollRegionSize, int dy) {
		scrollDy.addAndGet(dy);
		// mySelection = null;
	}

	public void beep() {
		// if (mySettingsProvider.audibleBell()) {
		// 	Toolkit.getDefaultToolkit().beep();
		// }
	}

	public TerminalTextBuffer getTerminalTextBuffer() {
		return myTerminalTextBuffer;
	}

	// public TerminalSelection getSelection() {
	// 	return mySelection;
	// }

	@Override
	public boolean ambiguousCharsAreDoubleWidth() {
		return false;
	}

	@Override
	public void setBracketedPasteMode(boolean enabled) {
		// myBracketedPasteMode = enabled;
	}

	public LinesBuffer getScrollBuffer() {
		return myTerminalTextBuffer.getHistoryBuffer();
	}

	public TerminalOutputStream getTerminalOutputStream() {
		return myTerminalStarter;
	}

	@Override
	public void setWindowTitle(String name) {
		myWindowTitle = name;
		if (myTerminalPanelListener != null) {
			myTerminalPanelListener.onTitleChanged(myWindowTitle);
		}
	}

	public void clearBuffer() {
		clearBuffer(true);
	}

	/**
	 * @param keepLastLine true to keep last line (e.g. to keep terminal prompt)
	 *                     false to clear entire terminal panel (relevant for terminal console)
	 */
	protected void clearBuffer(boolean keepLastLine) {
		if (!myTerminalTextBuffer.isUsingAlternateBuffer()) {
			myTerminalTextBuffer.clearHistory();

			if (myCoordsAccessor != null) {
				if (keepLastLine) {
					if (myCoordsAccessor.getY() > 0) {
						TerminalLine lastLine = myTerminalTextBuffer.getLine(myCoordsAccessor.getY() - 1);
						myTerminalTextBuffer.clearAll();
						myCoordsAccessor.setY(0);
						// myCursor.setY(1);
						myTerminalTextBuffer.addLine(lastLine);
					}
				}
				else {
					myTerminalTextBuffer.clearAll();
					myCoordsAccessor.setX(0);
					myCoordsAccessor.setY(1);
					// myCursor.setX(0);
					// myCursor.setY(1);
				}
			}

			// myBoundedRangeModel.setValue(0);
			// updateScrolling(true);

			// myClientScrollOrigin = myBoundedRangeModel.getValue();
		}
	}

	public void dispose() {
	}
}
