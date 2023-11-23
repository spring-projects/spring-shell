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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.TerminalUIBuilder;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.DialogView;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;

class TerminalUiSnippets {

	class SampleIntro {

		// tag::introsample[]
		@Autowired
		TerminalUIBuilder builder;

		void sample() {
			TerminalUI ui = builder.build();
			BoxView view = new BoxView();
			ui.configure(view);
			view.setDrawFunction((screen, rect) -> {
				screen.writerBuilder()
					.build()
					.text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
				return rect;
			});
			ui.setRoot(view, true);
			ui.run();
		}
		// end::introsample[]
	}

	class Sample3 {

		// tag::exitingfromloop[]
		@Autowired
		Terminal terminal;

		void sample() {
			TerminalUI ui = new TerminalUI(terminal);
			BoxView view = new BoxView();
			ui.configure(view);
			ui.setRoot(view, true);
			EventLoop eventLoop = ui.getEventLoop();
			eventLoop.keyEvents()
				.subscribe(event -> {
					if (event.getPlainKey() == Key.q && event.hasCtrl()) {
						eventLoop.dispatch(ShellMessageBuilder.ofInterrupt());
					}
				});
			ui.run();
		}
		// end::exitingfromloop[]
	}

	@SuppressWarnings("unused")
	class SampleUiAutowire {

		// tag::uibuilderautowire[]
		@Autowired
		TerminalUIBuilder builder;

		void sample() {
			TerminalUI ui = builder.build();
			// do something with ui
		}
		// end::uibuilderautowire[]
	}

	class SampleConfigureView {

		// tag::configureview[]
		TerminalUI ui;

		void sample() {
			BoxView view = new BoxView();
			ui.configure(view);
		}
		// end::configureview[]
	}

	class SampleUiLoop {

		// tag::uirun[]
		TerminalUI ui;

		void sample() {
			ui.run();
		}
		// end::uirun[]
	}

	class SampleUiModal {

		// tag::uimodal[]
		TerminalUI ui;

		void sample() {
			DialogView dialog = new DialogView();
			// set modal
			ui.setModal(dialog);
			// clear modal
			ui.setModal(null);
		}
		// end::uimodal[]
	}

}
