/*
 * Copyright 2023-2024 the original author or authors.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.Nullable;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Dimension;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.StyleSettings;
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

	// we support only one radio group
	private MenuItem radioActive;

	// keep checked states outside of items itself
	private Set<MenuItem> checkedActive = new HashSet<>();

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
			items.forEach(i -> {
				if (i.initialCheckState && i.getCheckStyle() == MenuItemCheckStyle.CHECKED) {
					checkedActive.add(i);
				}
				else if (i.initialCheckState && i.getCheckStyle() == MenuItemCheckStyle.RADIO) {
					radioActive = i;
				}
			});
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
		int selectedStyle = resolveThemeStyle(StyleSettings.TAG_HIGHLIGHT, ScreenItem.STYLE_BOLD);
		int selectedForegroundColor = resolveThemeForeground(StyleSettings.TAG_HIGHLIGHT, -1, -1);
		int selectedBackgroundColor = resolveThemeBackground(StyleSettings.TAG_HIGHLIGHT, -1, -1);
		Writer writer = screen.writerBuilder()
			.layer(getLayer()).build();
		Writer selectedWriter = screen.writerBuilder()
			.layer(getLayer())
			.color(selectedForegroundColor)
			.style(selectedStyle).build();
		int i = 0;
		boolean hasCheck = false;
		for (MenuItem item : items) {
			if (item.getCheckStyle() != MenuItemCheckStyle.NOCHECK) {
				hasCheck = true;
				break;
			}
		}
		for (MenuItem item : items) {
			String prefix = hasCheck ? "    " : "";
			if (item.checkStyle == MenuItemCheckStyle.RADIO) {
				if (radioActive == item) {
					prefix = "[x] ";
				}
				else {
					prefix = "[ ] ";
				}
			}
			else if (item.checkStyle == MenuItemCheckStyle.CHECKED) {
				if (checkedActive.contains(item)) {
					prefix = "[x] ";
				}
				else {
					prefix = "[ ] ";
				}
			}
			String text = prefix + item.getTitle();
			if (activeItemIndex == i) {
				selectedWriter.text(text, rect.x(), y);
				if (selectedBackgroundColor > -1) {
					Rectangle itemRect = new Rectangle(rect.x(), y, rect.width(), 1);
					selectedWriter.background(itemRect, selectedBackgroundColor);
				}
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

	/**
	 * Request to handle current selected item to get opened.
	 */
	private void keySelect() {
		select();
	}

	/**
	 * Request to handle mouse event.
	 */
	private void mouseSelect(MouseEvent event) {
		log.trace("select({})", event);
		setSelected(indexAtPosition(event.x(), event.y()));
		select();
	}

	/**
	 * Toggle {@link MenuItem} checked state.
	 *
	 * @param item the menu item to toggle
	 */
	public void toggle(MenuItem item) {
		if (item.checkStyle == MenuItemCheckStyle.RADIO) {
			radioActive = item;
		}
		else if (item.checkStyle == MenuItemCheckStyle.CHECKED) {
			if (checkedActive.contains(item)) {
				checkedActive.remove(item);
			}
			else {
				checkedActive.add(item);
			}
		}
	}

	/**
	 * From current selected item index, if applicable, dispatch
	 * {@link MenuViewSelectedItemChangedEvent}.
	 */
	private void select() {
		if (activeItemIndex > -1 && activeItemIndex < items.size()) {
			MenuItem item = items.get(activeItemIndex);
			if (item == null) {
				return;
			}
			toggle(item);
			dispatch(ShellMessageBuilder.ofView(this, MenuViewOpenSelectedItemEvent.of(this, item)));
			if (item.getAction() != null) {
				dispatchRunnable(item.getAction());
			}
		}
	}

	/**
	 * Request to move selection up or down.
	 */
	private void move(int count) {
		log.trace("move({})", count);
		setSelected(activeItemIndex + count);
	}

	/**
	 *
	 */
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

	/**
	 * Gets an index of a item at given position. Returns negative index if position
	 * doesn't map to existing item.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return an index or negative if not found
	 */
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
		private Runnable action;
		private boolean initialCheckState = false;

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
		 * Construct menu item with a title, a check style and a runnable.
		 *
		 * @param title the title
		 * @param checkStyle the check style
		 * @param action the action to run when item is chosen
		 */
		public MenuItem(String title, MenuItemCheckStyle checkStyle, Runnable action) {
			this(title, checkStyle, action, false);
		}

		/**
		 * Construct menu item with a title, a check style, a runnable and initial
		 * checked state.
		 *
		 * @param title the title
		 * @param checkStyle the check style
		 * @param action the action to run when item is chosen
		 * @param initialCheckState initial checked state
		 */
		public MenuItem(String title, MenuItemCheckStyle checkStyle, Runnable action, boolean initialCheckState) {
			Assert.state(StringUtils.hasText(title), "title must have text");
			Assert.notNull(checkStyle, "check style cannot be null");
			this.title = title;
			this.checkStyle = checkStyle;
			this.action = action;
			this.items = null;
			this.initialCheckState = initialCheckState;
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

		public static MenuItem of(String title, MenuItemCheckStyle checkStyle, Runnable action, boolean initialCheckState) {
			return new MenuItem(title, checkStyle, action, initialCheckState);
		}

		public Runnable getAction() {
			return action;
		}

		public void setAction(Runnable action) {
			this.action = action;
		}

		/**
		 * Gets initial check state.
		 *
		 * @return initial check state
		 */
		public boolean isInitialCheckState() {
			return initialCheckState;
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
