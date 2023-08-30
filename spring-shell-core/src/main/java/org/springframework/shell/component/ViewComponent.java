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
package org.springframework.shell.component;

import org.jline.terminal.Terminal;

import org.springframework.messaging.Message;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.control.ViewDoneEvent;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.message.ShellMessageBuilder;
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

	public ViewComponent(Terminal terminal, View view) {
		Assert.notNull(terminal, "terminal must be set");
		Assert.notNull(view, "view must be set");
		this.terminal = terminal;
		this.view = view;
	}

	/**
	 * Run a view execution loop.
	 */
	public void run() {
		TerminalUI ui = new TerminalUI(terminal);
		eventLoop = ui.getEventLoop();
		eventLoop.onDestroy(eventLoop.viewEvents(ViewDoneEvent.class, view)
			.subscribe(event -> {
					exit();
				}
			));
		view.setEventLoop(eventLoop);
		ui.setRoot(view, false);
		ui.run();
	}

	/**
	 * Request exit from an execution loop.
	 */
	public void exit() {
		if (eventLoop == null) {
			return;
		}
		Message<String> msg = ShellMessageBuilder.withPayload("int")
			.setEventType(EventLoop.Type.SYSTEM)
			.setPriority(0)
			.build();
		eventLoop.dispatch(msg);
	}

}
