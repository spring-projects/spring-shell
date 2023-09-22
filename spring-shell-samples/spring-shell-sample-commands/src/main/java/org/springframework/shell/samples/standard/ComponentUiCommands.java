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
package org.springframework.shell.samples.standard;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.ViewComponent;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.InputView;
import org.springframework.shell.component.view.geom.HorizontalAlign;
import org.springframework.shell.component.view.geom.VerticalAlign;
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

}
