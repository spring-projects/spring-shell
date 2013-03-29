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

import org.springframework.shell.event.ParseResult;

/**
 * Strategy interface to permit the controlled execution of methods.
 *
 * <p>
 * This interface is used to enable a {@link Shell} to execute methods in a consistent, system-wide
 * manner. A typical use case is to ensure user interface commands are not executed concurrently
 * when other background threads are performing certain operations.
 *
 * @author Ben Alex
 * @since 1.0
 *
 */
public interface ExecutionStrategy {

	/**
	 * Executes the method indicated by the {@link ParseResult}.
	 *
	 * @param parseResult that should be executed (never presented as null)
	 * @return an object which will be rendered by the {@link Shell} implementation (may return null)
	 * @throws RuntimeException which is handled by the {@link Shell} implementation
	 */
	Object execute(ParseResult parseResult) throws RuntimeException;

	/**
	 * Indicates commands are able to be presented. This generally means all important
	 * system startup activities have completed.
	 *
	 * @return whether commands can be presented for processing at this time
	 */
	boolean isReadyForCommands();

	/**
	 * Indicates the execution runtime should be terminated. This allows it to cleanup before returning
	 * control flow to the caller. Necessary for clean shutdowns.
	 */
	void terminate();
}
