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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.message.ShellMessageBuilder;
import org.springframework.shell.component.view.screen.Screen;

/**
 * {@link AppView} provides an opinionated terminal UI application view
 * controlling main viewing area, menubar, statusbar and modal window system.
 *
 * @author Janne Valkealahti
 */
public class AppView extends BoxView {

	private final static Logger log = LoggerFactory.getLogger(AppView.class);
	private View main;
	private View modal;

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		if (main != null) {
			main.setRect(rect.x(), rect.y(), rect.width(), rect.height());
			main.draw(screen);
		}
		if (modal != null) {
			modal.setLayer(1);
			modal.setRect(rect.x() + 5, rect.y() + 5, rect.width() - 10, rect.height() - 10);
			modal.draw(screen);
		}
		super.drawInternal(screen);
	}

	@Override
	public MouseHandler getMouseHandler() {
		log.trace("getMouseHandler()");
		if (main != null) {
			MouseHandler handler = main.getMouseHandler();
			if (handler != null) {
				return handler;
			}
		}
		return super.getMouseHandler();
	}

	@Override
	public KeyHandler getKeyHandler() {
		KeyHandler handler1 = args -> {
			KeyEvent event = args.event();
			boolean consumed = false;
			if (event.isKey(Key.CursorLeft)) {
				dispatch(ShellMessageBuilder.ofView(this, AppViewEvent.of(this, AppViewEventArgs.Direction.PREVIOUS)));
				consumed = true;
			}
			else if (event.isKey(Key.CursorRight)) {
				dispatch(ShellMessageBuilder.ofView(this, AppViewEvent.of(this, AppViewEventArgs.Direction.NEXT)));
				consumed = true;
			}
			return KeyHandler.resultOf(event, consumed, null);
		};

		KeyHandler handler2 = main != null ? main.getKeyHandler() : super.getKeyHandler();
		return handler2.thenIfNotConsumed(handler1);
	}

	@Override
	public boolean hasFocus() {
		if (main != null) {
			return main.hasFocus();
		}
		return super.hasFocus();
	}

	public void setMain(View main) {
		this.main = main;
	}

	public void setModal(View modal) {
		this.modal = modal;
	}

	/**
	 * {@link ViewEventArgs} for {@link AppViewEvent}.
	 *
	 * @param direction the direction enumeration
	 */
	public record AppViewEventArgs(Direction direction) implements ViewEventArgs {

		/**
		 * Direction where next selection should go.
		 */
		public enum Direction {
			PREVIOUS,
			NEXT
		}

		public static AppViewEventArgs of(Direction direction) {
			return new AppViewEventArgs(direction);
		}
	}

	/**
	 * {@link ViewEvent} indicating direction for a next selection.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record AppViewEvent(View view, AppViewEventArgs args) implements ViewEvent {

		public static AppViewEvent of(View view, AppViewEventArgs.Direction direction) {
			return new AppViewEvent(view, AppViewEventArgs.of(direction));
		}
	}
}
