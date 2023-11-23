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

import org.springframework.lang.Nullable;

/**
 * Provides services for a {@link View} like handling modals.
 *
 * @author Janne Valkealahti
 */
public interface ViewService {

	/**
	 * Gets a current modal view.
	 *
	 * @return current modal view
	 */
	@Nullable
	View getModal();

	/**
	 * Sets a new modal view. Setting modal to {@code null} clears existing modal.
	 *
	 * @param view a view to use as modal
	 */
	void setModal(@Nullable View view);

	/**
	 * Sets a view to be focused. Setting focus to {@code null} clears focused view.
	 *
	 * @param view a view to be focused
	 */
	void setFocus(@Nullable View view);
}
