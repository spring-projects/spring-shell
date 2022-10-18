/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.component.support;

import java.util.ArrayList;
import java.util.List;

public interface SelectorList<T extends Nameable> {

	void reset(List<T> items);
	T getSelected();
	void scrollUp();
	void scrollDown();
	List<ProjectionItem<T>> getProjection();

	public static <T extends Nameable> SelectorList<T> of(int max) {
		return new DefaultSelectorList<T>(max);
	}

	public interface ProjectionItem<T> extends Nameable, Selectable {
		T getItem();
	}

	static class DefaultSelectorList<T extends Nameable> implements SelectorList<T> {

		private final List<T> items = new ArrayList<>();
		private final int max;
		private int start;
		private int position;

		public DefaultSelectorList(int max) {
			this.max = max;
		}

		@Override
		public void reset(List<T> items) {
			this.items.clear();
			this.items.addAll(items);
			this.start = 0;
			this.position = 0;
		}

		@Override
		public T getSelected() {
			int index = start + position;
			if (this.items.isEmpty()) {
				return null;
			}
			return this.items.get(index);
		}

		@Override
		public void scrollUp() {
			// at highest position in page, scroll up
			if (start > 0 && position == 0) {
				start--;
			}
			// at highest position, can't go furter, start over from bottom
			else if (start + position <= 0) {
				if (items.size() < max) {
					start = 0;
					position = items.size() - 1;
				}
				else {
					start = items.size() - max;
					position = max - 1;
				}
			}
			// moving up in same page
			else {
				position--;
			}
		}

		@Override
		public void scrollDown() {
			// moving down in same page
			if (start + position + 1 < Math.min(items.size(), max)) {
				position++;
			}
			// at lowest position in page, can't go further, start over from top
			else if(start + position + 1 >= items.size()) {
				start = 0;
				position = 0;
			}
			// in middle of a page, nor highest or lowest
			else if(position < max - 1) {
				position++;
			}
			// at lowest position in page, scroll down
			else {
				start++;
			}
		}

		@Override
		public List<ProjectionItem<T>> getProjection() {
			List<ProjectionItem<T>> projection = new ArrayList<>();
			for (int i = start; i < start + Math.min(items.size(), max); i++) {
				boolean selected = i == start + position;
				BaseProjectionItem<T> item = new BaseProjectionItem<>(items.get(i), selected);
				projection.add(item);
			}
			return projection;
		}

	}

	static class BaseProjectionItem<T extends Nameable> implements ProjectionItem<T> {

		private final T item;
		private final boolean selected;

		BaseProjectionItem(T item, boolean selected) {
			this.item = item;
			this.selected = selected;
		}

		@Override
		public T getItem() {
			return item;
		}

		@Override
		public String getName() {
			return item.getName();
		}

		@Override
		public boolean isSelected() {
			return this.selected;
		}
	}
}
