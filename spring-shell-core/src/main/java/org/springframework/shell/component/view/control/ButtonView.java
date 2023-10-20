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

import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Dimension;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.style.StyleSettings;

/**
 * {@code ButtonView} is a {@link View} with border and text acting as a button.
 *
 * @author Janne Valkealahti
 */
public class ButtonView extends BoxView {

	private String text;
	private Runnable action;

	public ButtonView() {
		this(null, null);
	}

	public ButtonView(String text) {
		this(text, null);
	}

	public ButtonView(String text, Runnable action) {
		this.text = text;
		this.action = action;
	}

	@Override
	protected void initInternal() {
		registerKeyBinding(Key.Enter, () -> keySelect());
		registerMouseBinding(MouseEvent.Type.Released | MouseEvent.Button.Button1, event -> mouseSelect(event));
	}

	@Override
	public KeyHandler getKeyHandler() {
		return super.getKeyHandler();
	}

	@Override
	public MouseHandler getMouseHandler() {
		return super.getMouseHandler();
	}

	@Override
	protected String getBackgroundStyle() {
		return StyleSettings.TAG_BUTTON_BACKGROUND;
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		Writer writer = screen.writerBuilder().layer(getLayer()).build();
		if (text != null) {
			writer.border(rect.x(), rect.y(), rect.width(), rect.height());
			writer.text(text, rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		}

		super.drawInternal(screen);
	}

	public Dimension getPreferredDimension() {
		return null;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setAction(Runnable action) {
		this.action = action;
	}

	private void keySelect() {
		dispatch();
	}

	private void mouseSelect(MouseEvent event) {
		dispatch();
	}

	private void dispatch() {
		dispatch(ShellMessageBuilder.ofView(this, ButtonViewSelectEvent.of(this)));
		if (action != null) {
			dispatchRunnable(action);
		}
	}

	public record ButtonViewItemEventArgs() implements ViewEventArgs {

		public static ButtonViewItemEventArgs of() {
			return new ButtonViewItemEventArgs();
		}
	}

	public record ButtonViewSelectEvent(View view, ButtonViewItemEventArgs args) implements ViewEvent {

		public static ButtonViewSelectEvent of(View view) {
			return new ButtonViewSelectEvent(view, ButtonViewItemEventArgs.of());
		}
	}

}
