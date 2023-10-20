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

import org.springframework.shell.component.view.control.Control;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.screen.Screen;

/**
 * Base interface for all cells. Typically a {@link Cell} is a building block in
 * a {@link View} not needing to be aware of how it is drawn into a {@link Screen}
 * but needs to aware of its "item", bounds via {@link Control} and other
 * properties like {@code background}.
 *
 * @author Janne Valkealahti
 */
public interface Cell<T> extends Control {

	/**
	 * Get item bound to a cell.
	 *
	 * @return item bound to a cell
	 */
	T getItem();

	/**
	 * Sets an item to bound into a cell.
	 *
	 * @param item item to bound into a cell
	 */
	void setItem(T item);

	/**
	 * Sets a style.
	 *
	 * @param style the style
	 */
	void setStyle(int style);

	/**
	 * Sets a foreground color.
	 *
	 * @param foregroundColor the background color
	 */
	void setForegroundColor(int foregroundColor);

	/**
	 * Sets a background color.
	 *
	 * @param backgroundColor the background color
	 */
	void setBackgroundColor(int backgroundColor);

}
