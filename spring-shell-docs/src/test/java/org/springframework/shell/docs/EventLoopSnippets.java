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
package org.springframework.shell.docs;

import org.jline.terminal.Terminal;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.event.EventLoop;

class EventLoopSnippets {

	class Dump1 {

		@Autowired
		Terminal terminal;

		void events() {
			// tag::plainevents[]
			TerminalUI ui = new TerminalUI(terminal);
			EventLoop eventLoop = ui.getEventLoop();
			Flux<? extends Message<?>> events = eventLoop.events();
			events.subscribe();
			// end::plainevents[]
		}

		void keyEvents() {
			// tag::keyevents[]
			TerminalUI ui = new TerminalUI(terminal);
			EventLoop eventLoop = ui.getEventLoop();
			eventLoop.keyEvents()
				.doOnNext(event -> {
					// do something with key event
				})
				.subscribe();
			// end::keyevents[]
		}

		void onDestroy() {
			// tag::ondestroy[]
			TerminalUI ui = new TerminalUI(terminal);
			EventLoop eventLoop = ui.getEventLoop();
			eventLoop.onDestroy(eventLoop.events().subscribe());
			// end::ondestroy[]
		}
	}

}
