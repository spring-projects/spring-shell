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
 * Commands which can be performed by the application or bound to keys in a
 * {@link View}. This class is a placeholder for constants of types usually
 * needed in a views. We've chosen this not to be enumerable so that there
 * would not be restrictions in an api's to use these types.
 *
 * @author Janne Valkealahti
 */
public final class ViewCommand {

	/**
	 * Move line up.
	 *
	 * For example where selection needs to be moved up.
	 */
	public static String LINE_UP = "LineUp";

	/**
	 * Move line down.
	 *
	 * For example where selection needs to be moved down.
	 */
	public static String LINE_DOWN = "LineDown";

	/**
	 * Move focus to the next view.
	 *
	 * For example using tab to navigate into next input field.
	 */
	public static String NEXT_VIEW = "NextView";

	/**
	 * Accepts a current state.
	 */
	public static String ACCEPT = "Accept";

	/**
	 * Deletes the character on the left.
	 */
	public static String DELETE_CHAR_LEFT = "DeleteCharLeft";

	/**
	 * Deletes the character on the right.
	 */
	public static String DELETE_CHAR_RIGHT = "DeleteCharRight";

	/**
	 * Moves the selection left by one.
	 */
	public static String LEFT = "Left";

	/**
	 * Moves the selection righ by one.
	 */
	public static String RIGHT = "Right";

}
