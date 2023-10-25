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
package org.springframework.shell.samples.catalog.scenario.listview;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.shell.component.view.control.ListView;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.samples.catalog.scenario.AbstractScenario;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;

@ScenarioComponent(name = "Long List", description = "Show many items", category = { Scenario.CATEGORY_LISTVIEW })
public class LongListViewScenario extends AbstractScenario {

	@Override
	public View build() {
		ListView<String> view = new ListView<>();
		configure(view);
		List<String> items = IntStream.of(20).mapToObj(i -> "item" + i).collect(Collectors.toList());
		view.setItems(items);
		return view;
	}

}
