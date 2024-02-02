/*
 * Copyright 2024 the original author or authors.
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

import java.util.function.Function;

import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.style.StyleSettings;

/**
 * Base implementation of a {@link TextCell}.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractTextCell<T> extends AbstractCell<T> implements TextCell<T> {

	private Function<T, String> itemFunction;
	private HorizontalAlign hAlign = HorizontalAlign.CENTER;
	private VerticalAlign vAlign = VerticalAlign.CENTER;

	public AbstractTextCell(T item, Function<T, String> itemFunction) {
		this(item, itemFunction, HorizontalAlign.CENTER, VerticalAlign.CENTER);
	}

	public AbstractTextCell(T item, Function<T, String> itemFunction, HorizontalAlign hAlign, VerticalAlign vAlign) {
		super(item);
		this.itemFunction = itemFunction;
		this.hAlign = hAlign;
		this.vAlign = vAlign;
	}

	@Override
	public void setHorizontalAlign(HorizontalAlign hAlign) {
		this.hAlign = hAlign;
	}

	@Override
	public void setVerticalAlign(VerticalAlign vAlign) {
		this.vAlign = vAlign;
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
		String text = itemFunction.apply(getItem());
		if (text != null) {
			Rectangle rect = getRect();
			Writer writer = screen.writerBuilder().style(getStyle()).color(getForegroundColor()).build();
			writer.text(text, rect, hAlign, vAlign);
		}
	}

}
