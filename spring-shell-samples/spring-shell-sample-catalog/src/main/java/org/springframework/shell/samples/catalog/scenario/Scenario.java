/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.samples.catalog.scenario;

import org.springframework.lang.Nullable;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.samples.catalog.Catalog;

/**
 * {@link Scenario} participates in a catalog showcase.
 *
 * @author Janne Valkealahti
 */
public interface Scenario {

	// Common category names
	public static final String CATEGORY_ALL = "All Scenarios";
	public static final String CATEGORY_LISTVIEW = "ListView";
	public static final String CATEGORY_PROGRESSVIEW = "ProgressView";
	public static final String CATEGORY_BOXVIEW = "BoxView";
	public static final String CATEGORY_LAYOUT = "Layout";
	public static final String CATEGORY_OTHER = "Other";

	/**
	 * Build a {@link View} to be shown with a scenario. Prefer {@link #buildContext()}
	 * as this method will be deprecated and renamed.
	 *
	 * @return view of a scenario
	 * @see #buildContext()
	 */
	View build();

	/**
	 * Build a {@link ScenarioContext} wrapping needed view and lifecycle methods.
	 *
	 * @return a scenario context
	 */
	default ScenarioContext buildContext() {
		return ScenarioContext.of(build());
	}

	/**
	 * Configure scenario.
	 *
	 * @param ui the terminal ui
	 * @return scenario for chaining
	 */
	Scenario configure(TerminalUI ui);

	/**
	 * Context represents a build {@code View} and lifecycle methods called by a
	 * catalog app.
	 *
	 * For simple {@link View} without any need for lifecycle methods, just call
	 * {@link #of(View)}.
	 */
	public interface ScenarioContext {

		/**
		 * Get a view represented by a {@link Scenario}.
		 *
		 * @return a scenario view
		 */
		View view();

		/**
		 * Called by a {@link Catalog} when its ready to show a {@link View} build from
		 * a {@link Scenario}.
		 */
		void start();

		/**
		 * Called by a {@link Catalog} when its ready to discard a {@link View} build from
		 * a {@link Scenario}.
		 */
		void stop();

		/**
		 * Utility method to build a {@code ScenarioContext} of {@link View} without
		 * lifecycle callbacks.
		 *
		 * @param view the main view
		 * @return a scenario context
		 */
		public static ScenarioContext of(View view) {
			return of(view, null, null);
		}

		/**
		 * Utility method to build a {@code ScenarioContext}.
		 *
		 * @param view the main view
		 * @param start the start callback
		 * @param stop the stop callback
		 * @return a scenario context
		 */
		public static ScenarioContext of(View view, @Nullable Runnable start, @Nullable Runnable stop) {
			return new ScenarioContext() {

				@Override
				public View view() {
					return view;
				}

				@Override
				public void start() {
					if (start != null) {
						start.run();
					}
				}

				@Override
				public void stop() {
					if (stop != null) {
						stop.run();
					}
				}

			};
		}

	}

}
