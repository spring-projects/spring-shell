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

import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.util.Assert;

/**
 * {@link AppView} provides an opinionated terminal UI application view
 * controlling main viewing area, menubar, statusbar and modal window system.
 *
 * @author Janne Valkealahti
 */
public class AppView extends BoxView {

	private GridView grid;
	private View main;
	private View menu;
	private View status;
	private boolean menuVisible = true;
	private boolean statusVisible = true;

	public AppView(View main, View menuBar, View statusBar) {
		Assert.notNull(main, "Main view must be set");
		Assert.notNull(menuBar, "Menubar view must be set");
		Assert.notNull(statusBar, "Statusbar view must be set");
		this.main = main;
		this.menu = menuBar;
		this.status = statusBar;
		initLayout();
	}

	@Override
	public void setThemeName(String themeName) {
		super.setThemeName(themeName);
		main.setThemeName(themeName);
		menu.setThemeName(themeName);
		status.setThemeName(themeName);
	}

	@Override
	public void setThemeResolver(ThemeResolver themeResolver) {
		super.setThemeResolver(themeResolver);
		main.setThemeResolver(themeResolver);
		menu.setThemeResolver(themeResolver);
		status.setThemeResolver(themeResolver);
	}

	private void initLayout() {
		grid = new GridView();
		grid.setRowSize(1, 0, 1);
		grid.setColumnSize(0);
		grid.clearItems();
		if (menuVisible && statusVisible) {
			grid.addItem(menu, 0, 0, 1, 1, 0, 0);
			grid.addItem(main, 1, 0, 1, 1, 0, 0);
			grid.addItem(status, 2, 0, 1, 1, 0, 0);
		}
		else if (!menuVisible && !statusVisible) {
			grid.addItem(menu, 0, 0, 0, 0, 0, 0);
			grid.addItem(main, 0, 0, 3, 1, 0, 0);
			grid.addItem(status, 2, 0, 0, 0, 0, 0);
		}
		else if (menuVisible && !statusVisible) {
			grid.addItem(menu, 0, 0, 1, 1, 0, 0);
			grid.addItem(main, 1, 0, 2, 1, 0, 0);
			grid.addItem(status, 2, 0, 0, 1, 0, 0);
		}
		else if (!menuVisible && statusVisible) {
			grid.addItem(menu, 0, 0, 0, 1, 0, 0);
			grid.addItem(main, 0, 0, 2, 1, 0, 0);
			grid.addItem(status, 2, 0, 1, 1, 0, 0);
		}
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		if (grid != null) {
			grid.setRect(rect.x(), rect.y(), rect.width(), rect.height());
			grid.draw(screen);
		}
		super.drawInternal(screen);
	}

	@Override
	public MouseHandler getMouseHandler() {
		MouseHandler handler = grid.getMouseHandler();
		return handler.thenIfNotConsumed(super.getMouseHandler());
	}

	@Override
	public KeyHandler getKeyHandler() {
		KeyHandler handler = args -> {
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

		// if menu has focus, take from there
		if (menu != null && menu.hasFocus()) {
			return menu.getKeyHandler();
		}
		KeyHandler otherHandler = main != null ? main.getKeyHandler() : super.getKeyHandler();
		return otherHandler.thenIfNotConsumed(handler);
	}

	@Override
	public KeyHandler getHotKeyHandler() {
		KeyHandler mainHandler = main != null ? main.getHotKeyHandler() : super.getHotKeyHandler();
		KeyHandler menuHandler = menu != null ? menu.getHotKeyHandler() : super.getHotKeyHandler();
		KeyHandler statusHandler = status != null ? status.getHotKeyHandler() : super.getHotKeyHandler();
		return mainHandler.thenIfNotConsumed(menuHandler).thenIfNotConsumed(statusHandler);
	}

	@Override
	public boolean hasFocus() {
		if (grid != null) {
			return grid.hasFocus();
		}
		return super.hasFocus();
	}

	/**
	 * Sets visibility for a {@code menubar}.
	 *
	 * @param visible the menubar visibility
	 */
	public void setMenuBarVisible(boolean visible) {
		menuVisible = visible;
		initLayout();
	}

	/**
	 * Sets visibility for a {@code statusbar}.
	 *
	 * @param visible the statusbar visibility
	 */
	public void setStatusBarVisible(boolean visible) {
		statusVisible = visible;
		initLayout();
	}

	/**
	 * Toggles a {@code menubar} visibility.
	 */
	public void toggleMenuBarVisibility() {
		setMenuBarVisible(!menuVisible);
	}

	/**
	 * Toggles a {@code statusbar} visibility.
	 */
	public void toggleStatusBarVisibility() {
		setStatusBarVisible(!statusVisible);
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
