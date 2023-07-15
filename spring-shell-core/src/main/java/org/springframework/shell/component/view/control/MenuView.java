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
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.Nullable;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.geom.Dimension;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.message.ShellMessageBuilder;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link MenuView} shows {@link MenuView} items vertically and is
 * typically used in layouts which builds complete terminal UI's.
 *
 * @author Janne Valkealahti
 */
public class MenuView extends BoxView {

	private final Logger log = LoggerFactory.getLogger(MenuView.class);
	private final List<MenuItem> items = new ArrayList<>();
	private int activeItemIndex = -1;

	/**
	 * Construct menu view with no initial menu items.
	 */
	public MenuView() {
		this(new MenuItem[0]);
	}

	/**
	 * Construct menu view with menu items.
	 *
	 * @param items the menu items
	 */
	public MenuView(MenuItem[] items) {
		this(items != null ? Arrays.asList(items) : Collections.emptyList());
	}

	/**
	 * Construct menu view with menu items.
	 *
	 * @param items the menu items
	 */
	public MenuView(@Nullable List<MenuItem> items) {
		setItems(items);
	}

	/**
	 * Sets a new menu items. Will always clear existing items and if {@code null}
	 * is passed this effectively keeps items empty.
	 *
	 * @param items the menu items
	 */
	public void setItems(@Nullable List<MenuItem> items) {
		this.items.clear();
		activeItemIndex = -1;
		if (items != null) {
			this.items.addAll(items);
			if (!items.isEmpty()) {
				activeItemIndex = 0;
			}
		}
	}

	/**
	 * Gets a menu items.
	 *
	 * @return the menu items
	 */
	public List<MenuItem> getItems() {
		return items;
	}

	/**
	 * Gets a preferred dimension menu needs to show it's content.
	 *
	 * @return preferred dimension
	 */
	public Dimension getPreferredDimension() {
		int width = 0;
		int height = items.size();
		if (isShowBorder()) {
			height += 2;
		}
		for (MenuItem item : items) {
			int l = item.getTitle().length();
			if (item.getCheckStyle() != MenuItemCheckStyle.CHECKED) {
				l += 4;
				if (isShowBorder()) {
					l += 2;
				}
			}
			width = Math.max(width, l);
		}
		return new Dimension(width, height);
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		int y = rect.y();
		Writer writer = screen.writerBuilder().layer(getLayer()).build();
		Writer writer2 = screen.writerBuilder().layer(getLayer()).style(ScreenItem.STYLE_BOLD).build();
		int i = 0;
		boolean hasCheck = false;
		for (MenuItem item : items) {
			if (item.getCheckStyle() != MenuItemCheckStyle.NOCHECK) {
				hasCheck = true;
				break;
			}
		}
		for (MenuItem item : items) {
			String prefix = hasCheck
					? (item.getCheckStyle() != MenuItemCheckStyle.NOCHECK
						? (item.isChecked() ? "[x] " : "[ ] ")
						: "    ")
					: "";
			String text = prefix + item.getTitle();
			if (activeItemIndex == i) {
				writer2.text(text, rect.x(), y);
			}
			else {
				writer.text(text, rect.x(), y);
			}
			i++;
			y++;
		}
		super.drawInternal(screen);
	}

	@Override
	protected void initInternal() {
		registerKeyBinding(Key.CursorUp, () -> move(-1));
		registerKeyBinding(Key.CursorDown, () -> move(1));
		registerKeyBinding(Key.Enter, () -> keySelect());

		registerMouseBinding(MouseEvent.Type.Released | MouseEvent.Button.Button1, event -> mouseSelect(event));
		registerMouseBinding(MouseEvent.Type.Wheel | MouseEvent.Button.WheelDown, () -> move(1));
		registerMouseBinding(MouseEvent.Type.Wheel | MouseEvent.Button.WheelUp, () -> move(-1));
	}

	private void keySelect() {
		MenuItem item = items.get(activeItemIndex);
		dispatch(ShellMessageBuilder.ofView(this, MenuViewOpenSelectedItemEvent.of(this, item)));
		if (item.getAction() != null) {
			dispatchRunnable(item.getAction());
		}
	}

	private void move(int count) {
		log.trace("move({})", count);
		setSelected(activeItemIndex + count);
	}

	private void setSelected(int index) {
		if (index >= items.size()) {
			activeItemIndex = 0;
		}
		else if(index < 0) {
			activeItemIndex = items.size() - 1;
		}
		else {
			if (activeItemIndex != index) {
				activeItemIndex = index;
				MenuItem item = items.get(index);
				dispatch(ShellMessageBuilder.ofView(this, MenuViewSelectedItemChangedEvent.of(this, item)));
			}
		}
	}

	private void mouseSelect(MouseEvent event) {
		log.trace("select({})", event);
		int x = event.x();
		int y = event.y();
		setSelected(indexAtPosition(x, y));
		keySelect();
	}

	private int indexAtPosition(int x, int y) {
		Rectangle rect = getRect();
		if (!rect.contains(x, y)) {
			return -1;
		}
		int pos = y - rect.y() - 1;
		if (pos > -1 && pos < items.size()) {
			MenuItem i = items.get(pos);
			if (i != null) {
				return pos;
			}
		}
		return -1;
	}

	/**
	 * Specifies how a {@link MenuItem} shows selection state.
	 */
	public enum MenuItemCheckStyle {

		/**
		 * The menu item will be shown normally, with no check indicator. The default.
		 */
		NOCHECK,

		/**
		 * The menu item will indicate checked/un-checked state.
		 */
		CHECKED,

		/**
		 * The menu item is part of a menu radio group and will indicate selected state.
		 */
		RADIO
	}

	/**
	 * {@link MenuItem} represents an item in a {@link MenuView}.
	 *
	 * @see Menu
	 */
	public static class MenuItem  {

		private final String title;
		private final MenuItemCheckStyle checkStyle;
		private final List<MenuItem> items;
		private boolean checked;
		private Runnable action;

		/**
		 * Construct menu item with a title.
		 *
		 * @param title the title
		 */
		public MenuItem(String title) {
			this(title, MenuItemCheckStyle.NOCHECK);
		}

		/**
		 * Construct menu item with a title and a check style.
		 *
		 * @param title the title
		 * @param checkStyle the check style
		 */
		public MenuItem(String title, MenuItemCheckStyle checkStyle) {
			this(title, checkStyle, null);
		}

		/**
		 * Construct menu item with a title and a check style.
		 *
		 * @param title the title
		 * @param checkStyle the check style
		 * @param action the action to run when item is chosen
		 */
		public MenuItem(String title, MenuItemCheckStyle checkStyle, Runnable action) {
			Assert.state(StringUtils.hasText(title), "title must have text");
			Assert.notNull(checkStyle, "check style cannot be null");
			this.title = title;
			this.checkStyle = checkStyle;
			this.action = action;
			this.items = null;
		}

		protected MenuItem(String title, MenuItem[] items) {
			this(title, Arrays.asList(items));
		}

		protected MenuItem(String title, List<MenuItem> items) {
			Assert.state(StringUtils.hasText(title), "title must have text");
			Assert.notNull(items, "Sub items cannot be null");
			this.title = title;
			this.items = items;
			this.checkStyle = null;
		}

		/**
		 * Return a {@link MenuItem} with a given {@code title}.
		 *
		 * @param title the title
		 * @return a menu item
		 */
		public static MenuItem of(String title) {
			return new MenuItem(title);
		}

		/**
		 * Return a {@link MenuItem} with a given {@code title} and a
		 * {@code check style}.
		 *
		 * @param title the title
		 * @param checkStyle the check style
		 * @return a menu item
		 */
		public static MenuItem of(String title, MenuItemCheckStyle checkStyle) {
			return new MenuItem(title, checkStyle);
		}

		public static MenuItem of(String title, MenuItemCheckStyle checkStyle, Runnable action) {
			return new MenuItem(title, checkStyle, action);
		}

		public Runnable getAction() {
			return action;
		}

		public void setAction(Runnable action) {
			this.action = action;
		}

		/**
		 * Get a {@code title}. Never null, empty or just having white spaces.
		 *
		 * @return a title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * Gets a check style. This will be {@code null} if constructed via
		 * {@link Menu} as style doesn't apply items being a sub-menu.
		 *
		 * @return a check style
		 */
		@Nullable
		public MenuItemCheckStyle getCheckStyle() {
			return checkStyle;
		}

		/**
		 * Sets a checked state.
		 *
		 * @param checked checked state
		 */
		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		/**
		 * Gets a checked state.
		 *
		 * @return checked state
		 */
		public boolean isChecked() {
			return checked;
		}

		/**
		 * Gets sub menu items. This will be {@code null} if not constructed via
		 * {@link Menu} as plain {@link MenuItem} can't have other items.
		 *
		 * @return a menu items
		 */
		@Nullable
		public List<MenuItem> getItems() {
			return items;
		}
	}

	/**
	 * {@link Menu} represents an item in a {@link MenuView} being a specialisation
	 * of {@link MenuItem} indicating it having a sub-menu.
	 *
	 * @see MenuItem
	 */
	public static class Menu extends MenuItem {

		/**
		 * Construct menu with a title.
		 *
		 * @param title the title
		 */
		public Menu(String title) {
			super(title);
		}

		/**
		 * Construct menu with a title and a menu items.
		 *
		 * @param title the title
		 * @param items the menu items
		 */
		public Menu(String title, MenuItem[] items) {
			super(title, items);
		}

		/**
		 * Construct menu with a title and a menu items.
		 *
		 * @param title the title
		 * @param items the menu items
		 */
		public Menu(String title, List<MenuItem> items) {
			super(title, items);
		}

		/**
		 * Return a {@link Menu} with a given {@code title} and {@link MenuItem}s.
		 *
		 * @param title the title
		 * @param items the menu items
		 * @return a menu
		 */
		public static Menu of(String title, MenuItem... items) {
			return new Menu(title, items);
		}
	}

	/**
	 * {@link ViewEventArgs} for {@link MenuViewOpenSelectedItemEvent} and
	 * {@link MenuViewSelectedItemChangedEvent}.
	 *
	 * @param item the menu view item
	 */
	public record MenuViewItemEventArgs(MenuItem item) implements ViewEventArgs {

		public static MenuViewItemEventArgs of(MenuItem item) {
			return new MenuViewItemEventArgs(item);
		}
	}

	/**
	 * {@link ViewEvent} indicating that selected item has been requested to open.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record MenuViewOpenSelectedItemEvent(View view, MenuViewItemEventArgs args) implements ViewEvent {

		public static MenuViewOpenSelectedItemEvent of(View view, MenuItem item) {
			return new MenuViewOpenSelectedItemEvent(view, MenuViewItemEventArgs.of(item));
		}
	}

	/**
	 * {@link ViewEvent} indicating that selected item has changed.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record MenuViewSelectedItemChangedEvent(View view, MenuViewItemEventArgs args) implements ViewEvent {

		public static MenuViewSelectedItemChangedEvent of(View view, MenuItem item) {
			return new MenuViewSelectedItemChangedEvent(view, MenuViewItemEventArgs.of(item));
		}
	}

}
