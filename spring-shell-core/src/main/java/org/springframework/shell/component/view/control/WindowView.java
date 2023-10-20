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

import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.StyleSettings;


/**
 * {@code WindowView} is a {@link View} defining area within view itself.
 *
 * @author Janne Valkealahti
 */
public class WindowView extends AbstractView {

	private int backgroundColor = -1;
	private int minWidth = 30;
	private int maxWidth = 60;
	private int minHeight = 8;
	private int maxHeight = 12;

	/**
	 * Sets a background color. If color is set to {@code null} it indicates that
	 * background should be set to be {@code empty} causing possible layer to be
	 * non-transparent.
	 *
	 * @param backgroundColor the background color
	 */
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	protected String getBackgroundStyle() {
		return StyleSettings.TAG_BACKGROUND;
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		int bgColor = resolveThemeBackground(getBackgroundStyle(), backgroundColor, -1);
		screen.writerBuilder().layer(getLayer()).build().background(rect, bgColor);
	}

	/**
	 * Gets an inner rectangle of this view.
	 *
	 * @return an inner rectangle of this view
	 */
	protected Rectangle getInnerRect() {
		Rectangle rect = getRect();

		int w;
		int x;
		if (rect.width() <= minWidth) {
			w = rect.width();
			x = rect.x();
		}
		else if (rect.width() >= maxWidth) {
			w = maxWidth;
			x = (rect.width() - w) / 2;
		}
		else {
			w = minWidth;
			x = (rect.width() - w) / 2;
		}

		int h;
		int y;
		if (rect.height() <= minHeight) {
			h = rect.height();
			y = rect.y();
		}
		else if (rect.height() >= maxHeight) {
			h = maxHeight;
			y = (rect.height() - h) / 2;
		}
		else {
			h = rect.height();
			y = (rect.height() - h) / 2;
		}

		rect = new Rectangle(x, y, w, h);
		return rect;
	}

}
