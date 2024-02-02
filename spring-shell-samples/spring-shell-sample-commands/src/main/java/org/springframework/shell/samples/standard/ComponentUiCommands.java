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
package org.springframework.shell.samples.standard;

import java.time.Duration;

import reactor.core.publisher.Flux;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.ViewComponent;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.message.ShellMessageHeaderAccessor;
import org.springframework.shell.component.message.StaticShellMessageHeaderAccessor;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.InputView;
import org.springframework.shell.component.view.control.ProgressView;
import org.springframework.shell.component.view.control.ProgressView.ProgressViewItem;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.standard.AbstractShellComponent;

@Command
public class ComponentUiCommands extends AbstractShellComponent {

	@Command(command = "componentui tui1")
	public void tui1() {
		TerminalUI ui = new TerminalUI(getTerminal());
		BoxView view = new BoxView();
		view.setShowBorder(true);
		view.setDrawFunction((screen, rect) -> {
			screen.writerBuilder()
				.build()
				.text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
			return rect;
		});
		ui.setRoot(view, true);
		ui.run();
	}

	@Command(command = "componentui tui2")
	public void tui2() {
		TerminalUI ui = new TerminalUI(getTerminal());
		BoxView view = new BoxView();
		view.setRect(0, 0, 40, 5);
		view.setShowBorder(true);
		view.setDrawFunction((screen, rect) -> {
			screen.writerBuilder()
				.build()
				.text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
			return rect;
		});
		ui.setRoot(view, false);
		ui.run();
	}

	@Command(command = "componentui tui3")
	public void tui3() {
		TerminalUI ui = new TerminalUI(getTerminal());
		BoxView view = new BoxView();
		view.setShowBorder(true);
		view.setDrawFunction((screen, rect) -> {
			screen.writerBuilder()
				.build()
				.text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
			return rect;
		});
		ui.setRoot(view, false);
		ui.run();
	}

	@Command(command = "componentui string")
	public String stringInput() {
		InputView view = new InputView();
		view.setRect(0, 0, 10, 1);
		ViewComponent component = new ViewComponent(getTerminal(), view);
		component.run();
		String input = view.getInputText();
		return String.format("Input was '%s'", input);
	}

	@Command(command = "componentui progress1")
	public void progress1() {
		ProgressView view = new ProgressView();
		view.setDescription("name");
		view.setRect(0, 0, 20, 1);


		ViewComponent component = new ViewComponent(getTerminal(), view);
		EventLoop eventLoop = component.getEventLoop();

		Flux<Message<?>> ticks = Flux.interval(Duration.ofMillis(100)).map(l -> {
			Message<Long> message = MessageBuilder
				.withPayload(l)
				.setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.USER)
				.build();
			return message;
		});
		eventLoop.dispatch(ticks);

		eventLoop.onDestroy(eventLoop.events()
			.filter(m -> EventLoop.Type.USER.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.subscribe(m -> {
				if (m.getPayload() instanceof Long) {
					view.tickAdvance(5);
					eventLoop.dispatch(ShellMessageBuilder.ofRedraw());
				}
			}));


		component.run();
	}

	@Command(command = "componentui progress2")
	public void progress2() {
		ProgressView view = new ProgressView(0, 100, ProgressViewItem.ofText(10, HorizontalAlign.LEFT),
				ProgressViewItem.ofSpinner(3, HorizontalAlign.LEFT),
				ProgressViewItem.ofPercent(0, HorizontalAlign.RIGHT));
		view.setDescription("name");
		view.setRect(0, 0, 20, 1);


		ViewComponent component = new ViewComponent(getTerminal(), view);
		EventLoop eventLoop = component.getEventLoop();

		Flux<Message<?>> ticks = Flux.interval(Duration.ofMillis(100)).map(l -> {
			Message<Long> message = MessageBuilder
				.withPayload(l)
				.setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.USER)
				.build();
			return message;
		});
		eventLoop.dispatch(ticks);

		eventLoop.onDestroy(eventLoop.events()
			.filter(m -> EventLoop.Type.USER.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.subscribe(m -> {
				if (m.getPayload() instanceof Long) {
					view.tickAdvance(5);
					eventLoop.dispatch(ShellMessageBuilder.ofRedraw());
				}
			}));


		component.run();
	}

}
