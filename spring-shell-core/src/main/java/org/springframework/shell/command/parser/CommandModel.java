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
package org.springframework.shell.command.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.parser.ParserConfig.Feature;

/**
 * Helper class to make it easier to work with command registrations and how
 * those are used with parser model.
 *
 * @author Janne Valkealahti
 */
public class CommandModel {

	private final Map<String, CommandInfo> rootCommands = new HashMap<>();
	private final ParserConfig configuration;

	public CommandModel(Map<String, CommandRegistration> registrations, ParserConfig configuration) {
		this.configuration = configuration;
		buildModel(registrations);
	}

	@Nullable
	CommandInfo getRootCommand(String command) {
		return rootCommands.get(command);
	}

	CommandInfo resolve(List<String> commands) {
		CommandInfo info = null;
		boolean onRoot = true;
		for (String commandx : commands) {
			String command = configuration.isEnabled(Feature.CASE_SENSITIVE_COMMANDS) ? commandx : commandx.toLowerCase();
			if (onRoot) {
				info = rootCommands.get(command);
				onRoot = false;
			}
			else {
				Optional<CommandInfo> nextInfo = info.getChildren().stream()
					.filter(i -> i.command.equals(command))
					.findFirst();
				if (nextInfo.isEmpty()) {
					break;
				}
				info = nextInfo.get();
			}
		}
		return info;
	}

	Map<String, CommandInfo> getRootCommands() {
		return rootCommands;
	}

	Map<String, Token> getValidRootTokens() {
		Map<String, Token> tokens = new HashMap<>();

		rootCommands.entrySet().forEach(e -> {
			tokens.put(e.getKey(), new Token(e.getKey(), TokenType.COMMAND));
			// Map<String, Token> validTokens = e.getValue().getValidTokens();
			// tokens.putAll(validTokens);
		});

		// rootCommands.entrySet().forEach(e -> {
		// 	tokens.put(e.getKey(), new Token(e.getKey(), TokenType.COMMAND));
		// 	if (e.getValue().registration != null) {
		// 		e.getValue().registration.getAliases().forEach(alias -> {
		// 			String[] commands = alias.getCommand().split(" ");
		// 			tokens.put(commands[0], new Token(commands[0], TokenType.COMMAND));
		// 		});
		// 	}
		// });
		return tokens;
	}

	void xxx(String command, CommandRegistration registration) {
		String[] commands = command.split(" ");
		for (int i = 0; i < commands.length; i++) {

		}
	}

	// root1 sub1
	// root1 sub2

	private CommandInfo getOrCreate(String[] commands, CommandRegistration registration) {
		CommandInfo ret = null;

		CommandInfo parent = null;
		int i = -1;
		for (String command : commands) {
			i++;
			String key = configuration.isEnabled(Feature.CASE_SENSITIVE_COMMANDS) ? command : command.toLowerCase();

			if (i == 0) {
				parent = rootCommands.get(command);
				if (parent == null) {
					parent = new CommandInfo(key, i < commands.length - 1 ? null : registration, parent);
					rootCommands.put(key, parent);
				}
				ret = parent;
				continue;
			}

			CommandInfo children = parent.getChildren(command);
			if (children == null) {
				children = new CommandInfo(key, i < commands.length - 1 ? null : registration, parent);
				parent.addChildred(command, children);
			}
			parent = children;
			ret = parent;
			// CommandInfo asdf = new CommandInfo(key, i < commands.length - 1 ? null : registration, parent);
			// parent.addChildred(command, asdf);
			// parent = asdf;

		}

		if (ret.registration == null) {
			ret.registration = registration;
		}
		return ret;
	}

	private void buildModel(Map<String, CommandRegistration> registrations) {
		registrations.entrySet().forEach(e -> {
			String[] commands = e.getKey().split(" ");
			getOrCreate(commands, e.getValue());

			// String[] commands = e.getKey().split(" ");
			// CommandInfo parent = null;
			// for (int i = 0; i < commands.length; i++) {
			// 	CommandRegistration registration = i + 1 == commands.length ? e.getValue() : null;
			// 	String key = configuration.isCommandsCaseSensitive() ? commands[i] : commands[i].toLowerCase();
			// 	if (parent == null) {
			// 		CommandInfo info = new CommandInfo(commands[i], registration, parent);
			// 		rootCommands.computeIfAbsent(key, command -> info);
			// 		parent = info;
			// 	}
			// 	else {
			// 		CommandInfo info = new CommandInfo(key, registration, parent);
			// 		parent.children.add(info);
			// 		parent = info;
			// 	}
			// }
		});
	}

	/**
	 * Contains info about a command, its registration, its parent and childs.
	 */
	static class CommandInfo {
		String command;
		CommandRegistration registration;
		CommandInfo parent;
		// private List<CommandInfo> children = new ArrayList<>();
		private Map<String, CommandInfo> children = new HashMap<>();

		CommandInfo(String command, CommandRegistration registration, CommandInfo parent) {
			this.registration = registration;
			this.parent = parent;
			this.command = command;
		}

		/**
		 * Get valid tokens for this {@code CommandInfo}.
		 *
		 * @return mapping from raw value to a token
		 */
		Map<String, Token> getValidTokens() {
			Map<String, Token> tokens = new HashMap<>();
			children.values().forEach(commandInfo -> {
				tokens.put(commandInfo.command, new Token(command, TokenType.COMMAND));
			});
			if (registration != null) {
				registration.getOptions().forEach(commandOption -> {
					for (String longName : commandOption.getLongNames()) {
						tokens.put("--" + longName, new Token(longName, TokenType.OPTION));
					}
					for (Character shortName : commandOption.getShortNames()) {
						tokens.put("-" + shortName, new Token(shortName.toString(), TokenType.OPTION));
					}
				});
			}
			return tokens;
		}

		public Collection<CommandInfo> getChildren() {
			return children.values();
		}

		public void addChildred(String command, CommandInfo children) {
			this.children.put(command, children);
		}

		CommandInfo getChildren(String command) {
			return children.get(command);
			// return children.stream()
			// 	.filter(c -> c.command.equals(command))
			// 	.findFirst()
			// 	.orElse(null)
			// 	;
		}
	}
}
