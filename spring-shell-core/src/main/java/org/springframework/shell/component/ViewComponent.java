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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final static Logger log = LoggerFactory.getLogger(ViewComponent.class);
	private final Terminal terminal;
	private final View view;
	private EventLoop eventLoop;
	private TerminalUI terminalUI;
	private boolean useTerminalWidth = true;
	private ViewComponentExecutor viewComponentExecutor;

	/**
	 * Construct view component with a given {@link Terminal} and {@link View}.
	 *
	 * @param terminal the terminal
	 * @param view the main view
	 */
	public ViewComponent(TerminalUI terminalUI, Terminal terminal, ViewComponentExecutor viewComponentExecutor,
			View view) {
		Assert.notNull(terminalUI, "terminal ui must be set");
		Assert.notNull(terminal, "terminal must be set");
		Assert.notNull(view, "view must be set");
		this.terminalUI = terminalUI;
		this.terminal = terminal;
		this.view = view;
		this.viewComponentExecutor = viewComponentExecutor;
		this.eventLoop = terminalUI.getEventLoop();
		view.setEventLoop(this.eventLoop);
	}

	/**
	 * Run a component asyncronously. Returned state can be used to wait, cancel or
	 * see its completion status.
	 *
	 * @return run state
	 */
	public ViewComponentRun runAsync() {
		ViewComponentRun run = viewComponentExecutor.start(() -> {
			runBlocking();
		});
		return run;
	}

	/**
	 * Run a view execution loop.
	 */
	public void runBlocking() {
		log.debug("Start run()");
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
		terminalUI.setRoot(view, false);
		terminalUI.run();
		log.debug("End run()");
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

	/**
	 * Represent run state of an async run of a component.
	 */
	public interface ViewComponentRun {

		/**
		 * Await component termination.
		 */
		void await();

		/**
		 * Cancel component run.
		 */
		void cancel();

		/**
	     * Returns {@code true} if component run has completed.
		 *
	     * @return {@code true} if component run has completed
		 */
		boolean isDone();

	}

}
