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

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.control.ButtonView.ButtonViewSelectEvent;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.StyleSettings;

/**
 * {@code DialogView} is a {@link View} with border, number of buttons and area
 * for a generic content.
 *
 * @author Janne Valkealahti
 */
public class DialogView extends WindowView {

	private View content;
	private List<ButtonView> buttons;

	public DialogView() {
		this(null);
	}

	public DialogView(View content, ButtonView... buttons) {
		this(content, Arrays.asList(buttons));
	}

	public DialogView(View content, List<ButtonView> buttons) {
		this.content = content;
		this.buttons = buttons;
	}

	@Override
	public void setEventLoop(EventLoop eventLoop) {
		// TODO: should find better way to hook into eventloop
		super.setEventLoop(eventLoop);
		hookButtonEvents();
	}

	@Override
	protected String getBackgroundStyle() {
		return StyleSettings.TAG_DIALOG_BACKGROUND;
	}

	@Override
	public void setLayer(int index) {
		if (content != null) {
			content.setLayer(index);
		}
		buttons.forEach(b -> b.setLayer(index + 1));
		super.setLayer(index);
	}

	private void hookButtonEvents() {
		buttons.forEach(b -> {
			onDestroy(getEventLoop().viewEvents(ButtonViewSelectEvent.class, b)
				.subscribe(event -> {
					dispatch();
					ViewService viewService = getViewService();
					if (viewService != null) {
						viewService.setModal(null);
					}
				}));
		});
	}

	@Override
	public MouseHandler getMouseHandler() {
		return args -> {
			View focus = null;
			for (ButtonView b : buttons) {
				MouseHandlerResult r = b.getMouseHandler().handle(args);
				if (r.focus() != null) {
					focus = r.focus();
					break;
				}
			}
			return MouseHandler.resultOf(args.event(), true, focus, null);
		};
	}

	@Override
	public void setRect(int x, int y, int width, int height) {
		super.setRect(x, y, width, height);

		Rectangle rect = getInnerRect();
		rect = new Rectangle(rect.x() + 1, rect.y() + 1, rect.width() - 2, rect.height() - 2);
		if (content != null) {
			content.setRect(rect.x(), rect.y(), rect.width(), rect.height() - 3);
		}
		int xx = rect.x();
		ListIterator<ButtonView> iter = buttons.listIterator();
		while (iter.hasNext()) {
			ButtonView button = iter.next();
			button.setRect(xx, rect.y() + rect.height() - 3, 7, 3);
			xx += 7;
		}
	}

	@Override
	protected void drawInternal(Screen screen) {
		Rectangle rect = getInnerRect();
		Writer writer = screen.writerBuilder().layer(getLayer()).build();
		writer.border(rect.x(), rect.y(), rect.width(), rect.height());
		if (content != null) {
			content.draw(screen);
		}
		ListIterator<ButtonView> iter = buttons.listIterator();
		while (iter.hasNext()) {
			ButtonView button = iter.next();
			button.draw(screen);
		}

		super.drawInternal(screen);
	}

	private void dispatch() {
		dispatch(ShellMessageBuilder.ofView(this, DialogViewCloseEvent.of(this)));
	}

	public record DialogViewItemEventArgs() implements ViewEventArgs {

		public static DialogViewItemEventArgs of() {
			return new DialogViewItemEventArgs();
		}
	}

	public record DialogViewCloseEvent(View view, DialogViewItemEventArgs args) implements ViewEvent {

		public static DialogViewCloseEvent of(View view) {
			return new DialogViewCloseEvent(view, DialogViewItemEventArgs.of());
		}
	}

}
