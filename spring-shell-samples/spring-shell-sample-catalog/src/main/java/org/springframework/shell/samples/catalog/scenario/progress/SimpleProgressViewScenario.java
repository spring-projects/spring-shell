/*
 * Copyright 2024 the original author or authors.
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
package org.springframework.shell.samples.catalog.scenario.progress;

import java.time.Duration;
import java.util.List;
import java.util.ListIterator;

import reactor.core.publisher.Flux;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.component.message.ShellMessageHeaderAccessor;
import org.springframework.shell.component.message.StaticShellMessageHeaderAccessor;
import org.springframework.shell.component.view.control.GridView;
import org.springframework.shell.component.view.control.ProgressView;
import org.springframework.shell.component.view.control.ProgressView.ProgressViewItem;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.samples.catalog.scenario.AbstractScenario;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;

@ScenarioComponent(name = "Simple progressview", description = "ProgressView sample", category = {
		Scenario.CATEGORY_PROGRESSVIEW })
public class SimpleProgressViewScenario extends AbstractScenario {

	@Override
	public ScenarioContext buildContext() {
		ContextData contextData = buildContextData();
		return ScenarioContext.of(contextData.view(), contextData.start(), contextData.stop());
	}

	private record ContextData(View view, List<ProgressView> progress, Runnable start, Runnable stop) {
	}

	private ContextData buildContextData() {
		List<ProgressView> views = List.of(plain(), spinnerOnly(), percentOnly(), spreadAlign());
		GridView grid = new GridView();
		grid.setShowBorders(true);
		configure(grid);
		int[] rows = new int[views.size()];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = 1;
		}
		grid.setRowSize(rows);
		grid.setColumnSize(0);

		ListIterator<ProgressView> iter = views.listIterator();
		while (iter.hasNext()) {
			ProgressView view = iter.next();
			configure(view);
			view.start();
			grid.addItem(view, iter.previousIndex(), 0, 1, 1, 0, 0);
		}

		EventLoop eventLoop = getEventloop();

		Flux<Message<?>> ticks = Flux.interval(Duration.ofMillis(500)).take(100).map(l -> {
			Message<Long> message = MessageBuilder
				.withPayload(l)
				.setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.USER)
				.setHeader("SimpleProgressViewScenario", "")
				.build();
			return message;
		});

		eventLoop.dispatch(ticks);

		eventLoop.onDestroy(eventLoop.events()
			.filter(m -> EventLoop.Type.USER.equals(StaticShellMessageHeaderAccessor.getEventType(m))
					&& m.getHeaders().containsKey("SimpleProgressViewScenario"))
			.subscribe(m -> {
				views.forEach(v -> {
					v.tickAdvance(1);
				});
			}));

		Runnable stop = () -> {
			views.forEach(v -> {
				v.stop();
			});
		};

		return new ContextData(grid, views, null, stop);
	}

	private ProgressView plain() {
		ProgressView view = new ProgressView();
		view.setDescription("desc");
		return view;
	}

	private ProgressView spinnerOnly() {
		ProgressView view = new ProgressView(ProgressViewItem.ofSpinner());
		return view;
	}

	private ProgressView percentOnly() {
		ProgressView view = new ProgressView(ProgressViewItem.ofPercent());
		return view;
	}

	private ProgressView spreadAlign() {
		ProgressView view = new ProgressView(ProgressViewItem.ofText(0, HorizontalAlign.LEFT),
				ProgressViewItem.ofSpinner(0, HorizontalAlign.CENTER),
				ProgressViewItem.ofPercent(0, HorizontalAlign.RIGHT));
		view.setDescription("desc");
		return view;
	}

}
