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
package org.springframework.shell.samples.catalog.scenario.other;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import reactor.core.publisher.Flux;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.message.ShellMessageHeaderAccessor;
import org.springframework.shell.component.message.StaticShellMessageHeaderAccessor;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.samples.catalog.scenario.AbstractScenario;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;

@ScenarioComponent(name = "Clock", description = "Showing time ticks", category = { Scenario.CATEGORY_OTHER })
public class ClockScenario extends AbstractScenario {

	@Override
	public View build() {
		// simply use a plain box view to draw a date using a custom
		// draw function
		BoxView root = new BoxView();
		root.setTitle("What's o'clock");
		root.setShowBorder(true);

		// store text to print
		AtomicReference<String> ref = new AtomicReference<>();

		// dispatch dates as messages
		Flux<Message<?>> dates = Flux.interval(Duration.ofSeconds(1)).map(l -> {
			String date = new Date().toString();
			Message<String> message = MessageBuilder
				.withPayload(date)
				.setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.USER)
				.build();
			return message;
		});
		getEventloop().dispatch(dates);

		// process dates
		getEventloop().onDestroy(getEventloop().events()
			.filter(m -> EventLoop.Type.USER.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.subscribe(m -> {
				if (m.getPayload() instanceof String s) {
					ref.set(s);
					getEventloop().dispatch(ShellMessageBuilder.ofRedraw());
				}
			}));

		// testing for animations for now
		AtomicInteger animX = new AtomicInteger();
		getEventloop().onDestroy(getEventloop().events()
			.filter(m -> EventLoop.Type.SYSTEM.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.filter(m -> m.getHeaders().containsKey("animationtick"))
			.subscribe(m -> {
				Object payload = m.getPayload();
				if (payload instanceof Integer i) {
					animX.set(i);
					getEventloop().dispatch(ShellMessageBuilder.ofRedraw());
				}
			}));

		AtomicReference<HorizontalAlign> hAlign = new AtomicReference<>(HorizontalAlign.CENTER);
		AtomicReference<VerticalAlign> vAlign = new AtomicReference<>(VerticalAlign.CENTER);

		getEventloop().onDestroy(getEventloop().keyEvents()
			.subscribe(event -> {
				switch (event.key()) {
					case Key.CursorDown -> {
						if (vAlign.get() == VerticalAlign.TOP) {
							vAlign.set(VerticalAlign.CENTER);
						}
						else if (vAlign.get() == VerticalAlign.CENTER) {
							vAlign.set(VerticalAlign.BOTTOM);
						}
					}
					case Key.CursorUp -> {
						if (vAlign.get() == VerticalAlign.BOTTOM) {
							vAlign.set(VerticalAlign.CENTER);
						}
						else if (vAlign.get() == VerticalAlign.CENTER) {
							vAlign.set(VerticalAlign.TOP);
						}
					}
					case Key.CursorLeft -> {
						if (hAlign.get() == HorizontalAlign.RIGHT) {
							hAlign.set(HorizontalAlign.CENTER);
						}
						else if (hAlign.get() == HorizontalAlign.CENTER) {
							hAlign.set(HorizontalAlign.LEFT);
						}
					}
					case Key.CursorRight -> {
						Message<String> animStart = MessageBuilder
							.withPayload("")
							.setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.SYSTEM)
							.setHeader("animationstart", true)
							.build();
						getEventloop().dispatch(animStart);
					}
				};

			}));

		// draw current date
		root.setDrawFunction((screen, rect) -> {
			int a = animX.get();
			Rectangle r = new Rectangle(rect.x() + 1 + a, rect.y() + 1, rect.width() - 2, rect.height() - 2);
			// Rectangle r = new View.Rectangle(rect.x() + 1, rect.y() + 1, rect.width() - 2, rect.height() - 2);
			String s = ref.get();
			if (s != null) {
				screen.writerBuilder().build().text(s, r, hAlign.get(), vAlign.get());
			}
			return rect;
		});
		return root;
	}
}
