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

import org.springframework.shell.component.view.control.AbstractControl;
import org.springframework.shell.component.view.screen.Screen;

/**
 * Base implementation of a {@link Cell}.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractCell<T> extends AbstractControl implements Cell<T> {

	private T item;
	private int style = -1;
	private int foregroundColor = -1;
	private int backgroundColor = -1;

	public AbstractCell(T item) {
		this.item = item;
	}

	@Override
	public T getItem() {
		return item;
	}

	@Override
	public void setItem(T item) {
		this.item = item;
	}

	@Override
	public void setStyle(int style) {
		this.style = style;
	}

	@Override
	public void setForegroundColor(int foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	@Override
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getStyle() {
		return style;
	}

	public int getForegroundColor() {
		return foregroundColor;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public void draw(Screen screen) {
		drawBackground(screen);
		drawContent(screen);
	}

	protected void drawBackground(Screen screen) {
	}

	protected void drawContent(Screen screen) {
	}

}
