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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.message.ShellMessageBuilder;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;
import org.springframework.shell.style.ThemeResolver;
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
	private final static Logger log = LoggerFactory.getLogger(Catalog.class);

	// mapping from category name to scenarios(can belong to multiple categories)
	private final Map<String, List<ScenarioData>> categoryMap = new TreeMap<>();
	private final Terminal terminal;
	private View currentScenarioView = null;
	private TerminalUI ui;
	private ListView<String> categories;
	private ListView<ScenarioData> scenarios;
	private AppView app;
	private EventLoop eventLoop;
	private ThemeResolver themeResolver;
	private String activeThemeName = "default";

	public Catalog(Terminal terminal, List<Scenario> scenarios, ThemeResolver themeResolver) {
		this.terminal = terminal;
		this.themeResolver = themeResolver;
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
		app = buildScenarioBrowser(eventLoop, ui);
		app.setThemeResolver(themeResolver);
		app.setThemeName(activeThemeName);

		// handle logic to switch between main scenario browser
		// and currently active scenario
		eventLoop.onDestroy(eventLoop.keyEvents()
			.doOnNext(m -> {
				if (m.getPlainKey() == Key.q && m.hasCtrl()) {
					if (currentScenarioView != null) {
						currentScenarioView = null;
						ui.setRoot(app, true);
						ui.setFocus(categories);
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
		// category selector on left, scenario selector on right
		GridView grid = new GridView();
		grid.setThemeResolver(themeResolver);
		grid.setThemeName(activeThemeName);
		grid.setEventLoop(eventLoop);
		grid.setRowSize(0);
		grid.setColumnSize(30, 0);

		categories = buildCategorySelector(eventLoop);
		scenarios = buildScenarioSelector(eventLoop);

		grid.addItem(categories, 0, 0, 1, 1, 0, 0);
		grid.addItem(scenarios, 0, 1, 1, 1, 0, 0);

		MenuBarView menuBar = buildMenuBar(eventLoop);
		StatusBarView statusBar = buildStatusBar(eventLoop);

		// we use main app view to represent scenario browser
		AppView app = new AppView(grid, menuBar, statusBar);
		app.setThemeResolver(themeResolver);
		app.setThemeName(activeThemeName);
		app.setEventLoop(eventLoop);

		// handle event when scenario is chosen
		eventLoop.onDestroy(eventLoop.viewEvents(LISTVIEW_SCENARIO_TYPEREF, scenarios)
			.subscribe(event -> {
				View view = event.args().item().scenario()
					.configure(eventLoop, themeResolver, activeThemeName)
					.build();
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

		// we could potentially do keybinding somewhere else
		// but at least this shows how to do it in low level.
		// essentially we now just handle F10 to toggle
		// menubar visibility
		// TODO: when we get support for hotkeys we should do
		//       binding there
		eventLoop.onDestroy(eventLoop.keyEvents()
			.subscribe(event -> {
					log.debug("Raw keyevent {}", event);
					if (event.isKey(KeyEvent.Key.f10)) {
						app.toggleStatusBarVisibility();
					}
				}
			));

		return app;
	}

	private ListView<String> buildCategorySelector(EventLoop eventLoop) {
		ListView<String> categories = new ListView<>();
		categories.setThemeResolver(themeResolver);
		categories.setThemeName(activeThemeName);
		categories.setEventLoop(eventLoop);
		List<String> items = List.copyOf(categoryMap.keySet());
		categories.setItems(items);
		categories.setTitle("Categories");
		categories.setFocusedTitleStyle(ScreenItem.STYLE_BOLD);
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
		scenarios.setThemeResolver(themeResolver);
		scenarios.setThemeName(activeThemeName);
		scenarios.setEventLoop(eventLoop);
		scenarios.setTitle("Scenarios");
		scenarios.setFocusedTitleStyle(ScreenItem.STYLE_BOLD);
		scenarios.setShowBorder(true);
		scenarios.setCellFactory(list -> new ScenarioListCell());
		return scenarios;
	}

	private void setStyle(String name) {
		log.debug("Setting active theme name {}", name);
		activeThemeName = name;
		scenarios.setThemeName(activeThemeName);
		categories.setThemeName(activeThemeName);
		app.setThemeName(activeThemeName);
	}

	private Runnable styleAction(String style) {
		return () -> setStyle(style);
	}

	private MenuBarView buildMenuBar(EventLoop eventLoop) {
		Runnable quitAction = () -> requestQuit();
		MenuItem[] themeItems = themeResolver.themeNames().stream()
			.map(tn -> {
				return MenuItem.of(tn, MenuItemCheckStyle.RADIO, styleAction(tn), "default".equals(tn));
			})
			.toArray(MenuItem[]::new);
		MenuBarView menuBar = MenuBarView.of(
			MenuBarItem.of("File",
				MenuItem.of("Quit", MenuItemCheckStyle.NOCHECK, quitAction)),
			MenuBarItem.of("Theme",
				themeItems),
			MenuBarItem.of("Help",
				MenuItem.of("About"))
		);

		menuBar.setThemeResolver(themeResolver);
		menuBar.setThemeName(activeThemeName);
		menuBar.setEventLoop(eventLoop);
		return menuBar;
	}

	private StatusBarView buildStatusBar(EventLoop eventLoop) {
		Runnable quitAction = () -> requestQuit();
		StatusBarView statusBar = new StatusBarView();
		statusBar.setThemeResolver(themeResolver);
		statusBar.setThemeName(activeThemeName);
		statusBar.setEventLoop(eventLoop);
		StatusItem item1 = new StatusBarView.StatusItem("CTRL-Q Quit", quitAction);
		StatusItem item2 = new StatusBarView.StatusItem("F10 Status Bar");
		statusBar.setItems(Arrays.asList(item1, item2));
		return statusBar;
	}

	private record ScenarioData(Scenario scenario, String name, String description, String[] category){};

}
