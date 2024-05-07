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
import java.util.ListIterator;
import java.util.Set;
import java.util.function.BiFunction;

import org.springframework.lang.Nullable;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.control.cell.ListCell;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.StyleSettings;
import org.springframework.util.Assert;

/**
 * {@code ListView} is a {@link View} showing items in a vertical list.
 *
 * <p>Supported view commands:
 * <ul>
 * <li>{@link ViewCommand#LINE_UP} - Move active line upwards.
 * <li>{@link ViewCommand#LINE_DOWN} - Move active line downwards.
 * </ul>
 *
 * @author Janne Valkealahti
 */
public class ListView<T> extends BoxView {

	private final List<T> items = new ArrayList<>();
	private final List<ListCell<T>> cells = new ArrayList<>();
	private final ItemStyle itemStyle;
	private int start = 0;
	private int pos = 0;
	private final Set<Integer> selected = new HashSet<>();
	private BiFunction<ListView<T>, T, ListCell<T>> factory = (listView, item) -> ListCell.of(item,
			listView.getItemStyle());

	/**
	 * Specifies how a item shows selection state.
	 */
	public enum ItemStyle {

		/**
		 * The item will be shown normally, with no check indicator. The default.
		 */
		NOCHECK,

		/**
		 * The item will indicate checked/un-checked state.
		 */
		CHECKED,

		/**
		 * The item is part of a radio group and will indicate selected state.
		 */
		RADIO
	}

	public ListView() {
		this.itemStyle = ItemStyle.NOCHECK;
	}

	public ListView(ItemStyle itemStyle) {
		Assert.notNull(itemStyle, "item style must be set");
		this.itemStyle = itemStyle;
		setItems(null);
	}

	public ListView(T[] items, ItemStyle itemStyle) {
		this(items != null ? Arrays.asList(items) : Collections.emptyList(), itemStyle);
	}

	public ListView(@Nullable List<T> items, ItemStyle itemStyle) {
		Assert.notNull(itemStyle, "item style must be set");
		this.itemStyle =  itemStyle;
		setItems(items);
	}

	@Override
	protected void initInternal() {
		super.initInternal();
		registerViewCommand(ViewCommand.LINE_UP, () -> up());
		registerViewCommand(ViewCommand.LINE_DOWN, () -> down());

		registerKeyBinding(Key.CursorUp, ViewCommand.LINE_UP);
		registerKeyBinding(Key.CursorDown, ViewCommand.LINE_DOWN);
		registerKeyBinding(Key.Enter, () -> enter());
		registerKeyBinding(Key.Space, () -> space());

		registerMouseBinding(MouseEvent.Type.Wheel | MouseEvent.Button.WheelUp, ViewCommand.LINE_UP);
		registerMouseBinding(MouseEvent.Type.Wheel | MouseEvent.Button.WheelDown, ViewCommand.LINE_DOWN);
		registerMouseBinding(MouseEvent.Type.Released | MouseEvent.Button.Button1, event -> click(event));
	}

	public ItemStyle getItemStyle() {
		return itemStyle;
	}

	private void updateCells() {
		cells.clear();
		for (T i : items) {
			ListCell<T> c = factory.apply(this, i);
			c.setItemStyle(getItemStyle());
			cells.add(c);
		}
	}

	public void setItems(@Nullable List<T> items) {
		this.items.clear();
		if (items != null) {
			this.items.addAll(items);
		}
		if (this.items.isEmpty()) {
			start = -1;
			pos = -1;
		}
		else {
			start = 0;
			pos = 0;
		}
		updateCells();
	}

	private void updateSelectionStates() {
		int active = start + pos;
		if (itemStyle == ItemStyle.CHECKED) {
			boolean removed = selected.remove(active);
			if (!removed) {
				selected.add(active);
			}
		}
		else if (itemStyle == ItemStyle.RADIO) {
			selected.clear();
			selected.add(active);
		}
		ListIterator<ListCell<T>> iter = cells.listIterator();
		while (iter.hasNext()) {
			int index = iter.nextIndex();
			ListCell<T> c = iter.next();
			c.setSelected(selected.contains(index));
		}
	}

	@Override
	protected void drawInternal(Screen screen) {
		if (start > -1 && pos > -1) {
			Rectangle rect = getInnerRect();
			int y = rect.y();
			int selectedStyle = resolveThemeStyle(StyleSettings.TAG_HIGHLIGHT, ScreenItem.STYLE_BOLD);
			int selectedForegroundColor = resolveThemeForeground(StyleSettings.TAG_HIGHLIGHT, -1, -1);
			int selectedBackgroundColor = resolveThemeBackground(StyleSettings.TAG_HIGHLIGHT, -1, -1);
			int i = 0;

			for (ListCell<T> c : cells) {
				if (i < start) {
					i++;
					continue;
				}
				c.setRect(rect.x(), y++, rect.width(), 1);
				if (i == start + pos) {
					c.setForegroundColor(selectedForegroundColor);
					c.setBackgroundColor(selectedBackgroundColor);
					c.setStyle(selectedStyle);
				}
				else {
					c.setBackgroundColor(-1);
					c.setForegroundColor(-1);
					c.setStyle(-1);
				}
				c.draw(screen);
				i++;
				if (i - start >= rect.height()) {
					break;
				}
			}
		}

		super.drawInternal(screen);
	}

	public void setCellFactory(BiFunction<ListView<T>, T, ListCell<T>> factory) {
		Assert.notNull(factory, "cell factory must be set");
		this.factory = factory;
	}

	private void scrollIndex(boolean up) {
		int size = items.size();
		int maxItems = getInnerRect().height();
		int active = start + pos;
		if (up) {
			if (start > 0 && pos == 0) {
				start--;
			}
			else if (start + pos <= 0) {
				start = size - Math.min(maxItems, size);
				pos = Math.min(maxItems, size) - 1;
			}
			else {
				pos--;
			}
		}
		else {
			if (start + pos + 1 < Math.min(maxItems, size)) {
				pos++;
			}
			else if (start + pos + 1 >= size) {
				start = 0;
				pos = 0;
			}
			else {
				start++;
			}
		}
		if (active != start + pos) {
			dispatch(ShellMessageBuilder.ofView(this, ListViewSelectedItemChangedEvent.of(this, selectedItem())));
		}
	}

	private void scrollIndex(int step) {
		if (start < 0 && pos < 0) {
			return;
		}
		if (step < 0) {
			for (int i = step; i < 0; i++) {
				scrollIndex(true);
			}
		}
		else if (step > 0) {
			for (int i = step; i > 0; i--) {
				scrollIndex(false);
			}
		}
	}

	private T selectedItem() {
		T selectedItem = null;
		int active = start + pos;
		if (active >= 0 && active < items.size()) {
			selectedItem = items.get(active);
		}
		return selectedItem;
	}

	private void up() {
		scrollIndex(-1);
	}

	private void down() {
		scrollIndex(1);
	}

	private void enter() {
		if (itemStyle == ItemStyle.NOCHECK) {
			dispatch(ShellMessageBuilder.ofView(this, ListViewOpenSelectedItemEvent.of(this, selectedItem())));
			return;
		}
	}

	private void space() {
		updateSelectionStates();
	}

	private void click(MouseEvent event) {
		int index = event.y() - getInnerRect().y();
		int active = start + index;
		if (active >= 0 && active < items.size()) {
			pos = index;
			if (itemStyle == ItemStyle.NOCHECK) {
				dispatch(ShellMessageBuilder.ofView(this, ListViewSelectedItemChangedEvent.of(this, selectedItem())));
				return;
			}
		}
		updateSelectionStates();
	}

	/**
	 * {@link ViewEventArgs} for {@link ListViewOpenSelectedItemEvent} and
	 * {@link ListViewSelectedItemChangedEvent}.
	 *
	 * @param item the list view item
	 */
	public record ListViewItemEventArgs<T>(T item) implements ViewEventArgs {

		public static <T> ListViewItemEventArgs<T> of(T item) {
			return new ListViewItemEventArgs<T>(item);
		}
	}

	/**
	 * {@link ViewEvent} indicating that selected item has been requested to open.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record ListViewOpenSelectedItemEvent<T>(View view, ListViewItemEventArgs<T> args) implements ViewEvent {

		public static <T> ListViewOpenSelectedItemEvent<T> of(View view, T item) {
			return new ListViewOpenSelectedItemEvent<T>(view, ListViewItemEventArgs.of(item));
		}
	}

	/**
	 * {@link ViewEvent} indicating that selected item has changed.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record ListViewSelectedItemChangedEvent<T>(View view, ListViewItemEventArgs<T> args) implements ViewEvent {

		public static <T> ListViewSelectedItemChangedEvent<T> of(View view, T item) {
			return new ListViewSelectedItemChangedEvent<T>(view, ListViewItemEventArgs.of(item));
		}
	}

}
