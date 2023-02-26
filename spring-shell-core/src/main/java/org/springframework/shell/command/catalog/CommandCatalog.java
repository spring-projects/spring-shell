/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.command.catalog;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.shell.command.CommandAlias;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandResolver;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;

/**
 * Interface defining contract to handle existing {@link CommandRegistration}s.
 *
 * @author Janne Valkealahti
 */
public interface CommandCatalog {

	/**
	 * Register a {@link CommandRegistration}.
	 *
	 * @param registration the command registration
	 */
	void register(CommandRegistration... registration);

	/**
	 * Unregister a {@link CommandRegistration}.
	 *
	 * @param registration the command registration
	 */
	void unregister(CommandRegistration... registration);

	/**
	 * Unregister a {@link CommandRegistration} by its command name.
	 *
	 * @param commandName the command name
	 */
	void unregister(String... commandName);

	/**
	 * Gets all {@link CommandRegistration}s mapped with their names.
	 * Returned map is a copy and cannot be used to register new commands.
	 *
	 * @return all command registrations
	 */
	Map<String, CommandRegistration> getRegistrations();

	/**
	 * Gets an instance of a default {@link CommandCatalog}.
	 *
	 * @return default command catalog
	 */
	static CommandCatalog of() {
		return new DefaultCommandCatalog(null, null);
	}

	/**
	 * Gets an instance of a default {@link CommandCatalog}.
	 *
	 * @param resolvers the command resolvers
	 * @param shellContext the shell context
	 * @return default command catalog
	 */
	static CommandCatalog of(Collection<CommandResolver> resolvers, ShellContext shellContext) {
		return new DefaultCommandCatalog(resolvers, shellContext);
	}

}
