/*
 * Copyright 2017-2022 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;

/**
 * A {@link CommandRegistry} that supports registration of new commands.
 *
 * <p>Makes sure that no two commands are registered with the same name.</p>
 *
 * @author Eric Bottard
 */
public class ConfigurableCommandRegistry implements CommandRegistry {

	private final ShellContext shellContext;
	private Map<String, MethodTarget> commands = new HashMap<>();

	public ConfigurableCommandRegistry(ShellContext shellContext) {
		this.shellContext = shellContext;
	}

	@Override
	public Map<String, MethodTarget> listCommands() {
		return commands.entrySet().stream()
				.filter(e -> {
					InteractionMode mim = e.getValue().getInteractionMode();
					InteractionMode cim = shellContext.getInteractionMode();
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
				})
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	public void register(String name, MethodTarget target) {
		MethodTarget previous = commands.get(name);
		if (previous != null) {
			throw new IllegalArgumentException(
				String.format("Illegal registration for command '%s': Attempt to register both '%s' and '%s'", name, target, previous));
		}
		commands.put(name, target);
	}
}
