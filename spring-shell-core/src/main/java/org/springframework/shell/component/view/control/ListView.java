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
import java.util.List;
import java.util.function.Function;

import org.springframework.shell.component.view.control.cell.ListCell;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.message.ShellMessageBuilder;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.shell.style.StyleSettings;

/**
 * {@link ListView} shows {@code list items} vertically.
 *
 * @author Janne Valkealahti
 */
public class ListView<T> extends BoxView {

	private final List<T> items = new ArrayList<>();
	private int selected = -1;

	private final List<ListCell<T>> cells = new ArrayList<>();
	private Function<ListView<T>, ListCell<T>> factory = listView -> new ListCell<>();

	/**
	 * Construct list view with no initial items.
	 */
	public ListView() {
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		int y = rect.y();

		int selectedStyle = resolveThemeStyle(StyleSettings.TAG_HIGHLIGHT, ScreenItem.STYLE_BOLD);
		int i = 0;
		for (ListCell<T> c : cells) {
			c.setRect(rect.x(), y++, rect.width(), 1);
			if (i == selected) {
				c.updateSelected(true);
				c.setStyle(selectedStyle);
			}
			else {
				c.updateSelected(false);
				c.setBackgroundColor(-1);
				c.setStyle(-1);
			}
			c.updateSelected(i == selected);
			c.draw(screen);
			i++;
		}
		super.drawInternal(screen);
	}

	/**
	 * Sets a cell factory.
	 *
	 * @param factory the cell factory
	 */
	public void setCellFactory(Function<ListView<T>, ListCell<T>> factory) {
		this.factory = factory;
	}

	public void setItems(List<T> items) {
		this.items.clear();
		this.items.addAll(items);
		this.cells.clear();
		for (T i : items) {
			ListCell<T> c = factory.apply(this);
			cells.add(c);
			c.updateItem(i);
		}
	}

	@Override
	protected void initInternal() {
		registerKeyBinding(Key.CursorUp, () -> up());
		registerKeyBinding(Key.CursorDown, () -> down());
		registerKeyBinding(Key.Enter, () -> enter());

		registerMouseBinding(MouseEvent.Type.Wheel | MouseEvent.Button.WheelUp, () -> up());
		registerMouseBinding(MouseEvent.Type.Wheel | MouseEvent.Button.WheelDown, () -> down());
		registerMouseBinding(MouseEvent.Type.Released | MouseEvent.Button.Button1, event -> click(event));
	}

	private void up() {
		updateIndex(-1);
		dispatch(ShellMessageBuilder.ofView(this, ListViewSelectedItemChangedEvent.of(this, selectedItem())));
	}

	private void down() {
		updateIndex(1);
		dispatch(ShellMessageBuilder.ofView(this, ListViewSelectedItemChangedEvent.of(this, selectedItem())));
	}

	private void enter() {
		dispatch(ShellMessageBuilder.ofView(this, ListViewOpenSelectedItemEvent.of(this, selectedItem())));
	}

	private void click(MouseEvent event) {
		int index = event.y() - getInnerRect().y();
		if (index > -1 && index < items.size()) {
			setSelected(index);
		}
	}

	public void setSelected(int selected) {
		if (this.selected != selected) {
			this.selected = selected;
			dispatch(ShellMessageBuilder.ofView(this, ListViewSelectedItemChangedEvent.of(this, selectedItem())));
		}
	}

	private T selectedItem() {
		T selectedItem = null;
		if (selected >= 0 && selected < items.size()) {
			selectedItem = items.get(selected);
		}
		return selectedItem;
	}

	private void updateIndex(int step) {
		int size = items.size();
		if (step > 0) {
			if (selected + step < size) {
				selected += step;
			}
		}
		else if (step < 0) {
			if (selected - step > 0) {
				selected += step;
			}
		}
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
