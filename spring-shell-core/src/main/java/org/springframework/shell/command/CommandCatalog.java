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
package org.springframework.shell.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

	/**
	 * Default implementation of a {@link CommandCatalog}.
	 */
	static class DefaultCommandCatalog implements CommandCatalog {

		private final Map<String, CommandRegistration> commandRegistrations = new HashMap<>();
		private final Collection<CommandResolver> resolvers = new ArrayList<>();
		private final ShellContext shellContext;

		DefaultCommandCatalog(Collection<CommandResolver> resolvers, ShellContext shellContext) {
			this.shellContext = shellContext;
			if (resolvers != null) {
				this.resolvers.addAll(resolvers);
			}
		}

		@Override
		public void register(CommandRegistration... registration) {
			for (CommandRegistration r : registration) {
				String commandName = r.getCommand();
				commandRegistrations.put(commandName, r);
				for (CommandAlias a : r.getAliases()) {
					commandRegistrations.put(a.getCommand(), r);
				}
			}
		}

		@Override
		public void unregister(CommandRegistration... registration) {
			for (CommandRegistration r : registration) {
				String commandName = r.getCommand();
				commandRegistrations.remove(commandName);
				for (CommandAlias a : r.getAliases()) {
					commandRegistrations.remove(a.getCommand());
				}
			}
		}

		@Override
		public void unregister(String... commandName) {
			for (String n : commandName) {
				commandRegistrations.remove(n);
			}
		}

		@Override
		public Map<String, CommandRegistration> getRegistrations() {
			Map<String, CommandRegistration> regs = new HashMap<>();
			regs.putAll(commandRegistrations);
			for (CommandResolver resolver : resolvers) {
				resolver.resolve().stream().forEach(r -> {
					regs.put(r.getCommand(), r);
				});
			}
			return regs.entrySet().stream()
				.filter(filterByInteractionMode(shellContext))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}

		/**
		 * Filter registration entries by currently set mode. Having it set to ALL or null
		 * effectively disables filtering as as we only care if mode is set to interactive
		 * or non-interactive.
		 */
		private static Predicate<Entry<String, CommandRegistration>> filterByInteractionMode(ShellContext shellContext) {
			return e -> {
				InteractionMode mim = e.getValue().getInteractionMode();
				InteractionMode cim = shellContext != null ? shellContext.getInteractionMode() : InteractionMode.ALL;
				if (mim == null || cim == null || mim == InteractionMode.ALL) {
					return true;
				}
				else if (mim == InteractionMode.INTERACTIVE) {
					return cim == InteractionMode.INTERACTIVE || cim == InteractionMode.ALL;
				}
				else if (mim == InteractionMode.NONINTERACTIVE) {
					return cim == InteractionMode.NONINTERACTIVE || cim == InteractionMode.ALL;
				}
				return true;
			};
		}
	}
}
