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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Class representing a registry of {@link Command}s. If defined as a Spring bean, it will
 * be automatically populated by Spring with all available commands.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class CommandRegistry implements SmartInitializingSingleton, ApplicationContextAware {

	private final Set<Command> commands;

	private final CommandTree.Node root = new CommandTree.Node();

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

	public @Nullable Command getCommandByName(String name) {
		return commands.stream().filter(command -> command.getName().equals(name)).findFirst().orElse(null);
	}

	public @Nullable Command lookupCommand(List<String> args) {
		Command command = null;

		CommandTree.Node current = root;
		for (String arg : args) {
			CommandTree.Node next = current.getChild().get(arg);
			if (next == null) {
				return command;
			}
			current = next;

			Command cmd = current.getCommand();
			if (cmd != null) {
				command = cmd;
			}
		}

		return command;
	}

	@Override
	public void afterSingletonsInstantiated() {
		Collection<Command> commandCollection = applicationContext.getBeansOfType(Command.class).values();
		commands.addAll(commandCollection);

		for (Command command : commandCollection) {
			CommandTree.Node current = root;
			String[] names = command.getName().split(" ");
			for (String name : names) {
				current = current.child(name);
			}
			current.setCommand(command);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private static class CommandTree {

		private static class Node {

			private final Map<String, Node> child = new HashMap<>();

			private @Nullable Command command;

			private Map<String, Node> getChild() {
				return child;
			}

			private Node child(String name) {
				return child.computeIfAbsent(name, n -> new Node());
			}

			private @Nullable Command getCommand() {
				return command;
			}

			private void setCommand(Command command) {
				this.command = command;
			}

		}

	}

}
