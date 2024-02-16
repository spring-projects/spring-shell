/*
 * Copyright 2024 the original author or authors.
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

import org.springframework.shell.component.view.control.ProgressView;
import org.springframework.shell.component.view.control.ProgressView.ProgressViewItem;
import org.springframework.shell.geom.HorizontalAlign;

public class ProgressViewSnippets {

	class Dump1 {

		void dump1() {
			// tag::default[]
			ProgressView view = new ProgressView();
			view.start();
			// end::default[]
		}

		void dump2() {
			// tag::allmixedalignandsize[]
			ProgressView view = new ProgressView(
				ProgressViewItem.ofText(10, HorizontalAlign.LEFT),
				ProgressViewItem.ofSpinner(3, HorizontalAlign.LEFT),
				ProgressViewItem.ofPercent(0, HorizontalAlign.RIGHT));
			view.start();
			// end::allmixedalignandsize[]
		}

	}

}
