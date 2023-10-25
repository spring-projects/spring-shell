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

import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.ButtonView;
import org.springframework.shell.component.view.control.DialogView;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.samples.catalog.scenario.AbstractScenario;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.samples.catalog.scenario.ScenarioComponent;

@ScenarioComponent(name = "Dialog", description = "Modal dialog", category = {
		Scenario.CATEGORY_OTHER })
public class DialogScenario extends AbstractScenario {

	private Runnable dialogAction = () -> {
		DialogView dialog = buildDialog();
		getViewService().setModal(dialog);
	};

	@Override
	public View build() {
		ButtonView button = new ButtonView("Open Dialog", dialogAction);
		configure(button);
		return button;
	}

	private DialogView buildDialog() {
		ButtonView button = new ButtonView("OK");
		configure(button);
		BoxView content = new BoxView();
		content.setDrawFunction((screen, rect) -> {
			screen.writerBuilder().layer(1).build().text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
			return rect;
		});
		DialogView dialog = new DialogView(content, button);
		configure(dialog);
		return dialog;
	}

}
