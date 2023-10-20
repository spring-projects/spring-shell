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

import org.springframework.lang.Nullable;
import org.springframework.shell.component.view.control.cell.Cell;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.ThemeResolver;

/**
 * Base interface for all controls. {@link Control} is able to define a
 * {@link Rectangle} it is bound to and draw into a {@link Screen}.
 *
 * @author Janne Valkealahti
 * @see View
 * @see Cell
 */
public interface Control {

	/**
	 * Draw {@link Control} into a {@link Screen}.
	 *
	 * @param screen the screen
	 */
	void draw(Screen screen);

	/**
	 * Gets rectanle of a bounded box for this {@link View}.
	 *
	 * @return the rectanle of a bounded box
	 */
	Rectangle getRect();

	/**
	 * Sets bounds where this {@link Control} should operate.
	 *
	 * @param x a x coord of a bounded box
	 * @param y an y coord of a bounded box
	 * @param width a width of a bounded box
	 * @param height a height of a bounded box
	 */
	void setRect(int x, int y, int width, int height);

	/**
	 * Sets a {@link ThemeResolver}.
	 *
	 * @param themeResolver the theme resolver
	 */
	void setThemeResolver(@Nullable ThemeResolver themeResolver);

	/**
	 * Sets a theme name to use.
	 *
	 * @param themeName the theme name
	 */
	void setThemeName(@Nullable String themeName);

}
