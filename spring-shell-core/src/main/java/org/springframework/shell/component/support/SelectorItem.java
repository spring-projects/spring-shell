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

import org.springframework.util.StringUtils;

public interface SelectorItem<T> extends Nameable, Matchable, Enableable, Itemable<T> {

	static <T> SelectorItem<T> of(String name, T item) {
		return of(name, item, true);
	}

	static <T> SelectorItem<T> of(String name, T item, boolean enabled) {
		return new SelectorItemWrapper<T>(name, item, enabled);
	}

	public static class SelectorItemWrapper<T> implements SelectorItem<T> {
		private String name;
		private boolean enabled;
		private T item;

		public SelectorItemWrapper(String name, T item) {
			this(name, item, true);
		}

		public SelectorItemWrapper(String name, T item, boolean enabled) {
			this.name = name;
			this.item = item;
			this.enabled = enabled;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean matches(String match) {
			if (!StringUtils.hasText(match)) {
				return true;
			};
			return name.toLowerCase().contains(match.toLowerCase());
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		public T getItem() {
			return item;
		}
	}
}
