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

import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.GridView;

class GridViewSnippets {

	class Dump1 {

		void dump1() {
			// tag::snippet1[]
			GridView grid = new GridView();
			grid.setShowBorders(true);
			grid.setRowSize(0, 0);
			grid.setColumnSize(0, 0);

			grid.addItem(new BoxView(), 0, 0, 1, 1, 0, 0);
			grid.addItem(new BoxView(), 0, 1, 1, 1, 0, 0);
			grid.addItem(new BoxView(), 1, 0, 1, 1, 0, 0);
			grid.addItem(new BoxView(), 1, 1, 1, 1, 0, 0);
			// end::snippet1[]
		}
	}

}
