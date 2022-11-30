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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.test.jediterm.terminal.StyledTextConsumer;
import org.springframework.shell.test.jediterm.terminal.StyledTextConsumerAdapter;
import org.springframework.shell.test.jediterm.terminal.TextStyle;
import org.springframework.shell.test.jediterm.terminal.model.TerminalLine.TextEntry;
import org.springframework.shell.test.jediterm.terminal.util.CharUtils;
import org.springframework.shell.test.jediterm.terminal.util.Pair;

/**
 * Buffer for storing styled text data.
 * Stores only text that fit into one screen XxY, but has scrollBuffer to save history lines and screenBuffer to restore
 * screen after resize. ScrollBuffer stores all lines before the first line currently shown on the screen. TextBuffer
 * stores lines that are shown currently on the screen and they have there(in TextBuffer) their initial length (even if
 * it doesn't fit to screen width).
 * <p/>
 *
 * @author jediterm authors
 */
public class TerminalTextBuffer {

	private static final Logger LOG = LoggerFactory.getLogger(TerminalTextBuffer.class);

	private final StyleState myStyleState;

	private LinesBuffer myHistoryBuffer;

	private LinesBuffer myScreenBuffer;

	private int myWidth;

	private int myHeight;

	private final int myHistoryLinesCount;

	private final Lock myLock = new ReentrantLock();

	private LinesBuffer myHistoryBufferBackup;

	private LinesBuffer myScreenBufferBackup; // to store textBuffer after switching to alternate buffer

	// private boolean myAlternateBuffer = false;

	private boolean myUsingAlternateBuffer = false;

	private final List<TerminalModelListener> myListeners = new CopyOnWriteArrayList<>();

	private final List<TerminalModelListener> myTypeAheadListeners = new CopyOnWriteArrayList<>();

	public TerminalTextBuffer(final int width, final int height,  StyleState styleState) {
		this(width, height, styleState, LinesBuffer.DEFAULT_MAX_LINES_COUNT);
	}

	public TerminalTextBuffer(final int width, final int height, StyleState styleState, final int historyLinesCount) {
		myStyleState = styleState;
		myWidth = width;
		myHeight = height;
		myHistoryLinesCount = historyLinesCount;

		myScreenBuffer = createScreenBuffer();
		myHistoryBuffer = createHistoryBuffer();
	}


	private LinesBuffer createScreenBuffer() {
		return new LinesBuffer(-1);
	}


	private LinesBuffer createHistoryBuffer() {
		return new LinesBuffer(myHistoryLinesCount);
	}

	// public Dimension resize(final Dimension pendingResize, final RequestOrigin origin, final int cursorX,
	// 		final int cursorY, JediTerminal.ResizeHandler resizeHandler) {
	// 	lock();
	// 	try {
	// 		return doResize(pendingResize, origin, cursorX, cursorY, resizeHandler);
	// 	} finally {
	// 		unlock();
	// 	}
	// }

	// private Dimension doResize(final Dimension pendingResize, final RequestOrigin origin, final int cursorX,
	// 		final int cursorY, JediTerminal.ResizeHandler resizeHandler) {
	// 	final int newWidth = pendingResize.width;
	// 	final int newHeight = pendingResize.height;
	// 	int newCursorX = cursorX;
	// 	int newCursorY = cursorY;

	// 	if (myWidth != newWidth) {
	// 		myWidth = newWidth;
	// 		myHeight = newHeight;
	// 	}

	// 	final int oldHeight = myHeight;
	// 	if (newHeight < oldHeight) {
	// 		int count = oldHeight - newHeight;
	// 		if (!myAlternateBuffer) {
	// 			//we need to move lines from text buffer to the scroll buffer
	// 			//but empty bottom lines can be collapsed
	// 			int emptyLinesDeleted = myScreenBuffer.removeBottomEmptyLines(oldHeight - 1, count);
	// 			myScreenBuffer.moveTopLinesTo(count - emptyLinesDeleted, myHistoryBuffer);
	// 			newCursorY = cursorY - (count - emptyLinesDeleted);
	// 		} else {
	// 			newCursorY = cursorY;
	// 		}
	// 	} else if (newHeight > oldHeight) {
	// 		if (!myAlternateBuffer) {
	// 			//we need to move lines from scroll buffer to the text buffer
	// 			int historyLinesCount = Math.min(newHeight - oldHeight, myHistoryBuffer.getLineCount());
	// 			myHistoryBuffer.moveBottomLinesTo(historyLinesCount, myScreenBuffer);
	// 			newCursorY = cursorY + historyLinesCount;
	// 		} else {
	// 			newCursorY = cursorY;
	// 		}
	// 	}

	// 	myWidth = newWidth;
	// 	myHeight = newHeight;


	// 	resizeHandler.sizeUpdated(myWidth, myHeight, newCursorX, newCursorY);


	// 	fireModelChangeEvent();

	// 	return pendingResize;
	// }

	public void addModelListener(TerminalModelListener listener) {
		myListeners.add(listener);
	}

	public void addTypeAheadModelListener(TerminalModelListener listener) {
		myTypeAheadListeners.add(listener);
	}

	public void removeModelListener(TerminalModelListener listener) {
		myListeners.remove(listener);
	}

	public void removeTypeAheadModelListener(TerminalModelListener listener) {
		myTypeAheadListeners.remove(listener);
	}

	void fireModelChangeEvent() {
		for (TerminalModelListener modelListener : myListeners) {
			modelListener.modelChanged();
		}
	}

	void fireTypeAheadModelChangeEvent() {
		for (TerminalModelListener modelListener : myTypeAheadListeners) {
			modelListener.modelChanged();
		}
	}

	private TextStyle createEmptyStyleWithCurrentColor() {
		return myStyleState.getCurrent().createEmptyWithColors();
	}

	private TextEntry createFillerEntry() {
		return new TextEntry(createEmptyStyleWithCurrentColor(), new CharBuffer(CharUtils.NUL_CHAR, myWidth));
	}

	public void deleteCharacters(final int x, final int y, final int count) {
		if (y > myHeight - 1 || y < 0) {
			LOG.error("attempt to delete in line " + y + "\n" +
							"args were x:" + x + " count:" + count);
		} else if (count < 0) {
			LOG.error("Attempt to delete negative chars number: count:" + count);
		} else if (count > 0) {
			myScreenBuffer.deleteCharacters(x, y, count, createEmptyStyleWithCurrentColor());

			fireModelChangeEvent();
		}
	}

	public void insertBlankCharacters(final int x, final int y, final int count) {
		if (y > myHeight - 1 || y < 0) {
			LOG.error("attempt to insert blank chars in line " + y + "\n" +
							"args were x:" + x + " count:" + count);
		} else if (count < 0) {
			LOG.error("Attempt to insert negative blank chars number: count:" + count);
		} else if (count > 0) { //nothing to do
			myScreenBuffer.insertBlankCharacters(x, y, count, myWidth, createEmptyStyleWithCurrentColor());

			fireModelChangeEvent();
		}
	}

	public void writeString(final int x, final int y,  final CharBuffer str) {
		writeString(x, y, str, myStyleState.getCurrent());
	}

	public void addLine( final TerminalLine line) {
		myScreenBuffer.addLines(List.of(line));

		fireModelChangeEvent();
	}

	private void writeString(int x, int y,  CharBuffer str,  TextStyle style) {
		myScreenBuffer.writeString(x, y - 1, str, style);

		fireModelChangeEvent();
	}

	public void scrollArea(final int scrollRegionTop, final int dy, int scrollRegionBottom) {
		if (dy == 0) {
			return;
		}
		if (dy > 0) {
			insertLines(scrollRegionTop - 1, dy, scrollRegionBottom);
		} else {
			LinesBuffer removed = deleteLines(scrollRegionTop - 1, -dy, scrollRegionBottom);
			if (scrollRegionTop == 1) {
				removed.moveTopLinesTo(removed.getLineCount(), myHistoryBuffer);
			}

			fireModelChangeEvent();
		}
	}

	public String getStyleLines() {
		final Map<Integer, Integer> hashMap = new HashMap<>();
		myLock.lock();
		try {
			final StringBuilder sb = new StringBuilder();
			myScreenBuffer.processLines(0, myHeight, new StyledTextConsumerAdapter() {
				int count = 0;

				@Override
				public void consume(int x, int y,  TextStyle style,  CharBuffer characters, int startRow) {
					if (x == 0) {
						sb.append("\n");
					}
					int styleNum = style.getId();
					if (!hashMap.containsKey(styleNum)) {
						hashMap.put(styleNum, count++);
					}
					sb.append(String.format("%02d ", hashMap.get(styleNum)));
				}
			});
			return sb.toString();
		} finally {
			myLock.unlock();
		}
	}

	/**
	 * Returns terminal lines. Negative indexes are for history buffer. Non-negative for screen buffer.
	 *
	 * @param index index of line
	 * @return history lines for {@literal index<0}, screen line for index>=0
	 */
	public TerminalLine getLine(int index) {
		if (index >= 0) {
			if (index >= getHeight()) {
				LOG.error("Attempt to get line out of bounds: " + index + " >= " + getHeight());
				return TerminalLine.createEmpty();
			}
			return myScreenBuffer.getLine(index);
		} else {
			if (index < -getHistoryLinesCount()) {
				LOG.error("Attempt to get line out of bounds: " + index + " < " + -getHistoryLinesCount());
				return TerminalLine.createEmpty();
			}
			return myHistoryBuffer.getLine(getHistoryLinesCount() + index);
		}
	}

	public List<String> getScreen() {
		myLock.lock();
		List<String> lines = new ArrayList<>();
		try {
			for (int row = 0; row < myHeight; row++) {
				StringBuilder line = new StringBuilder(myScreenBuffer.getLine(row).getText());

				for (int i = line.length(); i < myWidth; i++) {
					line.append(' ');
				}
				if (line.length() > myWidth) {
					line.setLength(myWidth);
				}
				lines.add(line.toString());
			}
			return lines;
		} finally {
			myLock.unlock();
		}
	}

	public void processScreenLines(final int yStart, final int yCount, final StyledTextConsumer consumer) {
		myScreenBuffer.processLines(yStart, yCount, consumer);
	}

	public void lock() {
		myLock.lock();
	}

	public void unlock() {
		myLock.unlock();
	}

	public boolean tryLock() {
		return myLock.tryLock();
	}

	public int getWidth() {
		return myWidth;
	}

	public int getHeight() {
		return myHeight;
	}

	public int getHistoryLinesCount() {
		return myHistoryBuffer.getLineCount();
	}

	public int getScreenLinesCount() {
		return myScreenBuffer.getLineCount();
	}

	public char getBuffersCharAt(int x, int y) {
		return getLine(y).charAt(x);
	}

	public TextStyle getStyleAt(int x, int y) {
		return getLine(y).getStyleAt(x);
	}

	public Pair<Character, TextStyle> getStyledCharAt(int x, int y) {
		synchronized (myScreenBuffer) {
			TerminalLine line = getLine(y);
			return new Pair<Character, TextStyle>(line.charAt(x), line.getStyleAt(x));
		}
	}

	public char getCharAt(int x, int y) {
		synchronized (myScreenBuffer) {
			TerminalLine line = getLine(y);
			return line.charAt(x);
		}
	}

	public boolean isUsingAlternateBuffer() {
		return myUsingAlternateBuffer;
	}

	public void useAlternateBuffer(boolean enabled) {
		// myAlternateBuffer = enabled;
		if (enabled) {
			if (!myUsingAlternateBuffer) {
				myScreenBufferBackup = myScreenBuffer;
				myHistoryBufferBackup = myHistoryBuffer;
				myScreenBuffer = createScreenBuffer();
				myHistoryBuffer = createHistoryBuffer();
				myUsingAlternateBuffer = true;
			}
		} else {
			if (myUsingAlternateBuffer) {
				myScreenBuffer = myScreenBufferBackup;
				myHistoryBuffer = myHistoryBufferBackup;
				myScreenBufferBackup = createScreenBuffer();
				myHistoryBufferBackup = createHistoryBuffer();
				myUsingAlternateBuffer = false;
			}
		}
		fireModelChangeEvent();
	}

	public LinesBuffer getHistoryBuffer() {
		return myHistoryBuffer;
	}

	public void insertLines(int y, int count, int scrollRegionBottom) {
		myScreenBuffer.insertLines(y, count, scrollRegionBottom - 1, createFillerEntry());

		fireModelChangeEvent();
	}

	// returns deleted lines
	public LinesBuffer deleteLines(int y, int count, int scrollRegionBottom) {
		LinesBuffer linesBuffer = myScreenBuffer.deleteLines(y, count, scrollRegionBottom - 1, createFillerEntry());
		fireModelChangeEvent();
		return linesBuffer;
	}

	public void clearLines(int startRow, int endRow) {
		myScreenBuffer.clearLines(startRow, endRow, createFillerEntry());
		fireModelChangeEvent();
	}

	public void eraseCharacters(int leftX, int rightX, int y) {
		TextStyle style = createEmptyStyleWithCurrentColor();
		if (y >= 0) {
			myScreenBuffer.clearArea(leftX, y, rightX, y + 1, style);
			fireModelChangeEvent();
		} else {
			LOG.error("Attempt to erase characters in line: " + y);
		}
	}

	public void clearAll() {
		myScreenBuffer.clearAll();
		fireModelChangeEvent();
	}

	/**
	 * @param scrollOrigin row where a scrolling window starts, should be in the range [-history_lines_count, 0]
	 */
	public void processHistoryAndScreenLines(int scrollOrigin, int maximalLinesToProcess, StyledTextConsumer consumer) {
		if (maximalLinesToProcess<0) {
			//Process all lines in this case

			maximalLinesToProcess = myHistoryBuffer.getLineCount() + myScreenBuffer.getLineCount();
		}

		int linesFromHistory = Math.min(-scrollOrigin, maximalLinesToProcess);

		int y = myHistoryBuffer.getLineCount() + scrollOrigin;
		if (y < 0) { // it seems that lower bound of scrolling can get out of sync with history buffer lines count
			y = 0; // to avoid exception we start with the first line in this case
		}
		myHistoryBuffer.processLines(y, linesFromHistory, consumer, y);

		if (linesFromHistory < maximalLinesToProcess) {
			// we can show lines from screen buffer
			myScreenBuffer.processLines(0, maximalLinesToProcess - linesFromHistory, consumer, -linesFromHistory);
		}
	}

	public void clearHistory() {
		myHistoryBuffer.clearAll();
		fireModelChangeEvent();
	}

	void moveScreenLinesToHistory() {
		myLock.lock();
		try {
			myScreenBuffer.removeBottomEmptyLines(myScreenBuffer.getLineCount() - 1, myScreenBuffer.getLineCount());
			myScreenBuffer.moveTopLinesTo(myScreenBuffer.getLineCount(), myHistoryBuffer);
			if (myHistoryBuffer.getLineCount() > 0) {
				myHistoryBuffer.getLine(myHistoryBuffer.getLineCount() - 1).setWrapped(false);
			}
		}
		finally {
			myLock.unlock();
		}
	}


	LinesBuffer getHistoryBufferOrBackup() {
		return myUsingAlternateBuffer ? myHistoryBufferBackup : myHistoryBuffer;
	}


	LinesBuffer getScreenBufferOrBackup() {
		return myUsingAlternateBuffer ? myScreenBufferBackup : myScreenBuffer;
	}

	public int findScreenLineIndex( TerminalLine line) {
		return myScreenBuffer.findLineIndex(line);
	}

	public void clearTypeAheadPredictions() {
		myScreenBuffer.clearTypeAheadPredictions();
		myHistoryBuffer.clearTypeAheadPredictions();
		fireModelChangeEvent();
	}
}
