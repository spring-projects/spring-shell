/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.component.view.control;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.geom.Position;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.screen.Screen;

/**
 * {@code InputView} is used as a text input.
 *
 * @author Janne Valkealahti
 */
public class InputView extends BoxView {

	private final ArrayList<String> text = new ArrayList<>();
	private int cursorIndex = 0;

	@Override
	protected void initInternal() {
		registerKeyBinding(Key.CursorLeft, event -> left());
		registerKeyBinding(Key.CursorRight, event -> right());
		registerKeyBinding(Key.Delete, () -> delete());
		registerKeyBinding(Key.Backspace, () -> backspace());
	}

	@Override
	public KeyHandler getKeyHandler() {
		KeyHandler handler = args -> {
			KeyEvent event = args.event();
			boolean consumed = false;
			if (event.isKey()) {
				consumed = true;
				int plainKey = event.getPlainKey();
				add(new String(new char[]{(char)plainKey}));
			}
			else if (event.isKey(KeyEvent.Key.Unicode)) {
				add(event.data());
			}
			return KeyHandler.resultOf(event, consumed, null);
		};
		return handler.thenIfNotConsumed(super.getKeyHandler());
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		String s = getInputText();
		screen.writerBuilder().build().text(s, rect.x(), rect.y());
		screen.setShowCursor(hasFocus());
		int cPos = cursorPosition();
		screen.setCursorPosition(new Position(rect.x() + cPos, rect.y()));
		super.drawInternal(screen);
	}

	/**
	 * Get a current known input text.
	 *
	 * @return current input text
	 */
	public String getInputText() {
		return text.stream().collect(Collectors.joining());
	}

	private int cursorPosition() {
		return text.stream().limit(cursorIndex).mapToInt(text -> text.length()).sum();
	}

	private void add(String data) {
		text.add(cursorIndex, data);
		moveCursor(1);
	}

	private void backspace() {
		if (cursorIndex > 0) {
			text.remove(cursorIndex - 1);
		}
		left();
	}

	private void delete() {
		if (cursorIndex < text.size()) {
			text.remove(cursorIndex);
		}
	}

	private void moveCursor(int index) {
		int toIndex = cursorIndex + index;
		if (toIndex > -1 && toIndex <= text.size()) {
			cursorIndex = toIndex;
		}
	}

	private void left() {
		moveCursor(-1);
	}

	private void right() {
		moveCursor(1);
	}
}
