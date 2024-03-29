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
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.StyleSettings;

/**
 * {@link StatusBarView} shows {@link StatusItem items} horizontally and is
 * typically used in layouts which builds complete terminal UI's.
 *
 * @author Janne Valkealahti
 */
public class StatusBarView extends BoxView {

	private final Logger log = LoggerFactory.getLogger(StatusBarView.class);
	private final List<StatusItem> items = new ArrayList<>();

	public StatusBarView() {
		this(new StatusItem[0]);
	}

	public StatusBarView(StatusItem[] items) {
		this(Arrays.asList(items));
	}

	public StatusBarView(List<StatusItem> items) {
		setItems(items);
	}

	@Override
	protected String getBackgroundStyle() {
		return StyleSettings.TAG_STATUSBAR_BACKGROUND;
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		log.debug("Drawing status bar to {}", rect);
		Writer writer = screen.writerBuilder().build();
		int x = rect.x();
		ListIterator<StatusItem> iter = items.listIterator();
		while (iter.hasNext()) {
			StatusItem item = iter.next();
			String text = String.format(" %s%s", item.getTitle(), iter.hasNext() ? " |" : "");
			writer.text(text, x, rect.y());
			x += text.length();
		}
		super.drawInternal(screen);
	}

	@Override
	public MouseHandler getMouseHandler() {
		log.trace("getMouseHandler()");
		return args -> {
			MouseEvent event = args.event();
			boolean consumed = false;
			if (!event.hasModifier() && event.has(MouseEvent.Type.Released) && event.has(MouseEvent.Button.Button1)) {
				int x = event.x();
				int y = event.y();
				StatusItem item = itemAt(x, y);
				if (item != null) {
					dispatch(ShellMessageBuilder.ofView(this, StatusBarViewOpenSelectedItemEvent.of(this, item)));
					if (item.getAction() != null) {
						dispatchRunnable(item.getAction());
					}
				}
				consumed = true;
			}
			// status bar don't request focus
			return MouseHandler.resultOf(args.event(), consumed, null, null);
		};
	}

	private StatusItem itemAt(int x, int y) {
		Rectangle rect = getRect();
		if (!rect.contains(x, y)) {
			return null;
		}
		int ix = 0;
		for (StatusItem item : items) {
			if (x < ix + item.getTitle().length()) {
				return item;
			}
			ix += item.getTitle().length();
		}
		return null;
	}

	/**
	 * Sets items.
	 *
	 * @param items status items
	 */
	public void setItems(List<StatusItem> items) {
		this.items.clear();
		this.items.addAll(items);
		registerHotKeys();
	}

	/**
	 * Gets a status items.
	 *
	 * @return the status items
	 */
	public List<StatusItem> getItems() {
		return items;
	}

	private void registerHotKeys() {
		getItems().stream()
			.filter(item -> item.getHotKey() != null)
			.forEach(item -> {
				Runnable action = item.getAction();
				if (action != null) {
					registerHotKeyBinding(item.getHotKey(), action);
				}
			});
	}

	/**
	 * {@link StatusItem} represents an item in a {@link StatusBarView}.
	 */
	public static class StatusItem {

		private String title;
		private Runnable action;
		private Integer hotKey;

		public StatusItem(String title) {
			this(title, null);
		}

		public StatusItem(String title, Runnable action) {
			this(title, action, null);
		}

		public StatusItem(String title, Runnable action, Integer hotKey) {
			this.title = title;
			this.action = action;
			this.hotKey = hotKey;
		}

		public static StatusItem of(String title) {
			return new StatusItem(title);
		}

		public static StatusItem of(String title, Runnable action) {
			return new StatusItem(title, action);
		}

		public static StatusItem of(String title, Runnable action, Integer hotKey) {
			return new StatusItem(title, action, hotKey);
		}

		public String getTitle() {
			return title;
		}

		public Runnable getAction() {
			return action;
		}

		public StatusItem setAction(Runnable action) {
			this.action = action;
			return this;
		}

		public Integer getHotKey() {
			return hotKey;
		}

		public StatusItem setHotKey(Integer hotKey) {
			this.hotKey = hotKey;
			return this;
		}

	}

	/**
	 * {@link ViewEventArgs} for {@link StatusBarViewOpenSelectedItemEvent}.
	 *
	 * @param item the status bar view item
	 */
	public record StatusBarViewItemEventArgs(StatusItem item) implements ViewEventArgs {

		public static StatusBarViewItemEventArgs of(StatusItem item) {
			return new StatusBarViewItemEventArgs(item);
		}
	}

	/**
	 * {@link ViewEvent} indicating that selected item has been requested to open.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record StatusBarViewOpenSelectedItemEvent(View view, StatusBarViewItemEventArgs args) implements ViewEvent {

		public static StatusBarViewOpenSelectedItemEvent of(View view, StatusItem item) {
			return new StatusBarViewOpenSelectedItemEvent(view, StatusBarViewItemEventArgs.of(item));
		}
	}

}
