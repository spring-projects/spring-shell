/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.component;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;

import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.control.ViewDoneEvent;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.geom.Rectangle;
import org.springframework.util.Assert;

/**
 * Handles view execution in a non-fullscreen setup.
 *
 * @author Janne Valkealahti
 */
public class ViewComponent {

	private final Terminal terminal;
	private final View view;
	private EventLoop eventLoop;
	private TerminalUI ui;
	private boolean useTerminalWidth = true;

	/**
	 * Construct view component with a given {@link Terminal} and {@link View}.
	 *
	 * @param terminal the terminal
	 * @param view the main view
	 */
	public ViewComponent(Terminal terminal, View view) {
		Assert.notNull(terminal, "terminal must be set");
		Assert.notNull(view, "view must be set");
		this.terminal = terminal;
		this.view = view;
		this.ui = new TerminalUI(terminal);
		this.eventLoop = ui.getEventLoop();
	}

	/**
	 * Run a view execution loop.
	 */
	public void run() {
		eventLoop.onDestroy(eventLoop.viewEvents(ViewDoneEvent.class, view)
			.subscribe(event -> {
					exit();
				}
			));
		view.setEventLoop(eventLoop);
		Size terminalSize = terminal.getSize();
		Rectangle rect = view.getRect();
		if (useTerminalWidth) {
			view.setRect(rect.x(), rect.y(), terminalSize.getColumns() - rect.x(), rect.height());
		}
		ui.setRoot(view, false);
		ui.run();
	}

	/**
	 * Sets if full terminal width should be used for a view. Defaults to {@code true}.
	 *
	 * @param useTerminalWidth the use terminal width flag
	 */
	public void setUseTerminalWidth(boolean useTerminalWidth) {
		this.useTerminalWidth = useTerminalWidth;
	}

	/**
	 * Gets an {@link EventLoop} associated with this view component.
	 *
	 * @return event loop with this view component
	 */
	public EventLoop getEventLoop() {
		return eventLoop;
	}

	/**
	 * Request exit from an execution loop.
	 */
	public void exit() {
		eventLoop.dispatch(ShellMessageBuilder.ofInterrupt());
	}

}
