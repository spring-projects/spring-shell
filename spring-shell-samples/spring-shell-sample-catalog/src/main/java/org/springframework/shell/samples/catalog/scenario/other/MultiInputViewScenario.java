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

import org.springframework.shell.component.view.control.GridView;
import org.springframework.shell.component.view.control.InputView;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.screen.Color;
import org.springframework.shell.samples.catalog.scenario.AbstractScenario;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;;

@ScenarioComponent(name = "Multi inputview", description = "Multi InputView sample", category = { Scenario.CATEGORY_OTHER })
public class MultiInputViewScenario extends AbstractScenario {

	@Override
	public View build() {
		GridView grid = new GridView();
		configure(grid);
		grid.setRowSize(1, 1, 1);
		grid.setColumnSize(0);

		InputView input1 = new InputView();
		input1.setBackgroundColor(Color.GREEN);
		configure(input1);

		InputView input2 = new InputView();
		input2.setBackgroundColor(Color.BLUE);
		configure(input2);

		InputView input3 = new InputView();
		input3.setBackgroundColor(Color.RED);
		configure(input3);

		grid.addItem(input1, 0, 0, 1, 1, 0, 0);
		grid.addItem(input2, 1, 0, 1, 1, 0, 0);
		grid.addItem(input3, 2, 0, 1, 1, 0, 0);

		return grid;
	}

}
