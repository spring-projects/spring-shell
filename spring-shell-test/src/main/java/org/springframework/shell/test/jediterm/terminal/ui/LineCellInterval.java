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

/**
 * @author jediterm authors
 */
final class LineCellInterval {
	private final int myLine;
	private final int myStartColumn;
	private final int myEndColumn;

	public LineCellInterval(int line, int startColumn, int endColumn) {
		myLine = line;
		myStartColumn = startColumn;
		myEndColumn = endColumn;
	}

	public int getLine() {
		return myLine;
	}

	public int getStartColumn() {
		return myStartColumn;
	}

	public int getEndColumn() {
		return myEndColumn;
	}

	public int getCellCount() {
		return myEndColumn - myStartColumn + 1;
	}
}
