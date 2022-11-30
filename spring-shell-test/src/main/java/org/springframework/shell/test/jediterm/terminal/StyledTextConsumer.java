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

import org.springframework.shell.test.jediterm.terminal.model.CharBuffer;

/**
 * General interface that obtains styled range of characters at coordinates (<b>x</b>, <b>y</b>) when the screen starts at <b>startRow</b>
 *
 * @author jediterm authors
 */
public interface StyledTextConsumer {
	/**
	 *
	 * @param x indicates starting column of the characters
	 * @param y indicates row of the characters
	 * @param style style of characters
	 * @param characters text characters
	 * @param startRow number of the first row.
	 *                 It can be different for different buffers, e.g. backBuffer starts from 0, textBuffer and scrollBuffer from -count
	 */
	void consume(int x, int y, TextStyle style, CharBuffer characters, int startRow);

	void consumeNul(int x, int y, int nulIndex, TextStyle style, CharBuffer characters, int startRow);

	void consumeQueue(int x, int y, int nulIndex, int startRow);

}
