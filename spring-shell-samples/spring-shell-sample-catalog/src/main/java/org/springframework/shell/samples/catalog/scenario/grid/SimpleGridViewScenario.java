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
package org.springframework.shell.samples.catalog.scenario.grid;

import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.GridView;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.screen.Color;
import org.springframework.shell.samples.catalog.scenario.AbstractScenario;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;

@ScenarioComponent(name = "Simple gridview", description = "GridView sample", category = { Scenario.CATEGORY_LAYOUT })
public class SimpleGridViewScenario extends AbstractScenario {

	@Override
	public View build() {
		BoxView menu = new BoxView();
		configure(menu);
		menu.setBackgroundColor(Color.KHAKI4);
		menu.setTitle("Menu");
		menu.setShowBorder(true);

		BoxView main = new BoxView();
		configure(main);
		main.setBackgroundColor(Color.KHAKI4);
		main.setTitle("Main");
		main.setShowBorder(true);

		BoxView sideBar = new BoxView();
		configure(sideBar);
		sideBar.setBackgroundColor(Color.KHAKI4);
		sideBar.setTitle("Sidebar");
		sideBar.setShowBorder(true);

		BoxView header = new BoxView();
		configure(header);
		header.setBackgroundColor(Color.KHAKI4);
		header.setTitle("Header");
		header.setShowBorder(true);

		BoxView footer = new BoxView();
		configure(footer);
		footer.setBackgroundColor(Color.KHAKI4);
		footer.setTitle("Footer");
		footer.setShowBorder(true);

		GridView grid = new GridView();
		configure(grid);
		grid.setBackgroundColor(Color.KHAKI3);
		grid.setTitle("Grid");
		grid.setShowBorder(true);

		grid.setRowSize(3, 0, 3);
		grid.setColumnSize(30, 0, 30);
		// grid.setShowBorder(true);
		grid.setShowBorders(true);
		grid.addItem(header, 0, 0, 1, 3, 0, 0);
		grid.addItem(footer, 2, 0, 1, 3, 0, 0);

		grid.addItem(menu, 0, 0, 0, 0, 0, 0);
		grid.addItem(main, 1, 0, 1, 3, 0, 0);
		grid.addItem(sideBar, 0, 0, 0, 0, 0, 0);

		grid.addItem(menu, 1, 0, 1, 1, 0, 100);
		grid.addItem(main, 1, 1, 1, 1, 0, 100);
		grid.addItem(sideBar, 1, 2, 1, 1, 0, 100);
		return grid;
	}

}
