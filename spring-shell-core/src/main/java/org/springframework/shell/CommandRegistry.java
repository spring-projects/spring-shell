/*
 * Copyright 2015-2022 the original author or authors.
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
package org.springframework.shell;

import java.util.Map;

/**
 * Implementing this interface allows sub-systems (such as the {@literal help} command) to
 * discover available commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
public interface CommandRegistry {

	/**
	 * Return the mapping from command trigger keywords to implementation.
	 */
	Map<String, MethodTarget> listCommands();

	/**
	 * Register a new command.
	 *
	 * @param name the command name
	 * @param target the method target
	 */
	void addCommand(String name, MethodTarget target);

	/**
	 * Deregister a command.
	 *
	 * @param name the command name
	 */
	void removeCommand(String name);
}
