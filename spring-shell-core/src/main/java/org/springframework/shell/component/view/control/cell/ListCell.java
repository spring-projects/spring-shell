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

/**
 * Extension of a {@link Cell} to make it aware of an item style and selection state.
 *
 * @author Janne Valkealahti
 */
public interface ListCell<T> extends Cell<T> {

	/**
	 * Set {@link ItemStyle}.
	 *
	 * @param itemStyle the item style
	 */
	void setItemStyle(ItemStyle itemStyle);

	/**
	 * Set selection state.
	 *
	 * @param selected the selection state
	 */
	void setSelected(boolean selected);

	/**
	 * Helper method to build a {@code ListCell}.
	 *
	 * @param item the item
	 * @param itemStyle the item style
	 * @return a default list cell
	 */
	static <T> ListCell<T> of(T item, ItemStyle itemStyle) {
		return new DefaultListCell<T>(item, itemStyle);
	}

	static class DefaultListCell<T> extends AbstractListCell<T> {

		DefaultListCell(T item, ItemStyle itemStyle) {
			super(item, itemStyle);
		}

	}
}
