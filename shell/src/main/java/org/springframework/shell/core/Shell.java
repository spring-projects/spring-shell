/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core;

import java.io.File;
import java.util.logging.Level;

import org.springframework.shell.event.ShellStatusProvider;

/**
 * Specifies the contract for an interactive shell.
 *
 * <p>
 * Any interactive shell class which implements these methods can be launched by the roo-bootstrap mechanism.
 *
 * <p>
 * It is envisaged implementations will be provided for JLine initially, with possible implementations for
 * Eclipse in the future.
 *
 * @author Ben Alex
 * @since 1.0
 */
public interface Shell extends ShellStatusProvider, ShellPromptAccessor {

	/**
	 * The slot name to use with {@link #flash(Level, String, String)} if a caller wishes to modify the window title.
	 * This may not be supported by all operating system shells. It is provided on a best-effort basis only.
	 */
	String WINDOW_TITLE_SLOT = "WINDOW_TITLE_SLOT";

	/**
	 * Presents a console prompt and allows the user to interact with the shell. The shell should not return
	 * to the caller until the user has finished their session (by way of a "quit" or similar command).
	 */
	void promptLoop();

	/**
	 * @return null if no exit was requested, otherwise the last exit code indicated to the shell to use
	 */
	ExitShellRequest getExitShellRequest();

	/**
	 * Runs the specified command. Control will return to the caller after the command is run.
	 *
	 * @param line to execute (required)
	 * @return true if the command was successful, false if there was an exception
	 */
	boolean executeCommand(String line);

	/**
	 * Indicates the shell should switch into a lower-level development mode. The exact meaning varies by
	 * shell implementation.
	 *
	 * @param developmentMode true if development mode should be enabled, false otherwise
	 */
	void setDevelopmentMode(boolean developmentMode);

	/**
	 * Displays a progress notification to the user. This notification will ideally be displayed in a
	 * consistent screen location by the shell implementation.
	 *
	 * <p>
	 * An implementation may allow multiple messages to be displayed concurrently. So an implementation can
	 * determine when a flash message replaces a previous flash message, callers should allocate a unique
	 * "slot" name for their messages. It is suggested the class name of the caller be used. This way a
	 * slot will be updated without conflicting with flash message sequences from other slots.
	 *
	 * <p>
	 * Passing an empty string in as the "message" indicates the slot should be cleared.
	 *
	 * <p>
	 * An implementation need not necessarily use the level or slot concepts. They are expected to be
	 * used in most cases, though.
	 *
	 * @param level the importance of the message (cannot be null)
	 * @param message to display (cannot be null, but may be empty)
	 * @param slot the identification slot for the message (cannot be null or empty)
	 */
	void flash(Level level, String message, String slot);

	boolean isDevelopmentMode();

	/**
	 * Changes the "path" displayed in the shell prompt. An implementation will ensure this path is
	 * included on the screen, taking care to merge it with the product name and handle any special
	 * formatting requirements (such as ANSI, if supported by the implementation).
	 *
	 * @param path to set (can be null or empty; must NOT be formatted in any special way eg ANSI codes)
	 */
	void setPromptPath(String path);

	void setPromptPath(String path, boolean overrideStyle);

	/**
	 * Returns the home directory of the current running shell instance
	 *
	 * @return the home directory of the current shell instance
	 */
	File getHome();
}