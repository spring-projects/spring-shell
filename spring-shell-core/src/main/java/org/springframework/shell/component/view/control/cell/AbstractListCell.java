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
package org.springframework.shell.component.view.control.cell;

import org.springframework.shell.component.view.control.ListView.ItemStyle;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.StyleSettings;
import org.springframework.util.StringUtils;

/**
 * Base implementation of a {@link ListCell}.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractListCell<T> extends AbstractCell<T> implements ListCell<T> {

	private ItemStyle itemStyle;
	private boolean selected;

	public AbstractListCell(T item) {
		super(item);
	}

	public AbstractListCell(T item, ItemStyle itemStyle) {
		super(item);
		this.itemStyle = itemStyle;
	}

	protected String getText() {
		return getItem().toString();
	}

	protected String getIndicator() {
		if (getItemStyle() == ItemStyle.CHECKED || getItemStyle() == ItemStyle.RADIO) {
			return selected ? "[x]" : "[ ]";
		}
		return null;
	}

	protected String getBackgroundStyle() {
		return StyleSettings.TAG_BACKGROUND;
	}

	@Override
	protected void drawBackground(Screen screen) {
		Rectangle rect = getRect();
		int bgColor = resolveThemeBackground(getBackgroundStyle(), getBackgroundColor(), -1);
		if (bgColor > -1) {
			screen.writerBuilder().build().background(rect, bgColor);
		}
	}

	@Override
	protected void drawContent(Screen screen) {
		String indicator = getIndicator();
		String text = null;
		if (StringUtils.hasText(indicator)) {
			text = String.format("%s %s", indicator, getText());
		}
		else {
			text = getText();
		}
		Rectangle rect = getRect();
		Writer writer = screen.writerBuilder().style(getStyle()).color(getForegroundColor()).build();
		writer.text(text, rect.x(), rect.y());
	}

	@Override
	public void setItemStyle(ItemStyle itemStyle) {
		this.itemStyle = itemStyle;
	}

	public ItemStyle getItemStyle() {
		return itemStyle;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

}
