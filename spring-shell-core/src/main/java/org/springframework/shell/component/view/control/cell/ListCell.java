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

import org.springframework.shell.component.view.control.Cell;
import org.springframework.shell.component.view.control.ListView;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;

/**
 * The {@link Cell} type used within {@link ListView} instances
 *
 * @author Janne Valkealahti
 */
public class ListCell<T> extends AbstractCell<T> {

	protected String text;

	@Override
	public void draw(Screen screen) {
		Rectangle rect = getRect();
		Writer writer = screen.writerBuilder().style(getStyle()).color(getForegroundColor()).build();
		writer.text(text, rect.x(), rect.y());
		writer.background(rect, getBackgroundColor());
	}

	public void updateItem(T item) {
		setItem(item);
		this.text = item.toString();
	}

}
