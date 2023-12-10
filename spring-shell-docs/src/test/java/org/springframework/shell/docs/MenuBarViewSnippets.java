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

import org.springframework.shell.component.view.control.MenuBarView;
import org.springframework.shell.component.view.control.MenuBarView.MenuBarItem;
import org.springframework.shell.component.view.control.MenuView.MenuItem;
import org.springframework.shell.component.view.control.MenuView.MenuItemCheckStyle;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyEvent.KeyMask;

public class MenuBarViewSnippets {

	class Dump1 {

		@SuppressWarnings("unused")
		void dump1() {
			// tag::snippet1[]
			Runnable quitAction = () -> {};
			Runnable aboutAction = () -> {};
			MenuBarView menuBar = MenuBarView.of(
				MenuBarItem.of("File",
						MenuItem.of("Quit", MenuItemCheckStyle.NOCHECK, quitAction))
					.setHotKey(Key.f | KeyMask.AltMask),
				MenuBarItem.of("Help",
						MenuItem.of("About", MenuItemCheckStyle.NOCHECK, aboutAction))
			);
			// end::snippet1[]
		}

	}
}
