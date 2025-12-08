/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.core.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Class representing a registry of {@link Command}s. If defined as a Spring bean, it will
 * be automatically populated by Spring with all available commands. Commands can also be
 * registered and unregistered at runtime.
 * <p>
 * Commands are uniquely identified by their name.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class CommandRegistry implements SmartInitializingSingleton, ApplicationContextAware {

	private final Set<Command> commands;

	@SuppressWarnings("NullAway.Init")
	private ApplicationContext applicationContext;

	public CommandRegistry() {
		this.commands = new HashSet<>();
	}

	public CommandRegistry(Set<Command> commands) {
		this.commands = commands;
	}

	public Set<Command> getCommands() {
		return Set.copyOf(commands);
	}

	@Nullable public Command getCommandByName(String name) {
		return commands.stream().filter(command -> command.getName().equals(name)).findFirst().orElse(null);
	}

	public List<Command> getCommandsByPrefix(String prefix) {
		return commands.stream().filter(command -> command.getName().startsWith(prefix)).toList();
	}

	public void registerCommand(Command command) {
		commands.add(command);
	}

	public void unregisterCommand(Command command) {
		commands.remove(command);
	}

	public void clearCommands() {
		commands.clear();
	}

	@Override
	public void afterSingletonsInstantiated() {
		Collection<Command> commandCollection = this.applicationContext.getBeansOfType(Command.class).values();
		commands.addAll(commandCollection);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
