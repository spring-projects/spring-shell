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
package org.springframework.shell.docs;

import java.util.List;

import org.springframework.shell.component.view.control.StatusBarView;
import org.springframework.shell.component.view.control.StatusBarView.StatusItem;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;

class StatusBarViewSnippets {

	@SuppressWarnings("unused")
	void simple() {
		// tag::simple[]
		StatusItem item1 = new StatusBarView.StatusItem("Item1");
		StatusBarView statusBar = new StatusBarView(List.of(item1));
		// end::simple[]
	}

	void items() {
		// tag::items[]
		StatusItem item1 = StatusBarView.StatusItem.of("Item1");

		Runnable action1 = () -> {};
		StatusItem item2 = StatusBarView.StatusItem.of("Item2", action1);

		Runnable action2 = () -> {};
		StatusItem item3 = StatusBarView.StatusItem.of("Item3", action2, KeyEvent.Key.f10);

		StatusBarView statusBar = new StatusBarView();
		statusBar.setItems(List.of(item1, item2, item3));
		// end::items[]
	}

	void viaArray() {
		// tag::viaarray[]
		new StatusBarView(new StatusItem[] {
			StatusItem.of("Item1"),
			StatusItem.of("Item2")
				.setAction(() -> {}),
			StatusItem.of("Item3")
				.setAction(() -> {})
				.setHotKey(Key.f10)
		});
		// end::viaarray[]
	}

}
