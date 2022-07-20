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
package org.springframework.shell.component.flow;

/**
 * Interface for selectitem contract in selectors.
 *
 * @author Janne Valkealahti
 */
public interface SelectItem {

	/**
	 * Gets a name.
	 *
	 * @return a name
	 */
	String name();

	/**
	 * Gets an item
	 *
	 * @return an item
	 */
	String item();

	/**
	 * Returns if item is enabled.
	 *
	 * @return if item is enabled
	 */
	boolean enabled();

	/**
	 * Return if the item is selected.
	 * 
	 * @return if item is selected
	 */
	boolean selected();

	public static SelectItem of(String name, String item) {
		return of(name, item, true, false);
	}

	public static SelectItem of(String name, String item, boolean enabled, boolean selected) {
		return new DefaultSelectItem(name, item, enabled, selected);
	}
}