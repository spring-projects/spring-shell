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

import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.MouseHandler;

/**
 * Base interface for all views. Represents a visible element that can render
 * itself and contains zero or more nested {@code Views}.
 *
 * @author Janne Valkealahti
 */
public interface View extends Control {

	void init();

	/**
	 * Sets a layer index this {@code View} operates on.
	 *
	 * @param index the layer index
	 */
	void setLayer(int index);

	/**
	 * Called when {@code View} gets or loses a focus.
	 *
	 * @param view the view receiving focus
	 * @param focus flag if focus is received
	 */
	void focus(View view, boolean focus);

	/**
	 * Gets if this {@code View} has a focus.
	 *
	 * @return true if view has a focus
	 */
	boolean hasFocus();

	/**
	 * Gets a {@link View} mouse {@link MouseHandler}. Can be {@code null} which
	 * indicates view will not handle any mouse events.
	 *
	 * @return a view mouse handler
	 * @see MouseHandler
	 */
	@Nullable
	MouseHandler getMouseHandler();

	/**
	 * Gets a {@link View} key {@link KeyHandler}. Can be {@code null} which
	 * indicates view will not handle any key events.
	 *
	 * @return a view key handler
	 * @see KeyHandler
	 */
	@Nullable
	KeyHandler getKeyHandler();

	/**
	 * Gets a {@link View} hotkey {@link KeyHandler}. Can be {@code null} which
	 * indicates view will not handle any key events.
	 *
	 * @return a view hotkey handler
	 * @see KeyHandler
	 */
	@Nullable
	KeyHandler getHotKeyHandler();

	/**
	 * Sets an {@link EventLoop}.
	 *
	 * @param eventLoop the event loop
	 */
	void setEventLoop(@Nullable EventLoop eventLoop);

	/**
	 * Sets a {@link ViewService}.
	 *
	 * @param viewService the view service
	 */
	void setViewService(ViewService viewService);

	/**
	 * Get supported commands.
	 *
	 * @return supported commands
	 * @see ViewCommand
	 */
	Set<String> getViewCommands();

	/**
	 * Run command.
	 *
	 * @param command the command to run
	 * @return true if command was succesfully dispatched
	 * @see ViewCommand
	 */
	boolean runViewCommand(String command);
}
