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
package org.springframework.shell.samples.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jline.terminal.Terminal;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.Message;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.AppView;
import org.springframework.shell.component.view.control.AppView.AppViewEvent;
import org.springframework.shell.component.view.control.GridView;
import org.springframework.shell.component.view.control.ListView;
import org.springframework.shell.component.view.control.ListView.ListViewOpenSelectedItemEvent;
import org.springframework.shell.component.view.control.ListView.ListViewSelectedItemChangedEvent;
import org.springframework.shell.component.view.control.MenuBarView;
import org.springframework.shell.component.view.control.MenuBarView.MenuBarItem;
import org.springframework.shell.component.view.control.MenuView.MenuItem;
import org.springframework.shell.component.view.control.MenuView.MenuItemCheckStyle;
import org.springframework.shell.component.view.control.StatusBarView;
import org.springframework.shell.component.view.control.StatusBarView.StatusItem;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.control.cell.ListCell;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.message.ShellMessageBuilder;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Catalog app logic. Builds a simple application ui where scenarios can be
 * selected and run.
 *
 * @author Janne Valkealahti
 */
public class Catalog {

	// ref types helping with deep nested generics from events
	private final static ParameterizedTypeReference<ListViewOpenSelectedItemEvent<ScenarioData>> LISTVIEW_SCENARIO_TYPEREF
		= new ParameterizedTypeReference<ListViewOpenSelectedItemEvent<ScenarioData>>() {};
	private final static ParameterizedTypeReference<ListViewSelectedItemChangedEvent<String>> LISTVIEW_STRING_TYPEREF
		= new ParameterizedTypeReference<ListViewSelectedItemChangedEvent<String>>() {};

	// mapping from category name to scenarios(can belong to multiple categories)
	private final Map<String, List<ScenarioData>> categoryMap = new TreeMap<>();
	private final Terminal terminal;
	private View currentScenarioView = null;
	private TerminalUI ui;
	private ListView<String> categories;
	private EventLoop eventLoop;

	public Catalog(Terminal terminal, List<Scenario> scenarios) {
		this.terminal = terminal;
		mapScenarios(scenarios);
	}

	private void mapScenarios(List<Scenario> scenarios) {
		// we blindly expect scenario to have ScenarioComponent annotation with all fields
		scenarios.forEach(sce -> {
			ScenarioComponent ann = AnnotationUtils.findAnnotation(sce.getClass(), ScenarioComponent.class);
			if (ann != null) {
				String name = ann.name();
				String description = ann.description();
				String[] category = ann.category();
				if (StringUtils.hasText(name) && StringUtils.hasText(description) && !ObjectUtils.isEmpty(category)) {
					for (String cat : category) {
						ScenarioData scenarioData = new ScenarioData(sce, name, description, category);
						categoryMap.computeIfAbsent(Scenario.CATEGORY_ALL, key -> new ArrayList<>()).add(scenarioData);
						categoryMap.computeIfAbsent(cat, key -> new ArrayList<>()).add(scenarioData);
					}
				}
			}
		});
	}

	private void requestQuit() {
		Message<String> msg = ShellMessageBuilder.withPayload("int")
			.setEventType(EventLoop.Type.SYSTEM)
			.setPriority(0)
			.build();
		eventLoop.dispatch(msg);
	}

	/**
	 * Main run loop. Builds the ui and exits when user requests exit.
	 */
	public void run() {
		ui = new TerminalUI(terminal);
		eventLoop = ui.getEventLoop();
		AppView app = buildScenarioBrowser(eventLoop, ui);

		// handle logic to switch between main scenario browser
		// and currently active scenario
		eventLoop.onDestroy(eventLoop.keyEvents()
			.doOnNext(m -> {
				if (m.getPlainKey() == Key.q && m.hasCtrl()) {
					if (currentScenarioView != null) {
						currentScenarioView = null;
						ui.setRoot(app, true);
					}
					else {
						requestQuit();
					}
				}
			})
			.subscribe());

		// start main scenario browser
		ui.setRoot(app, true);
		ui.setFocus(categories);
		categories.setSelected(0);
		ui.run();
	}

	private AppView buildScenarioBrowser(EventLoop eventLoop, TerminalUI component) {
		// we use main app view to represent scenario browser
		AppView app = new AppView();
		app.setEventLoop(eventLoop);

		// category selector on left, scenario selector on right
		GridView grid = new GridView();
		grid.setRowSize(1, 0, 1);
		grid.setColumnSize(30, 0);

		categories = buildCategorySelector(eventLoop);
		ListView<ScenarioData> scenarios = buildScenarioSelector(eventLoop);

		// handle event when scenario is chosen
		eventLoop.onDestroy(eventLoop.viewEvents(LISTVIEW_SCENARIO_TYPEREF, scenarios)
			.subscribe(event -> {
				View view = event.args().item().scenario().configure(eventLoop).build();
				component.setRoot(view, true);
				currentScenarioView = view;
			}));


		// handle event when category selection is changed
		eventLoop.onDestroy(eventLoop.viewEvents(LISTVIEW_STRING_TYPEREF, categories)
			.subscribe(event -> {
				if (event.args().item() != null) {
					String selected = event.args().item();
					List<ScenarioData> list = categoryMap.get(selected);
					scenarios.setItems(list);
				}
			}));

		// handle focus change between lists
		eventLoop.onDestroy(eventLoop.viewEvents(AppViewEvent.class, app)
			.subscribe(event -> {
					switch (event.args().direction()) {
						case NEXT -> ui.setFocus(scenarios);
						case PREVIOUS -> ui.setFocus(categories);
					}
				}
			));

		// We place statusbar below categories and scenarios
		MenuBarView menuBar = buildMenuBar(eventLoop);
		StatusBarView statusBar = buildStatusBar(eventLoop);
		grid.addItem(menuBar, 0, 0, 1, 2, 0, 0);
		grid.addItem(categories, 1, 0, 1, 1, 0, 0);
		grid.addItem(scenarios, 1, 1, 1, 1, 0, 0);
		grid.addItem(statusBar, 2, 0, 1, 2, 0, 0);
		app.setMain(grid);
		return app;
	}

	private ListView<String> buildCategorySelector(EventLoop eventLoop) {
		ListView<String> categories = new ListView<>();
		categories.setEventLoop(eventLoop);
		List<String> items = List.copyOf(categoryMap.keySet());
		categories.setItems(items);
		categories.setTitle("Categories");
		categories.setShowBorder(true);
		return categories;
	}

	private static class ScenarioListCell extends ListCell<ScenarioData> {

		@Override
		public void draw(Screen screen) {
			Rectangle rect = getRect();
			Writer writer = screen.writerBuilder().style(getStyle()).build();
			writer.text(String.format("%-20s %s", getItem().name(), getItem().description()), rect.x(), rect.y());
			writer.background(rect, getBackgroundColor());
		}
	}

	private ListView<ScenarioData> buildScenarioSelector(EventLoop eventLoop) {
		ListView<ScenarioData> scenarios = new ListView<>();
		scenarios.setEventLoop(eventLoop);
		scenarios.setTitle("Scenarios");
		scenarios.setShowBorder(true);
		scenarios.setCellFactory(list -> new ScenarioListCell());
		return scenarios;
	}

	private MenuBarView buildMenuBar(EventLoop eventLoop) {
		Runnable quitAction = () -> requestQuit();
		MenuBarView menuBar = MenuBarView.of(
			MenuBarItem.of("File",
				MenuItem.of("Quit", MenuItemCheckStyle.NOCHECK, quitAction)),
			MenuBarItem.of("Theme",
				MenuItem.of("Dump", MenuItemCheckStyle.RADIO),
				MenuItem.of("Funky", MenuItemCheckStyle.RADIO)
			),
			MenuBarItem.of("Help",
				MenuItem.of("About"))
		);

		menuBar.setEventLoop(eventLoop);
		return menuBar;
	}

	private StatusBarView buildStatusBar(EventLoop eventLoop) {
		Runnable quitAction = () -> requestQuit();
		StatusBarView statusBar = new StatusBarView();
		statusBar.setEventLoop(eventLoop);
		StatusItem item1 = new StatusBarView.StatusItem("CTRL-Q Quit", quitAction);
		StatusItem item2 = new StatusBarView.StatusItem("F10 Status Bar");
		statusBar.setItems(Arrays.asList(item1, item2));
		return statusBar;
	}

	private record ScenarioData(Scenario scenario, String name, String description, String[] category){};

}
