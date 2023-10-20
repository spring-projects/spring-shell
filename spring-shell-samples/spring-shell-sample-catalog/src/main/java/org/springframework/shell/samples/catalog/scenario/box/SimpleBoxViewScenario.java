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
package org.springframework.shell.samples.catalog.scenario.box;

import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.screen.Color;
import org.springframework.shell.component.view.screen.ScreenItem;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.samples.catalog.scenario.AbstractScenario;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;

@ScenarioComponent(name = "Simple boxview", description = "BoxView color and style", category = {
		Scenario.CATEGORY_BOXVIEW })
public class SimpleBoxViewScenario extends AbstractScenario {

	@Override
	public View build() {
		BoxView box = new BoxView();
		configure(box);
		box.setTitle("Title");
		box.setShowBorder(true);
		box.setBackgroundColor(Color.KHAKI4);
		box.setTitleColor(Color.RED);
		box.setTitleStyle(ScreenItem.STYLE_BOLD | ScreenItem.STYLE_ITALIC);
		box.setTitleAlign(HorizontalAlign.CENTER);
		return box;
	}
}
