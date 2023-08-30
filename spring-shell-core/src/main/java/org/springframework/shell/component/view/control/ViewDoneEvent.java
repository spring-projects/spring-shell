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
package org.springframework.shell.component.view.control;

/**
 * Spesialisation of a {@link ViewEvent} indicating that a {@link View} is done.
 *
 * @author Janne Valkealahti
 */
public interface ViewDoneEvent extends ViewEvent {

	/**
	 * Create a generic event with empty view args.
	 *
	 * @param view the view
	 * @return a generic view done event
	 */
	static ViewDoneEvent of(View view) {
		return new GenericViewDoneEvent(view, ViewEventArgs.EMPTY);
	}

	record GenericViewDoneEvent(View view, ViewEventArgs args) implements ViewDoneEvent {
	}

}
