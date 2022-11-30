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
public abstract class TerminalLineIntervalHighlighting {
	private final TerminalLine myLine;
	private final int myStartOffset;
	private final int myEndOffset;
	private boolean myDisposed = false;

	TerminalLineIntervalHighlighting( TerminalLine line, int startOffset, int length) {
		if (startOffset < 0) {
			throw new IllegalArgumentException("Negative startOffset: " + startOffset);
		}
		if (length < 0) {
			throw new IllegalArgumentException("Negative length: " + length);
		}
		myLine = line;
		myStartOffset = startOffset;
		myEndOffset = startOffset + length;
	}

	public  TerminalLine getLine() {
		return myLine;
	}

	public int getStartOffset() {
		return myStartOffset;
	}

	public int getEndOffset() {
		return myEndOffset;
	}

	public int getLength() {
		return myEndOffset - myStartOffset;
	}

	public boolean isDisposed() {
		return myDisposed;
	}

	public final void dispose() {
		doDispose();
		myDisposed = true;
	}

	protected abstract void doDispose();

	public boolean intersectsWith(int otherStartOffset, int otherEndOffset) {
		return !(myEndOffset <= otherStartOffset || otherEndOffset <= myStartOffset);
	}

	public  TextStyle mergeWith( TextStyle style) {
		// TerminalColor foreground = myStyle.getForeground();
		// if (foreground == null) {
		//   foreground = style.getForeground();
		// }
		// TerminalColor background = myStyle.getBackground();
		// if (background == null) {
		//   background = style.getBackground();
		// }
		return new TextStyle();
	}

	@Override
	public String toString() {
		return "startOffset=" + myStartOffset +
			", endOffset=" + myEndOffset +
			", disposed=" + myDisposed;
	}
}
