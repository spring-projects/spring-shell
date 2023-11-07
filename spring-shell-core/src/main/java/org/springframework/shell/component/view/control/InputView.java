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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.geom.Position;
import org.springframework.shell.geom.Rectangle;

/**
 * {@code InputView} is used as a text input.
 *
 * @author Janne Valkealahti
 */
public class InputView extends BoxView {

	private final Logger log = LoggerFactory.getLogger(InputView.class);
	private final ArrayList<String> text = new ArrayList<>();
	private int cursorIndex = 0;

	@Override
	protected void initInternal() {
		registerViewCommand(ViewCommand.ACCEPT, () -> done());
		registerViewCommand(ViewCommand.LEFT, () -> left());
		registerViewCommand(ViewCommand.RIGHT, () -> right());
		registerViewCommand(ViewCommand.DELETE_CHAR_LEFT, () -> deleteCharLeft());
		registerViewCommand(ViewCommand.DELETE_CHAR_RIGHT, () -> deleteCharRight());

		registerKeyBinding(Key.Enter, ViewCommand.ACCEPT);
		registerKeyBinding(Key.CursorLeft, ViewCommand.LEFT);
		registerKeyBinding(Key.CursorRight, ViewCommand.RIGHT);
		registerKeyBinding(Key.Backspace, ViewCommand.DELETE_CHAR_LEFT);
		registerKeyBinding(Key.Delete, ViewCommand.DELETE_CHAR_RIGHT);
	}

	@Override
	public KeyHandler getKeyHandler() {
		log.trace("getKeyHandler()");
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
		if (hasFocus()) {
			screen.setShowCursor(true);
			int cPos = cursorPosition();
			screen.setCursorPosition(new Position(rect.x() + cPos, rect.y()));
		}
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

	private void dispatchTextChange(String oldText, String newText) {
		dispatch(ShellMessageBuilder.ofView(this, InputViewTextChangeEvent.of(this, oldText, newText)));
	}

	private void add(String data) {
		String oldText = text.stream().collect(Collectors.joining());
		text.add(cursorIndex, data);
		moveCursor(1);
		String newText = text.stream().collect(Collectors.joining());
		dispatchTextChange(oldText, newText);
	}

	private void deleteCharLeft() {
		if (cursorIndex > 0) {
			String oldText = text.stream().collect(Collectors.joining());
			text.remove(cursorIndex - 1);
			String newText = text.stream().collect(Collectors.joining());
			dispatchTextChange(oldText, newText);
		}
		left();
	}

	private void deleteCharRight() {
		if (cursorIndex < text.size()) {
			String oldText = text.stream().collect(Collectors.joining());
			text.remove(cursorIndex);
			String newText = text.stream().collect(Collectors.joining());
			dispatchTextChange(oldText, newText);
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

	private void done() {
		dispatch(ShellMessageBuilder.ofView(this, ViewDoneEvent.of(this)));
	}

	public record InputViewTextChangeEventArgs(String oldText, String newText) implements ViewEventArgs {

		public static InputViewTextChangeEventArgs of(String oldText, String newText) {
			return new InputViewTextChangeEventArgs(oldText, newText);
		}
	}

	public record InputViewTextChangeEvent(View view, InputViewTextChangeEventArgs args) implements ViewEvent {

		public static InputViewTextChangeEvent of(View view, String oldText, String newText) {
			return new InputViewTextChangeEvent(view, InputViewTextChangeEventArgs.of(oldText, newText));
		}
	}

}
