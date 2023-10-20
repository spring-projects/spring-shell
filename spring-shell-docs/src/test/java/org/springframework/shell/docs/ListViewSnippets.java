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

import org.springframework.shell.component.view.control.ListView;
import org.springframework.shell.component.view.control.ListView.ItemStyle;
import org.springframework.shell.component.view.control.cell.AbstractListCell;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.shell.geom.Rectangle;

public class ListViewSnippets {

	class Dump1 {

		void dump1() {
			// tag::snippet1[]
			ListView<String> view = new ListView<>();
			view.setItems(List.of("item1", "item2"));
			// end::snippet1[]
		}

		@SuppressWarnings("unused")
		void dump2() {
			// tag::snippet2[]
			ListView<String> view = new ListView<>(ItemStyle.CHECKED);
			// end::snippet2[]
		}
	}

	// tag::listcell[]
	record ExampleData(String name) {
	};

	static class ExampleListCell extends AbstractListCell<ExampleData> {

		public ExampleListCell(ExampleData item) {
			super(item);
		}

		@Override
		public void draw(Screen screen) {
			Rectangle rect = getRect();
			Writer writer = screen.writerBuilder().style(getStyle()).build();
			writer.text(getItem().name(), rect.x(), rect.y());
			writer.background(rect, getBackgroundColor());
		}
	}
	// end::listcell[]

	class Dump2 {

		void dump1() {
			// tag::uselistcell[]
			ListView<ExampleData> view = new ListView<>();
			view.setCellFactory((list, item) -> new ExampleListCell(item));
			// end::uselistcell[]
		}
	}

}
