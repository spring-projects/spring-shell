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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.view.control.MenuView.MenuItem;
import org.springframework.shell.component.view.control.MenuView.MenuViewOpenSelectedItemEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Dimension;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.ThemeResolver;

/**
 * {@link MenuBarView} shows {@link MenuBarItem items} horizontally and is
 * typically used in layouts which builds complete terminal UI's.
 *
 * Internally {@link MenuView} is used to show the menus.
 *
 * @author Janne Valkealahti
 */
public class MenuBarView extends BoxView {

	private final Logger log = LoggerFactory.getLogger(MenuBarView.class);
	private final List<MenuBarItem> items = new ArrayList<>();
	private MenuView currentMenuView;
	private int activeItemIndex = -1;

	// Need to keep menuviews alive not to lose their states
	private final Map<MenuBarItem, MenuView> menuViews = new HashMap<>();

	/**
	 * Construct menubar view with menubar items.
	 *
	 * @param items the menubar items
	 */
	public MenuBarView(MenuBarItem[] items) {
		setItems(Arrays.asList(items));
	}

	/**
	 * Construct menubar view with menubar items.
	 *
	 * @param items the menubar items
	 */
	public static MenuBarView of(MenuBarItem... items) {
		return new MenuBarView(items);
	}

	@Override
	protected String getBackgroundStyle() {
		return StyleSettings.TAG_MENUBAR_BACKGROUND;
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		log.debug("Drawing menu bar to {}", rect);
		Writer writer1 = screen.writerBuilder().build();
		Writer writer2 = screen.writerBuilder().style(ScreenItem.STYLE_BOLD).build();
		int x = rect.x();
		ListIterator<MenuBarItem> iter = items.listIterator();
		while (iter.hasNext()) {
			MenuBarItem item = iter.next();
			int index = iter.previousIndex();
			Writer writer = activeItemIndex == index ? writer2 : writer1;
			String text = String.format(" %s%s", item.getTitle(), iter.hasNext() ? " " : "");
			writer.text(text, x, rect.y());
			x += text.length();
		}
		if (currentMenuView != null) {
			currentMenuView.draw(screen);
		}
		super.drawInternal(screen);
	}

	@Override
	protected void initInternal() {
		registerKeyBinding(Key.CursorLeft, () -> left());
		registerKeyBinding(Key.CursorRight, () -> right());

		registerMouseBinding(MouseEvent.Type.Released | MouseEvent.Button.Button1, event -> select(event));
	}

	@Override
	public KeyHandler getKeyHandler() {
		// check if possible menuview handles an event
		KeyHandler handler = currentMenuView != null ? currentMenuView.getKeyHandler() : KeyHandler.neverConsume();
		return handler.thenIfNotConsumed(super.getKeyHandler());
	}

	@Override
	public MouseHandler getMouseHandler() {
		// check if possible menuview handles an event
		MouseHandler handler = currentMenuView != null ? currentMenuView.getMouseHandler()
				: MouseHandler.neverConsume();
		return handler.thenIfNotConsumed(super.getMouseHandler());
	}

	/**
	 * Gets a menubar items.
	 *
	 * @return menubar items
	 */
	public List<MenuBarItem> getItems() {
		return items;
	}

	/**
	 * Sets a selected index. If given index is not within bounds of size of items,
	 * selection is set to {@code -1} effectively un-selecting active item.
	 *
	 * @param index the new index
	 */
	public void setSelected(int index) {
		if (index >= items.size() || index < 0) {
			activeItemIndex = -1;
		}
		else {
			activeItemIndex = index;
		}
	}

	private void left() {
		if (activeItemIndex > 0) {
			setSelected(activeItemIndex - 1);
			checkMenuView();
		}
	}

	private void right() {
		if (activeItemIndex + 1 < items.size()) {
			setSelected(activeItemIndex + 1);
			checkMenuView();
		}
	}

	private int indexAtPosition(int x, int y) {
		Rectangle rect = getRect();
		if (!rect.contains(x, y)) {
			return -1;
		}
		int i = 0;
		int p = 1;
		for (MenuBarItem item : items) {
			p += item.getTitle().length() + 1;
			if (x < p) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private int positionAtIndex(int index) {
		int i = 0;
		int x = 1;
		for (MenuBarItem item : items) {
			if (i == index) {
				return x;
			}
			x += item.getTitle().length() + 1;
			i++;
		}
		return x;
	}

	private int itemIndex(MenuBarItem item) {
		int index = 0;
		for (MenuBarItem i : items) {
			if (i == item) {
				return index;
			}
			index++;
		}
		return -1;
	}

	private void select(MouseEvent event) {
		int x = event.x();
		int y = event.y();
		int i = indexAtPosition(x, y);
		if (i > -1) {
			if (i == activeItemIndex) {
				setSelected(-1);
			}
			else {
				setSelected(i);
			}
		}
		checkMenuView();
	}

	@Override
	public void setThemeName(String themeName) {
		super.setThemeName(themeName);
		menuViews.values().forEach(view -> view.setThemeName(themeName));
	}

	@Override
	public void setThemeResolver(ThemeResolver themeResolver) {
		super.setThemeResolver(themeResolver);
		menuViews.values().forEach(view -> view.setThemeResolver(themeResolver));
	}

	private void checkMenuView() {
		if (activeItemIndex < 0) {
			closeCurrentMenuView();
		}
		else {
			MenuBarItem item = items.get(activeItemIndex);
			currentMenuView = menuViews.computeIfAbsent(item, i -> buildMenuView(i));
		}
	}

	private void closeCurrentMenuView() {
		currentMenuView = null;
	}

	private MenuView buildMenuView(MenuBarItem item) {
		MenuView menuView = new MenuView(item.getItems());
		menuView.init();
		menuView.setEventLoop(getEventLoop());
		menuView.setThemeResolver(getThemeResolver());
		menuView.setThemeName(getThemeName());
		menuView.setViewService(getViewService());
		menuView.setShowBorder(true);
		menuView.setLayer(1);
		Rectangle rect = getInnerRect();
		int x = positionAtIndex(activeItemIndex);
		Dimension dim = menuView.getPreferredDimension();
		menuView.setRect(rect.x() + x, rect.y() + 1, dim.width(), dim.height());
		menuView.onDestroy(getEventLoop().viewEvents(MenuViewOpenSelectedItemEvent.class, menuView)
			.subscribe(event -> {
				closeCurrentMenuView();
			}));

		return menuView;
	}

	/**
	 * Sets items.
	 *
	 * @param items status items
	 */
	public void setItems(List<MenuBarItem> items) {
		this.items.clear();
		this.items.addAll(items);
		registerHotKeys();
	}

	private void selectItem(MenuBarItem item) {
		int index = itemIndex(item);
		if (index > -1) {
			setSelected(index);
			checkMenuView();
		}
	}

	private void registerHotKeys() {
		getItems().stream()
			.filter(item -> item.getHotKey() != null)
			.forEach(item -> {
				registerHotKeyBinding(item.getHotKey(), () -> selectItem(item));
			});
	}

	/**
	 * {@link MenuBarItem} represents an item in a {@link MenuBarView}.
	 */
	public static class MenuBarItem {

		private String title;
		private List<MenuItem> items;
		private Integer hotKey;

		public MenuBarItem(String title) {
			this(title, null);
		}

		public MenuBarItem(String title, MenuItem[] items) {
			this.title = title;
			this.items = Arrays.asList(items);
		}

		public static MenuBarItem of(String title, MenuItem... items) {
			return new MenuBarItem(title, items);
		}

		public String getTitle() {
			return title;
		}

		public List<MenuItem> getItems() {
			return items;
		}

		public Integer getHotKey() {
			return hotKey;
		}

		public MenuBarItem setHotKey(Integer hotKey) {
			this.hotKey = hotKey;
			return this;
		}
	}

}
